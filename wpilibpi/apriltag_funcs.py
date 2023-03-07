# The following defines the code for finding AprilTags, getting Pose estimation and then using the tranlsation and
# rotation of the pose estimations to calculate locations of game pieces on the field.  This information can then be used
# to determine if the game piece is present or not.
#
# See the following for inspiration
#   From https://github.com/AprilRobotics/apriltag/wiki/AprilTag-User-Guide#python
#   From https://github.com/churrobots/vision2023/blob/main/app.py
#   Modified to use robotbpy_apriltag Detector instead of pupil_apriltags
#   Also see https://gist.github.com/lobrien/5d5e1b38e5fd64062c43ac752b74889c
#
#   Calibration: https://docs.opencv.org/4.7.0/d9/d0c/group__calib3d.html
#   projectPoints: https://docs.opencv.org/4.7.0/d9/d0c/group__calib3d.html#ga1019495a2c8d1743ed5cc23fa0daff8c
#
#   Coordinate System:
#   x-axis to the right, y-axis down, and z-axis into the tag

import json
import logging
import math
import time
from collections import OrderedDict

import cv2
import numpy as np
import robotpy_apriltag
from ntcore import NetworkTableInstance
from numpy import ndarray  # for type def
from robotpy_apriltag import AprilTagDetection, AprilTagFieldLayout, AprilTagField, loadAprilTagLayoutField
from wpimath.geometry import Pose3d, Rotation3d, Transform3d, Translation3d

from calibration import CameraCalibration, cam0, image_cal  # bandaid for now

global field
field = loadAprilTagLayoutField(AprilTagField.k2023ChargedUp)
print(field)

# from gamepiece_loc import GamePieceFieldResults
log = logging.getLogger(__name__)
log.addHandler(logging.NullHandler())

global apriltagNT
# this is what holds the apriltag locations
apriltagNT = [
    {
        "ID": idx,
        "ambiguity": 0.0,
        "pose": {
            "translation": {"x": 0, "y": 0, "z": 0},
            "rotation": {"quaternion": {"W": 0.0, "X": 0.0, "Y": 0.0, "Z": 0.0}},
        },
    }
    for idx in range(1, 8 + 1)
]


##########
# roi_map units = inches.  gets converted to meters in project_gamepiece_locations()
# roi_map element is X,Y,Z,W,H
# for some reason the coordinate system seems to be:
# (need to verify -- document probably somewhere in FRC information)
# X horizontal, Y vertical (negative is up), Z is distance from plane created by tag
# W is total width of detection area (see create_roi_rect)
# H is total height of detection area (see create_roi_rect)
##########
global roi_map
roi_map = np.float32(
    [
        [-25.62, 12.00, -12.00, 12, 4],  # low cone left   [0,0]
        [-22.00, -15.84 + 6, 8.43, 2, 4],  # mid cone left   [0,1]
        [-22.00, -27.84 + 6, 25.45, 2, 4],  # high cone left  [0,2]
        [0.00, 12.00, -12.00, 12, 4],  # low cube mid    [1,0]
        [0.00, -6.95 - 1, 6.00, 6, 6],  # mid cube mid    [1,1]
        [0.00, -18.22 - 1, 23.31, 6, 6],  # high cube mid   [1,2]
        [25.62, 12.00, -12.00, 12, 4],  # low cone right  [2,0]
        [22.00, -15.84 + 6, 8.43, 2, 4],  # mid cone right  [2,1]
        [22.00, -27.84 + 6, 25.45, 2, 4],  # high cone right [2,2]
    ]
)


def create_roi_rect(roi):
    """takes a roi row (X,Y,Z,W,H), and creates a rectangle
    centered at X,Y,Z, having width -W and height -H
    """
    x, y, z, W, H = roi
    w = W / 2.0
    h = H / 2.0

    return np.float32([[x - w, y - h, z], [x - w, y + h, z], [x + w, y + h, z], [x + w, y - h, z]])


# CALIBRATION data from one of the global shutter cameras on my laptop
#             6.0/39.37, # 6" tags for FRC2023, 39.37 inches/meter
#             Fx,#1028.90904278, #  (Fx)
#             Fy,#1028.49276415, #  (Fy)
#             Cx,#638.57001085,  #  (Cx) should be roughly 1/2 the img width
#             Cy,#337.36382032,  #  (Cy) should be roughly 1/2 the img height
# {TagCornerToObjectPoint(-3_in, -3_in, *tagPose),
#                      TagCornerToObjectPoint(+3_in, -3_in, *tagPose),
#                      TagCornerToObjectPoint(+3_in, +3_in, *tagPose),
#                      TagCornerToObjectPoint(-3_in, +3_in, *tagPose)}


def create_tag_cornerlocs(pose: Pose3d):
    """takes a roi row (X,Y,Z,W,H), and creates a rectangle
    centered at X,Y,Z, having width W and height H
    According to robotpy_apriltag, corners are defined in this order:(-1,1), (1,1), (1,-1), and (-1,-1)
    """
    x, y, z = np.array(pose.translation())
    w = 3 / 39.37
    h = 3 / 39.37

    # return np.float32([[x - w, y + h, z], [x + w, y + h, z], [x + w, y - h, z], [x - w, y - h, z]])
    return np.float32([[x - w, y - h, z], [x + w, y - h, z], [x + w, y + h, z], [x - w, y + h, z]])


# std::optional<std::array<cv::Point3d, 4>> detail::CalcTagCorners(
#     int tagID, const frc::AprilTagFieldLayout& aprilTags) {
#   if (auto tagPose = aprilTags.GetTagPose(tagID); tagPose.has_value()) {
#     return std::array{TagCornerToObjectPoint(-3_in, -3_in, *tagPose),
#                       TagCornerToObjectPoint(+3_in, -3_in, *tagPose),
#                       TagCornerToObjectPoint(+3_in, +3_in, *tagPose),
#                       TagCornerToObjectPoint(-3_in, +3_in, *tagPose)};
#   } else {
#     return std::nullopt;
#   }
# }

# cv::Point3d detail::ToPoint3d(const frc::Translation3d& translation) {
#   return cv::Point3d(-translation.Y().value(), -translation.Z().value(),
#                      +translation.X().value());
# }

# cv::Point3d detail::TagCornerToObjectPoint(units::meter_t cornerX,
#                                            units::meter_t cornerY,
#                                            frc::Pose3d tagPose) {
#   frc::Translation3d cornerTrans =
#       tagPose.Translation() +
#       frc::Translation3d(0.0_m, cornerX, cornerY).RotateBy(tagPose.Rotation());
#   return ToPoint3d(cornerTrans);
# }
def tag_corner_to_objectPoint(x, y, pose: Pose3d):
    cornerTrans = pose.translation() + Translation3d(0, x / 39.37, y / 39.37).rotateBy(pose.rotation())
    return [-cornerTrans.y, -cornerTrans.z, cornerTrans.x]


def create_field_cornerlocs(tag_id, pose: Pose3d):
    fieldpose = field.getTagPose(tag_id)
    ret = [
        tag_corner_to_objectPoint(-3, -3, fieldpose),
        tag_corner_to_objectPoint(+3, -3, fieldpose),
        tag_corner_to_objectPoint(+3, +3, fieldpose),
        tag_corner_to_objectPoint(-3, +3, fieldpose),
    ]
    return ret


class AprilTagPipeline:
    _MATCH_TAGMAP = {"RED": [1, 2, 3], "BLUE": [6, 7, 8], "ALL": [1, 2, 3, 6, 7, 8]}
    DETECTION_MARGIN_THRESHOLD = 100
    POSE_SOLVER_ITERATIONS = 200
    _match_tags = []  # holds the _MATCH_TAGMAP currently in use
    NTvar = NetworkTableInstance.getDefault().getTable("gridcam").getStringTopic("tags").publish()

    def __init__(self, frame_size, Fx, Fy, Cx, Cy, alliance="RED"):
        """AprilTagPipeline class
        NETWORK TABLES PARAMETERS ( will effect all instances of AprilTagPipeline)
            DETECTION_MARGIN_THRESHOLD
            POSE_SOLVER_ITERATIONS (see AprilTagPoseEstimator.estimateOrthogonalIteration())
        """
        self.detector = robotpy_apriltag.AprilTagDetector()
        self.detector.addFamily("tag16h5")
        # TODO: do these config values need to be exposed to NetworkTables?
        self.detector.Config.numThreads = 1  # default value = 1
        self.detector.Config.decodeSharpening = 0.25  # default value = 0.25
        self.detector.Config.quadDecimate = 1.0  # default value = 2
        # Notes for AprilTagPoseEstimator.Config
        # From https://github.com/AprilRobotics/apriltag/wiki/AprilTag-User-Guide#python
        # fx, fy: The camera's focal length (in pixels).
        #   For most cameras fx and fy will be equal or nearly so.
        # cx, cy: The camera's focal center (in pixels).
        #   For most cameras this will be approximately the same as the image center.
        # these values come from the 3x3 cameramatrix when doing calibration in opencv:
        # matrix =  | Fx   0  Cx |
        #           |  0  Fy  Cy |
        #           |  0   0   1 |
        # For camera calibration information, see https://docs.opencv.org/4.x/dc/dbb/tutorial_py_calibration.html
        # Also see cameravisionclass
        self.estimator_config = robotpy_apriltag.AprilTagPoseEstimator.Config(6.0 / 39.37, Fx, Fy, Cx, Cy)
        self.estimator = robotpy_apriltag.AprilTagPoseEstimator(self.estimator_config)
        self.results = []
        self.black_frame = np.zeros(shape=(720, 1280, 3), dtype="uint8")
        self.match_tags = alliance  # defined as a property vs. method

    @property
    def match_tags(self):
        return self._match_tags

    @match_tags.setter
    def match_tags(self, alliance):
        if alliance in self._MATCH_TAGMAP.keys():
            self._match_tags = self._MATCH_TAGMAP[alliance]

    def to_dict(self):
        """
        Format AprilTags to json
        TODO: Is it possible that both cameras see the same tag, and therefore we
              need to chose the tag pose with lower ambiguity?
        """
        # gamepieceNT is defined as a global near top of this file
        # TODO: is dataclass a better solution?
        # gamepieceNT = [{'ID':idx,'ambiguity':0.0,'pose':{'translation':{'x':0,'y':0,'z':0},'rotation':{'quaternion':{'W':0.0,'X':0.0,'Y':0.0,'Z':0.0}}}} for idx in range(1,8+1)]
        for result in self.results:
            pose, tag, est = result
            tag_id = tag.getId()
            tvec = pose.translation()
            rqua = pose.rotation().getQuaternion()
            if tag_id in apriltagNT:
                ret = apriltagNT[tag_id]
                ret["pose"]["translation"] = {"x": tvec.x, "y": tvec.y, "z": tvec.z}
                ret["pose"]["rotation"]["quaternion"] = {"W": rqua.W(), "X": rqua.X(), "Y": rqua.Y(), "Z": rqua.Z()}
                ret["ambiguity"] = est.getAmbiguity()
        return apriltagNT

    def process_apriltag(self, tag: AprilTagDetection):
        """
        process a single tag, including estimating pose

        Args:
            tag: AprilTagDetection object

        Returns:
          pose(Transform3d)
          tag object
          pose estimate object
        """
        # tag_id = tag.getId()
        # center = tag.getCenter()
        # hamming = tag.getHamming()
        # decision_margin = tag.getDecisionMargin()
        est = self.estimator.estimateOrthogonalIteration(tag, self.POSE_SOLVER_ITERATIONS)
        return est.pose1, tag, est

    def adjust_PoseEstimate(self, cal):
        # TODO: NOT WORKING YET
        # might be able to utilize https://docs.opencv.org/4.x/d5/d1f/calib3d_solvePnP.html to further refine the pose estimate
        # by using the pose estimate of multiple apriltags
        # solvePnP(	objectPoints, imagePoints, cameraMatrix, distCoeffs[, rvec[, tvec[, useExtrinsicGuess[, flags]]]]	) ->	retval, rvec, tvec
        # see https://github.com/PhotonVision/photonvision/blob/dcd917870cece9e34eb32bf7ddc068d52dc5aefa/photon-lib/src/main/native/cpp/photonlib/PhotonPoseEstimator.cpp#L373
        objpts = []
        imgpts = []
        if len(self.results) > 2:  # if reading documentation right, SOLVEPNP_SQPNP requires 3 tags
            for idx, result in enumerate(self.results):
                pose, tag, est = result
                # objpts.append(np.array(pose.translation()))
                # objpts.append(create_tag_cornerlocs(pose))
                objpts.append(create_field_cornerlocs(tag.getId(), pose))
                tmp = np.zeros(8, dtype="float")
                tmp = tag.getCorners(tmp)
                tmp = np.array(tmp)
                tmp = tmp.reshape(-1, 2)
                imgpts.append(tmp)
                print(f"\t{idx:03d}:[{pose.translation()}]\t[{pose.rotation()}]")
                # pts = [(pose,tag.getCorners()) for pose,tag,_ in self.results]
            # log.info(f"{np.concatenate(objpts)},{np.concatenate(imgpts)}")
            _, rvec_multi, tvec_multi = cv2.solvePnP(
                np.concatenate(objpts), np.concatenate(imgpts), cal.mtx, cal.dst, flags=cv2.SOLVEPNP_SQPNP
            )
            p = Pose3d(*tvec_multi.ravel(), Rotation3d(*rvec_multi.ravel()))
            print(f"\tALL:[{p.translation()}*39.37]\t[{p.rotation()}]")
            # log.info(f"\tALL::Translation[{tvec_multi.ravel()*39.37}]\tRotation[{rvec_multi.ravel()}]")
            return rvec_multi
        return None

    def runPipeline(self, frame: ndarray, cal: CameraCalibration):
        """
        this function should be called by main loop for AprilTags processing

        results are stored in self.results which is a python list

        Args:
            frame: raw image from camera
            cal: CameraCalibration object
        Returns:
            frame: image with drawn features added
            roipts: projected locations of gamepieces based on tag pose
        """
        roipts = []
        if frame is None:
            log.error("runPipeline called with no frame")
            return self.black_frame, []
        try:
            # Convert the frame to grayscale
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            # Detect apriltag
            tags = self.detector.detect(gray)
            # filter detected tags by DecisionMargin
            filter_tags = [tag for tag in tags if tag.getDecisionMargin() > self.DETECTION_MARGIN_THRESHOLD]
            log.debug(f"Num Filtered Tags={len(filter_tags)}")
            self.results = [self.process_apriltag(tag) for tag in filter_tags]
            _ = self.adjust_PoseEstimate(cal)
            # NOT WORKING YET self.adjust_PoseEstimate(cal)

            for idx, result in enumerate(self.results):
                pose, tag, est = result
                tag_id = tag.getId()
                amb = est.getAmbiguity()
                log.debug(f"Result[{idx}]\tAmbiguity: {amb:0.4f}\tError: {est.error1:3f}")

                frame = draw_tagframe(frame, tag)
                frame = draw_tagid(frame, tag)
                frame = draw_cube(frame, result, cal)
                frame = draw_tagpose(frame, pose, tag)

                # we only care about these tags for gamepiece detection.
                if tag_id in self.match_tags:
                    frame, pts = project_gamepiece_locations(roi_map, frame, result, cal)
                    roipts.append((tag_id, pts))

            log.info(roipts)
            return frame, roipts
        except Exception as e:
            log.error(e)
            self.results = []
            return self.black_frame, []


# Draw the TagID and Pose of the Tag
def draw_tagid(frame: ndarray, tag: AprilTagDetection):
    """draw the tagId on the image"""
    tagId = tag.getId()
    ptA = tag.getCorner(0)
    ptA = (int(ptA.x), int(ptA.y))
    # putText(img, text, org, fontFace, fontScale, color[, thickness[, lineType[, bottomLeftOrigin]]])
    cv2.putText(frame, f"id {tagId}", (ptA[0], ptA[1] - 15), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (0, 0, 0), 5)
    cv2.putText(frame, f"id {tagId}", (ptA[0], ptA[1] - 15), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (255, 255, 255), 1)
    return frame


def draw_tagpose(frame: ndarray, pose: Pose3d, tag: AprilTagDetection):
    """draw XYZ, Roll,Pitch,Yaw on the image

    Args:
        frame:ndarray numpy array [width,height,3]
        pose: Transforme3d
        tag: AprilTagDetection
    """
    ptA = tag.getCorner(0)
    ptA = (int(ptA.x), int(ptA.y))
    t = pose.translation()  # convert to inches
    r = pose.rotation()
    msg = [
        f"X={39.37*t.x:0.2f}",
        f"Y={39.37*t.y:0.2f}",
        f"Z={39.37*t.z:0.2f}",
        f"Roll={r.z_degrees:0.2f}",
        f"Pitch={r.x_degrees:0.2f}",
        f"Yaw={r.y_degrees:0.2f}",
    ]
    for idx, m in enumerate(msg):
        cv2.putText(frame, m, (ptA[0] + 100, (ptA[1] - 120) + 20 * (idx)), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 0), 3)
        cv2.putText(
            frame, m, (ptA[0] + 100, (ptA[1] - 120) + 20 * (idx)), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1
        )
    return frame


def draw_tagframe(frame: ndarray, tag: AprilTagDetection):
    """draw tag outline on the image"""
    ptA, ptB, ptC, ptD = tag.getCorner(0), tag.getCorner(1), tag.getCorner(2), tag.getCorner(3)
    ptA = (int(ptA.x), int(ptA.y))
    ptB = (int(ptB.x), int(ptB.y))
    ptC = (int(ptC.x), int(ptC.y))
    ptD = (int(ptD.x), int(ptD.y))
    cv2.line(frame, ptA, ptB, (255, 255, 255), 2)
    cv2.line(frame, ptB, ptC, (255, 255, 255), 2)
    cv2.line(frame, ptC, ptD, (255, 255, 255), 2)
    cv2.line(frame, ptD, ptA, (255, 255, 255), 2)
    return frame


def project_gamepiece_locations(
    roi_map: ndarray, frame: ndarray, result: tuple, cal: CameraCalibration, draw_roi=(0, 255, 0)
):
    """
    Function that transforms roi rectangles based on AprilTag pose and camera calibration values

    Args:
        roi_map: a list of 3x3 np.float32 arrays
        frame: current image being processed
        result: current AprilTag result
        cal: CameraCalibration object
        draw_roi: color to use for roi rectangles.
                    If None, rectangle are not drawn
    Returns:
        frame: image
        roi_rects: transformed roi_map in pixel coordinates
    """
    if draw_roi == None:
        return
    pose, tag, est = result
    tag_id = tag.getId()
    if tag_id not in [1, 2, 3, 6, 7, 8]:
        return
    tvec = np.array(pose.translation())
    rvec = pose.rotation().getQuaternion().toRotationVector()
    ret = []
    for roi in roi_map:  # iterate over each row in the array
        rect = create_roi_rect(roi)
        rect /= 39.37
        # using the CameraCalibration class to automatically pull in the calibration values needed for cv2.projectPoints
        # otherwise, the direct call to opencv is: imgpts,jac = cv2.projectPoints(rect, rvec, tvec,cal_factors.mtx, cal_factors.dst)
        imgpts, _ = cal.projectPoints(rect, rvec, tvec)
        imgpts = np.int32(imgpts).reshape(-1, 2)
        ret.append([*imgpts[0], *imgpts[2]])
        for i in range(4):
            j = (i + 1) % 4
            frame = cv2.line(frame, tuple(imgpts[i]), tuple(imgpts[j]), draw_roi, 2)
    return frame, ret


def draw_cube(frame: ndarray, result: tuple, cal: CameraCalibration):
    """draws a 3d cube over the apriltag to visually show pose"""
    CUBE = np.float32(
        [[-1, -1, 0], [-1, 1, 0], [1, 1, 0], [1, -1, 0], [-1, -1, -2], [-1, 1, -2], [1, 1, -2], [1, -1, -2]]
    )
    cube_pts = 3 / 39.37 * CUBE
    pose, tag, _ = result
    tvec = np.array(pose.translation())
    rvec = pose.rotation().getQuaternion().toRotationVector()
    imgpts, jac = cv2.projectPoints(cube_pts, rvec, tvec, cal.mtx, cal.dst)
    imgpts = np.int32(imgpts).reshape(-1, 2)
    # draw ground floor in green
    # frame = cv2.drawContours(frame, [imgpts[:4]],-1,(0,255,0),-3)
    # draw pillars in blue color
    for i, j in zip(range(4), range(4, 8)):
        frame = cv2.line(frame, tuple(imgpts[i]), tuple(imgpts[j]), (255), 3)
    # draw top layer in red color
    frame = cv2.drawContours(frame, [imgpts[4:]], -1, (0, 0, 255), 3)
    return frame

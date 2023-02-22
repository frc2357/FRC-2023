# The following defines the code for finding AprilTags, getting Pose estimation and then using the tranlsation and 
# rotation pose estimations to calculate locations of game pieces on the field.  This information can then be used
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

from collections import OrderedDict
import cv2
import robotpy_apriltag
from wpimath.geometry import Transform3d,Rotation3d
import time
import math
from robotpy_apriltag import AprilTagFieldLayout
import math
import numpy as np
from calibration import image_cal, cam0 #bandaid for now
import logging
log = logging.getLogger(__name__)
log.addHandler(logging.NullHandler())

##########
#roi_map units = inches.  gets converted to meters in project_gamepiece_locations()
#roi_map element is X,Y,Z,W,H
# for some reason the coordinate system seems to be:
# (need to verify -- document probably somewhere in FRC information)
# X horizontal, -Y vertical, Z is distance from plane created by tag
# W is total width of detection area (see create_roi_rect)
# H is total height of detection area (see create_roi_rect)
##########
roi_map = np.float32([[ 22.00,-15.84,  8.43,  2, 2], #mid cone
                      [-22.00,-15.84,  8.43,  2, 2], #mid cone
                      [ 22.00,-27.84, 25.45,  2, 2], #high cone
                      [-22.00,-27.84, 25.45,  2, 2], #high cone
                      [ 25.62, 12.00,-12.00, 12, 4], #low cone
                      [-25.62, 12.00,-12.00, 12, 4], #low cone
                      [  0.00, 12.00,-12.00, 12, 4], #low cube
                      [  0.00, -6.95,  6.00,  4, 4], #mid cube
                      [  0.00,-18.22, 23.31,  4, 4], #high cube
                      ])
                      

def create_roi_rect(roi):
    """ takes a roi row (X,Y,Z,W,H), and creates a rectangle
        centered at X,Y,Z, having width -W and height -H
    """
    x,y,z,W,H= roi 
    w = W/2.
    h = H/2

    return np.float32([[x-w,y-h,z],
                       [x-w,y+h,z],
                       [x+w,y+h,z],
                       [x+w,y-h,z]])

# CALIBRATION data from one of the global shutter cameras on my laptop
#             6.0/39.37, # 6" tags for FRC2023, 39.37 inches/meter
#             Fx,#1028.90904278, #  (Fx)
#             Fy,#1028.49276415, #  (Fy)
#             Cx,#638.57001085,  #  (Cx) should be roughly 1/2 the img width
#             Cy,#337.36382032,  #  (Cy) should be roughly 1/2 the img height



class AprilTagPipeline:
    def __init__(self, frame_size, Fx,Fy,Cx,Cy):
        """ AprilTagPipeline class 
        """
        self.detector = robotpy_apriltag.AprilTagDetector()
        self.detector.addFamily("tag16h5")
        # Notes for AprilTagPoseEstimator.Config
        #From https://github.com/AprilRobotics/apriltag/wiki/AprilTag-User-Guide#python
        #fx, fy: The camera's focal length (in pixels). 
        #   For most cameras fx and fy will be equal or nearly so.
        #cx, cy: The camera's focal center (in pixels). 
        #   For most cameras this will be approximately the same as the image center.
        # these values come from the 3x3 cameramatrix when doing calibration in opencv:
        # matrix =  | Fx   0  Cx |
        #           |  0  Fy  Cy |
        #           |  0   0   1 |  
        # For camera calibration information, see https://docs.opencv.org/4.x/dc/dbb/tutorial_py_calibration.html
        # Also see cameravisionclass      
        self.estimator_config = robotpy_apriltag.AprilTagPoseEstimator.Config(6.0/39.37,Fx,Fy,Cx,Cy)
        self.estimator = robotpy_apriltag.AprilTagPoseEstimator(self.estimator_config)
        self.results = []
        self.black_frame = np.zeros(shape=(720,1280,3),dtype='uint8')

    def to_json(self):
        """
            Format AprilTags to json
            TODO: Is it possible that both cameras see the same tag, and therefore we
                  need to chose the tag pose with lower ambiguity?
        """
        ret_list = [{'ID':idx,'ambiguity':0.0,'pose':{'translation':{'x':0,'y':0,'z':0},'rotation':{'quaternion':{'W':0.0,'X':0.0,'Y':0.0,'Z':0.0}}}} for idx in range(1,8+1)]
        for result in self.results:
            tag_id, pose, center, tag, est = result
            tvec = pose.translation()
            rqua = pose.rotation().getQuaternion()

            ret = ret_list[tag_id]
            ret['pose']['translation']= {'x':tvec.x,'y':tvec.y,'z':tvec.z}
            ret['pose']['rotation']['quaternion'] = {'W':rqua.W(),'X':rqua.X(),'Y':rqua.Y(),'Z':rqua.Z()}
            ret['ambiguity'] = est.getAmbiguity()
        return {'timestamp':0.0,'tags':ret_list}
            
    def process_apriltag(self, tag):
        """
            process a single tag, including estimating pose
            returns id, pose(Transform3d), tag center (pixels), tag object, pose estimate object
        """
        tag_id = tag.getId()
        center = tag.getCenter()
        hamming = tag.getHamming()
        decision_margin = tag.getDecisionMargin()
        EST_ITERATIONS = 125
        est = self.estimator.estimateOrthogonalIteration(tag, EST_ITERATIONS)
        return tag_id, est.pose1, center, tag, est

    def runPipeline(self, frame, cal):
        """
            this function should be called by main loop for AprilTags processing
            results are stored in self.results list
            inputs:
                frame: raw image from camera
                cal: CameraCalibration object
            returns:
                frame: image with drawn features added
                roipts: projected locations of gamepieces based on tag pose
        """
        roipts = []
        if frame is None:
            log.error("runPipeline called with no frame")
            return self.black_frame,[]
        try:
            # Convert the frame to grayscale
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            # Detect apriltag
            tag_info = self.detector.detect(gray)
            DETECTION_MARGIN_THRESHOLD = 100
            filter_tags = [tag for tag in tag_info if tag.getDecisionMargin() > DETECTION_MARGIN_THRESHOLD]
            log.debug(f"Num Filtered Tags={len(filter_tags)}")
            self.results = [ self.process_apriltag(tag) for tag in filter_tags ]
            
            for idx,result in enumerate(self.results):
                tag_id, pose, center, tag, est = result
                amb = est.getAmbiguity()
                log.debug(f"Result[{idx}]\tAmbiguity: {amb:0.4f}\tError: {est.error1:3f}")
                if tag_id not in [1,2,3,6,7,8]: # we only care about these tags.  We might only care about 1,2,3 or 6,7,8 depending on match        
                    continue
                frame = draw_tagframe(frame, tag)
                frame = draw_tagid(frame, tag)
                #frame = draw_tagpose(frame, pose, tag)
                frame ,roipts = project_gamepiece_locations(roi_map, frame, result, cal)   
                #log.debug(roipts)   
            return frame,roipts
        except Exception as e:
            log.error(e,)
            self.results = []
            return self.black_frame,[]

# Draw the TagID and Pose of the Tag
def draw_tagid(frame, tag):
    """ draw the tagId and pose of the tag on the image"""
    tagId = tag.getId()
    ptA = tag.getCorner(0)
    ptA = (int(ptA.x),int(ptA.y))
    # putText(img, text, org, fontFace, fontScale, color[, thickness[, lineType[, bottomLeftOrigin]]])
    cv2.putText(frame,f"id {tagId}",(ptA[0], ptA[1] - 15),cv2.FONT_HERSHEY_SIMPLEX,1.0,(0, 0, 0),5)
    cv2.putText(frame,f"id {tagId}",(ptA[0], ptA[1] - 15),cv2.FONT_HERSHEY_SIMPLEX,1.0,(255, 255, 255),1)
    return frame

def draw_tagpose(frame, pose, tag):
    """ draw XYZ, Roll,Pitch,Yaw on the image"""
    ptA = tag.getCorner(0)
    ptA = (int(ptA.x),int(ptA.y))   
    t = pose.translation()  # convert to inches
    r = pose.rotation()
    msg = [f"X={39.37*t.x:0.2f}",
           f"Y={39.37*t.y:0.2f}",
           f"Z={39.37*t.z:0.2f}",
           f"Roll={r.z_degrees:0.2f}", 
           f"Pitch={r.x_degrees:0.2f}", 
           f"Yaw={r.y_degrees:0.2f}"]
    for idx,m in enumerate(msg):
        cv2.putText(frame,m,(ptA[0]+100, (ptA[1] - 120) + 20*(idx)), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0,0,0), 3)
        cv2.putText(frame,m,(ptA[0]+100, (ptA[1] - 120) + 20*(idx)), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1)    
    return frame

def draw_tagframe(frame,tag):
    """ draw tag outline on the image"""
    ptA,ptB,ptC,ptD = tag.getCorner(0),tag.getCorner(1),tag.getCorner(2),tag.getCorner(3)
    ptA = (int(ptA.x), int(ptA.y))
    ptB = (int(ptB.x), int(ptB.y))
    ptC = (int(ptC.x), int(ptC.y))
    ptD = (int(ptD.x), int(ptD.y))
    cv2.line(frame, ptA, ptB, (255, 255, 255), 2)
    cv2.line(frame, ptB, ptC, (255, 255, 255), 2)
    cv2.line(frame, ptC, ptD, (255, 255, 255), 2)
    cv2.line(frame, ptD, ptA, (255, 255, 255), 2)      
    return frame

def project_gamepiece_locations(roi_map, frame, result, cal, draw_roi=(0,205,205)):
    """
    Function that transforms roi rectangles based on AprilTag pose and camera calibration values 
    inputs:
        roi_rects: a list of 3x3 np.float32 arrays
        frame: current image being processed
        result: current AprilTag result
        cal: CameraCalibration object
    returns: 
        frame: image
        roi_rects: transformed roi_map in pixel coordinates
    """
    if draw_roi == None:
        return
    tag_id, pose, center, tag, est = result
    if tag_id not in [1,2,3,6,7,8]:
        return
    tvec = np.array(pose.translation())
    rvec = pose.rotation().getQuaternion().toRotationVector()
    ret = []
    for roi in roi_map:
        rect = create_roi_rect(roi)
        rect /=39.37
        # using the imag_cal class to automatically pull in the calibration values needed for cv2.projectPoints
        #imgpts,jac = cv2.projectPoints(rect, rvec, tvec,cal_factors.mtx, cal_factors.dst)
        imgpts,jac = cal.projectPoints(rect,rvec,tvec)
        ret.append(imgpts)
        imgpts = np.int32(imgpts).reshape(-1,2)
        
        for i in range(4):
            j = (i + 1) % 4
            frame = cv2.line(frame, tuple(imgpts[i]), tuple(imgpts[j]), draw_roi, 2)        
    return frame,ret

def draw_cube(frame, result, cal):
    """ draws a 3d cube over the apriltag to visually show pose """
    cube_pts = .05*np.float32([[  0,  0,  0], 
                       [  0,  1,  0], 
                       [  1,  1,  0], 
                       [  1,  0,  0],
                       [  0,  0,- 1],
                       [  0,  1, -1],
                       [  1,  1, -1],
                       [  1,  0, -1] ])    
    tag_id, pose, center, tag = result
    tvec = np.array(pose.translation())
    rvec = pose.rotation().getQuaternion().toRotationVector()
    imgpts, jac = cv2.projectPoints(cube_pts, rvec, tvec)
    imgpts = np.int32(imgpts).reshape(-1,2)
    # draw ground floor in green
    #frame = cv2.drawContours(frame, [imgpts[:4]],-1,(0,255,0),-3)
    # draw pillars in blue color
    for i,j in zip(range(4),range(4,8)):
        frame = cv2.line(frame, tuple(imgpts[i]), tuple(imgpts[j]),(255),3)
    # draw top layer in red color
    frame = cv2.drawContours(frame, [imgpts[4:]],-1,(0,0,255),3)
    return frame
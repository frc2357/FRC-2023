# The following defines the code for finding AprilTags, getting Pose estimation
# and then using the tranlsation and rotation pose estimations
# to calculate locations of game pieces on the field.  This information can then be used
# to determine if the game piece is present or not.
# Maybe like this: https://docs.opencv.org/4.x/d4/d61/tutorial_warp_affine.html
#
# See the following for inspiration
#   From https://github.com/churrobots/vision2023/blob/main/app.py
#   Modified to use robotbpy_apriltag Detector instead of pupil_apriltags
#   Also see https://gist.github.com/lobrien/5d5e1b38e5fd64062c43ac752b74889c
#
#   https://docs.opencv.org/4.7.0/d9/d0c/group__calib3d.html
#   projectPoints https://docs.opencv.org/4.7.0/d9/d0c/group__calib3d.html#ga1019495a2c8d1743ed5cc23fa0daff8c
#   calibrateCamera

import cv2
import robotpy_apriltag
from wpimath.geometry import Transform3d
import time
import math
from robotpy_apriltag import AprilTagFieldLayout
import math
import numpy as np
from calibration_from_opencv import CameraCalibration as cal


# more research needed here
#cv2.warpAffine(src, M, dsize, dst, flags, borderMode, borderValue) 
#

# matrix functions from https://github.com/SouthwestRoboticsProgramming/TagTracker/tree/master/src
def matrixToQuat(m):
    r11 = m[0][0]; r12 = m[0][1]; r13 = m[0][2]
    r21 = m[1][0]; r22 = m[1][1]; r23 = m[1][2]
    r31 = m[2][0]; r32 = m[2][1]; r33 = m[2][2]

    q0 = math.sqrt((1 + r11 + r22 + r33) / 4)
    q1 = math.sqrt((1 + r11 - r22 - r33) / 4)
    q2 = math.sqrt((1 - r11 + r22 - r33) / 4)
    q3 = math.sqrt((1 - r11 - r22 + r33) / 4)

    if q0 > q1 and q0 > q2 and q0 > q3:
        q1 = (r32 - r23) / (4 * q0)
        q2 = (r13 - r31) / (4 * q0)
        q3 = (r21 - r12) / (4 * q0)
    elif q1 > q0 and q1 > q2 and q1 > q3:
        q0 = (r32 - r23) / (4 * q1)
        q2 = (r12 + r21) / (4 * q1)
        q3 = (r13 + r31) / (4 * q1)
    elif q2 > q0 and q2 > q1 and q2 > q3:
        q0 = (r13 - r31) / (4 * q2)
        q1 = (r12 + r21) / (4 * q2)
        q3 = (r23 + r32) / (4 * q2)
    elif q3 > q0 and q3 > q1 and q3 > q2:
        q0 = (r21 - r12) / (4 * q3)
        q1 = (r13 + r31) / (4 * q3)
        q2 = (r23 + r32) / (4 * q3)

    return (q0, q1, q2, q3)

def invertQuat(q):
    return (q[0], -q[1], -q[2], -q[3])

def quatToAxisAngle(q):
    if q[0] == 1:
        return (0, (1, 0, 0))

    theta = 2 * math.acos(q[0])

    s = math.sin(theta / 2)
    x = q[1] / s
    y = q[2] / s
    z = q[3] / s

    return (theta, (x, y, z))

def quatToFUL(q):
    x, y, z, w = q
    
    forward = (
        2 * (x * z + w * y),
        2 * (y * z - w * x),
        1 - 2 * (x * x + y * y)
    )

    up = (
        2 * (x * y - w * z),
        1 - 2 * (x * x + z * z),
        2 * (y * z + w * x)
    )

    left = (
        1 - 2 * (y * y + z * z),
        2 * (x * y + w * z),
        2 * (x * z - w * y)
    )

    return (forward, up, left)


# This function is called once to initialize the apriltag detector and the pose estimator
def get_apriltag_detector_and_estimator(frame_size):
    detector = robotpy_apriltag.AprilTagDetector()
    # FRC 2023 uses tag16h5 (game manual 5.9.2)
    assert detector.addFamily("tag16h5")
    #Config(tagSize: meters, fx: float, fy: float, cx: float, cy: float)
    #From https://github.com/AprilRobotics/apriltag/wiki/AprilTag-User-Guide#python
    #fx, fy: The camera's focal length (in pixels). For most cameras fx and fy will be equal or nearly so.
    #cx, cy: The camera's focal center (in pixels). For most cameras this will be approximately the same as the image center.
    # these values come from the cameramatrix when doing calibration in opencv
    #  | Fx  0  Cx |
    #  |  0  Fy Cy |
    #  |  0  0  1  |
    estimator = robotpy_apriltag.AprilTagPoseEstimator(
    robotpy_apriltag.AprilTagPoseEstimator.Config(
            6.0/39.37, # 6" tags for FRC2023, 39.37 inches/meter
            1028.90904278, #  (Fx)
            1028.49276415, #  (Fy)
            638.57001085,  #  (Cx) should be roughly 1/2 the img width
            337.36382032,  #  (Cy) should be roughly 1/2 the img height
        )
    )
    return detector, estimator
    
# This function is called for every detected tag. It uses the `estimator` to 
# return information about the tag, including its centerpoint. (The corners are 
# also available.)
def process_apriltag(estimator, tag):
    tag_id = tag.getId()
    center = tag.getCenter()
    hamming = tag.getHamming()
    decision_margin = tag.getDecisionMargin()
    #print("Hamming for {} is {} with decision margin {}".format(tag_id, hamming, decision_margin))
    est = estimator.estimateOrthogonalIteration(tag, 50)
    return tag_id, est.pose1, center, tag

# Draw the TagID and Pose of the Tag
def draw_tagid(frame, tag):
    tagId = tag.getId()
    ptA = tag.getCorner(0)
    ptA = (int(ptA.x),int(ptA.y))
    # putText(img, text, org, fontFace, fontScale, color[, thickness[, lineType[, bottomLeftOrigin]]])
    cv2.putText(frame,f"id {tagId}",(ptA[0], ptA[1] - 15),cv2.FONT_HERSHEY_SIMPLEX,1.0,(0, 0, 0),5)
    cv2.putText(frame,f"id {tagId}",(ptA[0], ptA[1] - 15),cv2.FONT_HERSHEY_SIMPLEX,1.0,(255, 255, 255),1)
    return frame

def draw_tagpose(frame, pose, tag):
    ptA = tag.getCorner(0)
    ptA = (int(ptA.x),int(ptA.y))   
    t = pose.translation() * 39.37 # convert to inches
    r = pose.rotation()
    msg = [f"    X={r.x:0.2f}",
           f"    Y={t.y:0.2f}",
           f"    Z={t.z:0.2f}",
           f" Roll={r.z_degrees:0.2f}", 
           f"Pitch={r.x_degrees:0.2f}", 
           f"  Yaw={r.y_degrees:0.2f}"]
    for idx,m in enumerate(msg):
        cv2.putText(frame,m,(ptA[0]+100, (ptA[1] - 120) + 20*(idx)), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0,0,0), 3)
        cv2.putText(frame,m,(ptA[0]+100, (ptA[1] - 120) + 20*(idx)), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1)    
    return frame

# Draw a Frame to outline the Tag
def draw_tagframe(frame,tag):
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

# This simply outputs some information about the results returned by `process_apriltag`.
# It prints some info to the console and draws a circle around the detected center of the tag
def draw_tag(frame, result):
    assert frame is not None
    assert result is not None
    tag_id, pose, center, tag = result
    #print(center)
    cv2.circle(frame, (int(center.x), int(center.y)), 50, (255, 0, 255), 3)
    frame = draw_tagframe(frame, tag)
    frame = draw_tagid(frame, tag)
    frame = draw_tagpose(frame, pose, tag)
    return frame

###  Draw a Cube
def draw_cube(frame, result):
    axis = .05*np.float32([[  0,  0,  0], 
                       [  0,  1,  0], 
                       [  1,  1,  0], 
                       [  1,  0,  0],
                       [  0,  0,- 1],
                       [  0,  1, -1],
                       [  1,  1, -1],
                       [  1,  0, -1] ])    
    tag_id, pose, center, tag = result
    print(tag_id)
    print(cal.mtx)
    print(cal.dst)
    tvec = np.array(pose.translation())
    rvec = pose.rotation().getQuaternion().toRotationVector()
    print(tvec)
    print(rvec)
    imgpts, jac = cv2.projectPoints(axis, rvec, tvec, cal.mtx, cal.dst)
    print(imgpts)
    imgpts = np.int32(imgpts).reshape(-1,2)
    # draw ground floor in green
    #frame = cv2.drawContours(frame, [imgpts[:4]],-1,(0,255,0),-3)
    # draw pillars in blue color
    for i,j in zip(range(4),range(4,8)):
        frame = cv2.line(frame, tuple(imgpts[i]), tuple(imgpts[j]),(255),3)
    # draw top layer in red color
    frame = cv2.drawContours(frame, [imgpts[4:]],-1,(0,0,255),3)
    return frame

#https://docs.opencv.org/4.x/d7/d53/tutorial_py_pose.html
#def draw_cube(img, imgpts):
#    # project 3D points to image plane
#    imgpts, jac = cv2.projectPoints(axis, rvecs, tvecs, mtx, dist)    
#    imgpts = np.int32(imgpts).reshape(-1,2)
#    # draw pillars in blue color
#    for i,j in zip(range(4),range(4,8)):
#        img = cv2.line(img, tuple(imgpts[i]), tuple(imgpts[j]),(255),3)
#    # draw top layer in red color
#    img = cv2.drawContours(img, [imgpts[4:]],-1,(0,0,255),3)
#    return img


# This function is called once for every frame captured by the Webcam. For testing, it can simply
# be passed a frame capture loaded from a file. (See commented-out alternative `if __name__ == main:` at bottom of file)
def detect_and_process_apriltag(frame, detector, estimator):
    assert frame is not None
    # Convert the frame to grayscale
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    # Detect apriltag
    tag_info = detector.detect(gray)
    DETECTION_MARGIN_THRESHOLD = 100
    filter_tags = [tag for tag in tag_info if tag.getDecisionMargin() > DETECTION_MARGIN_THRESHOLD]
    results = [ process_apriltag(estimator, tag) for tag in filter_tags ]
    # Note that results will be empty if no apriltag is detected
    for result in results:
        frame = draw_tag(frame, result)
        frame = draw_cube(frame, result)
    for tag in filter_tags:
        frame = draw_tagframe(frame, tag)
    return frame
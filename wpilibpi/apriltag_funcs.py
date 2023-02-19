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
from calibration_from_opencv import image_cal, cam0 #bandaid for now


# more research needed here
#cv2.warpAffine(src, M, dsize, dst, flags, borderMode, borderValue) 
#

# matrix functions from https://github.com/SouthwestRoboticsProgramming/TagTracker/tree/master/src

# This function is called once to initialize the apriltag detector and the pose estimator
def get_apriltag_detector_and_estimator(frame_size,Fx,Fy,Cx,Cy):
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
            Fx,#1028.90904278, #  (Fx)
            Fy,#1028.49276415, #  (Fy)
            Cx,#638.57001085,  #  (Cx) should be roughly 1/2 the img width
            Cy,#337.36382032,  #  (Cy) should be roughly 1/2 the img height
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
    est = estimator.estimateOrthogonalIteration(tag, 100)
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

# Draw a Frame to outline the Tag
def draw_tagframe(frame,tag):
    ptA,ptB,ptC,ptD = tag.getCorner(0),tag.getCorner(1),tag.getCorner(2),tag.getCorner(3)
    ptA = (int(ptA.x), int(ptA.y))
    ptB = (int(ptB.x), int(ptB.y))
    ptC = (int(ptC.x), int(ptC.y))
    ptD = (int(ptD.x), int(ptD.y))
    print(ptA,ptB,ptC,ptD)
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
    #cv2.circle(frame, (int(center.x), int(center.y)), 50, (255, 0, 255), 3)
    frame = draw_tagframe(frame, tag)
    frame = draw_tagid(frame, tag)
    frame = draw_tagpose(frame, pose, tag)
    if tag_id not in [1,2,3,6,7,8]:
        return
    frame = draw_cube(frame, result)
    frame = project_gamepiece_locations(roi_map,frame,result,)
    return frame

#roi_map units = inches.  gets converted to meters in project_gamepiece_locations()
#roi_map element is X,Y,Z,W,H
# for some reason the coordinate system seems to be:
# X horizontal, -Y vertical, Z is distance from plane created by tag
# W is total width of detection area
# H is total height of detection area
roi_map = np.float32([[ 22,-15.84,8.43,2,2], #mid cone
                      [-22,-15.84,8.43,2,2],#mid cone
                      [ 22,-27.84,25.45,2,2],#high cone
                      [-22,-27.84,25.45,2,2],#high cone
                      [ 25.625, 12,-12,12,4], #low cone
                      [-25.625, 12,-12,12,4], #low cone
                      ])
                      

def expand_location(roi):
    x,y,z,W,H= roi 
    w = W/2.
    h = H/2

    return np.float32([[x-w,y-h,z],
                       [x-w,y+h,z],
                       [x+w,y+h,z],
                       [x+w,y-h,z]
                                   ])

def project_gamepiece_locations(roi_map, frame, result)->list:
    """
    Function that transforms roi rectangles based on
    AprilTag pose and camera calibration values 
    roi_rects: a list of 3x3 np.float32 arrays
    frame: current image being processed
    result: current AprilTag result
    cal_factors: CameraCalibration instance
    """
    tag_id, pose, center, tag = result
    if tag_id not in [1,2,3,6,7,8]:
        return
    tvec = np.array(pose.translation())
    rvec = pose.rotation().getQuaternion().toRotationVector()
    ret = []
    for roi in roi_map:
        rect = expand_location(roi)
        rect /=39.37
        # using the imag_cal class to automatically pull in the calibration values needed for cv2.projectPoints
        #imgpts,jac = cv2.projectPoints(rect, rvec, tvec,cal_factors.mtx, cal_factors.dst)
        imgpts,jac = image_cal.projectPoints(rect,rvec,tvec)
        imgpts = np.int32(imgpts).reshape(-1,2)
        
        for i in range(4):
            j = (i + 1) % 4
            print("\t",i,tuple(imgpts[i]), tuple(imgpts[j]))
            ret = cv2.line(frame, tuple(imgpts[i]), tuple(imgpts[j]),(0,205,205),3)        
    return ret


###  Draw a Cube
def draw_cube(frame, result):
    tag_id, pose, center, tag = result
    axis = 3*np.float32([[ -1, -1,  0], 
                         [ -1,  1,  0], 
                         [  1,  1,  0], 
                         [  1, -1,  0],
                         [ -1, -1,  -2], 
                         [ -1,  1,  -2], 
                         [  1,  1,  -2], 
                         [  1, -1,  -2], ])    

    tvec = 39.37*np.array(pose.translation())
    rvec = pose.rotation().getQuaternion().toRotationVector()
    print(f"Translation:{tvec}\nRotation:{rvec}")

    #TODO: Change back for actual camera -- Using static values for mtx/distCoeff for field images
    #mtx = np.float32([1000, 0, 1280/2,0,1000,720/2,0,0,1]).reshape(3,3)
    imgpts,jac = image_cal.projectPoints(axis,rvec,tvec)
    #imgpts, jac = cv2.projectPoints(axis, rvec, tvec, mtx, np.float32([0,0,0,0]))
    imgpts = np.int32(imgpts).reshape(-1,2)
    # draw ground floor in green
    frame = cv2.drawContours(frame, [imgpts[:4]],-1,(0,255,0),-3)
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
    return frame
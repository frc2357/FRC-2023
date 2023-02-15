#!/usr/bin/env python3

# Copyright (c) FIRST and other WPILib contributors.
# Open Source Software; you can modify and/or share it under the terms of
# the WPILib BSD license file in the root directory of this project.

import json
import time
import sys
import cv2
import numpy as np
import apriltag_funcs
from wpimath.geometry import Transform3d
import math

from cameravisionclass import CameraVision
from calibration_from_opencv import CameraCalibration
cal = CameraCalibration()


if __name__ == "__main__":
    cfgfile = None
    if len(sys.argv) > 1:
        cfgfile = str(sys.argv[1])
    camvis = CameraVision(cfgfile) # this class automatically creates all camera objects
    camConfig = camvis.cameras[0].getConfigJsonObject()
    detector,estimator = apriltag_funcs.get_apriltag_detector_and_estimator([camConfig["width"],camConfig["height"]])
    
    frame = np.zeros(shape=(camConfig["height"], camConfig["width"], 3), dtype=np.uint8)
    sink1,outstream1 = camvis.getComponents(0)
    #projectPoints	(	InputArray 	objectPoints,InputArray 	rvec,InputArray 	tvec,InputArray 	cameraMatrix,InputArray 	distCoeffs,OutputArray 	imagePoints,OutputArray 	jacobian = noArray(),double 	aspectRatio = 0 )	
    # loop forever
count = 0
avg = 0
while True:
    start = time.perf_counter()
    sink1.grabFrame(frame)
    #frame = cal.undistort(frame) # TODO: Is this necessary? -- AprilTags already using calibration values
    output = apriltag_funcs.detect_and_process_apriltag(frame, detector, estimator)
    outstream1.putFrame(output)
    end = time.perf_counter()
    avg += end-start
    count += 1
    if(count%10 == 0):
        print(f"AprilTags Processing took Avg: {avg/10.0:.3f} sec")
        avg = 0
        count = 0
    time.sleep(0.05)
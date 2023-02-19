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
cal = CameraCalibration

import glob
if __name__ == "__main__":
    cfgfile = "/boot/frc.json"
    simulate = False
    imgs = []
    if len(sys.argv) > 1:
        cfgfile = str(sys.argv[1])
    if len(sys.argv) > 2:
        simulate = True
    camvis = CameraVision(cfgfile, simulate=True) # this class automatically creates all camera objects
    cam0 = camvis.cameras[0]
    #camConfig = camvis.cameras[0].getConfigJsonObject()
    #sink1,outstream1 = camvis.getComponents(0)
    print(f'Camera width = {cam0.config["width"]}, {cam0.config["height"]}')
    frame = np.zeros(shape=(cam0.config["height"], cam0.config["width"], 3), dtype=np.uint8)
    Fx = cam0.cal.mtx[0,0]
    Fy = cam0.cal.mtx[1,1]
    Cx = cam0.cal.mtx[0,2]
    Cy = cam0.cal.mtx[1,2]
    detector,estimator = apriltag_funcs.get_apriltag_detector_and_estimator([cam0.config["width"],cam0.config["height"]],Fx,Fy,Cx,Cy)
    
count = 0
avg = 0
import random

while True:
    start = time.perf_counter()
    
    if simulate:
        frame = cam0.images[count%(len(cam0.images)-1)]
    else:
        cam0.sink.grabFrame(frame)
    #output = cal.undistort(frame) # this might not be necessary, AprilTags already using camera Cx,Cy,Fx,Fy values
    try:
        output = apriltag_funcs.detect_and_process_apriltag(frame, detector, estimator)
        cam0.outstream.putFrame(output)
        end = time.perf_counter()
        avg += end-start
        count += 1
        if(count%10 == 0):
            print(f"AprilTags Processing took Avg: {avg/10.0:.3f} sec")
            avg = 0
            #count = 0
        for i in range(0,30):
            cam0.outstream.putFrame(output)
            time.sleep(0.05)
    except:
        pass
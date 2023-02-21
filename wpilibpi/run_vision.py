#!/usr/bin/env python3

# Copyright (c) FIRST and other WPILib contributors.
# Open Source Software; you can modify and/or share it under the terms of
# the WPILib BSD license file in the root directory of this project.

import glob
import json
import logging
import math
import random
import sys
import time

import cv2
import numpy as np
from wpimath.geometry import Transform3d

import apriltag_funcs
import detect_colors
# class used to encapsulate Camera + Calibration information
from cameravision import CameraVision
from detect_colors import detect_colors
from calibration import image_cal, cam0 #bandaid for now

logging.basicConfig(level="INFO")

def timeit(func):
    # This function shows the execution time of 
    # the function object passed
    def wrap_func(*args, **kwargs):
        t1 = time.perf_counter()
        result = func(*args, **kwargs)
        t2 = time.perf_counter()
        print(f'Function {func.__name__!r} executed in {(t2-t1):.4f}s')
        return result
    return wrap_func
import copy
if __name__ == "__main__":
    cfgfile = "/boot/frc.json"
    simulate = False
    imgs = []
    if len(sys.argv) > 1:
        cfgfile = str(sys.argv[1])
    if len(sys.argv) > 2:
        simulate = True
    camvis = CameraVision(cfgfile, simulate=True) # this class automatically creates all camera + calibration objects
    cam0 = camvis.cameras[0]
    print(f'Camera width = {cam0.config["width"]}, {cam0.config["height"]}')
    frame = np.zeros(shape=(cam0.config["height"], cam0.config["width"], 3), dtype=np.uint8)
    # retrieve the camera calibration information needed by apriltags
    Fx = cam0.cal.mtx[0,0]
    Fy = cam0.cal.mtx[1,1]
    Cx = cam0.cal.mtx[0,2]
    Cy = cam0.cal.mtx[1,2]
    apriltagpipeline = apriltag_funcs.AprilTagPipeline((1280,720),Fx,Fy,Cx,Cy)
    
    count = 0
    avg = 0
    frame = None 
    while True:

        start = time.perf_counter()
        
        # grab a new frame or disk image
        if simulate:
            # make sure to make a copy of the original image
            frame = copy.copy(cam0.images[count%(len(cam0.images)-1)])
            orig = copy.copy(frame)
        else:
            cam0.sink.grabFrame(frame)
        try:
            # trying undistort seemed to cause lockup issues
            #frame = cam0.cal.undistort(frame)
            frame,roi_rects = apriltagpipeline.runPipeline(frame,image_cal)
            ###
            #trying to overlay detected colors
            #colors = detect_colors(orig)
            #frame = cv2.addWeighted(frame,0.7,colors,0.3,0)
            ###
            cam0.outstream.putFrame(frame)

            # track processing time.  
            end = time.perf_counter()
            avg += end-start
            count += 1
            if(count%10 == 0):
                logging.info(f"Processing took Avg: {avg/10.0:.4f} sec")
                avg = 0
            if simulate: # the following slows down image processing w/out messing up web server function
                for i in range(0,30):
                    cam0.outstream.putFrame(frame)
                    time.sleep(0.01)
        except Exception as e:
            #raise(e)
            #print(f"ERROR: {e}")
            pass
            
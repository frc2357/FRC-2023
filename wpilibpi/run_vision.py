#!/usr/bin/env python3
# originally from:
# Copyright (c) FIRST and other WPILib contributors.
# Open Source Software; you can modify and/or share it under the terms of
# the WPILib BSD license file in the root directory of this project.
#
# modified and extended by a mentor and other team members of
# Team 2357  Raymore-Peculiar High School
#
#    )     )  (  (       )
#  ( /(  ( /(  )\))(   ( /(
#  )(_)) )\())((_)()\  )\())
# ((_)  ((_)\  (()((_)((_)\
# |_  )|__ /    | __||__  /
#  / /  |_ \    |__ \  / /
# /___||___/    |___/ /_/
# text art (just because it's cool) check out: https://patorjk.com/software/taag

import copy
import json
import logging
import logging.handlers
import sys
import time

import cv2
import numpy as np
import apriltag_funcs
import detect_colors

# class used to encapsulate Camera + Calibration information
from cameravision import CameraVision
from gamepiece import gamepiecetracker as gptracker

fmt = logging.Formatter("%(asctime)s,%(levelname)10s:%(name)20s,%(message)s")

log = logging.getLogger("")
log.setLevel("INFO")

# sh = logging.handlers.SocketHandler("localhost", logging.handlers.DEFAULT_TCP_LOGGING_PORT)
# sh.setFormatter(fmt)
# sh.setLevel("DEBUG")

ch = logging.StreamHandler(stream=sys.stdout)
ch.setFormatter(fmt)
ch.setLevel("INFO")
# log.addHandler(sh) # big CPU% penalty here, don't suggest using for competition
log.addHandler(ch)

global g_avg
g_avg = 1000.0


def add_text_toimg(frame, txt):
    cv2.putText(frame, f"{txt}", (10, 40), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 0), 5)
    cv2.putText(frame, f"{txt}", (10, 40), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1)
    return frame


if __name__ == "__main__":
    cfgfile = "/boot/frc.json"
    simulate = False
    imgs = []
    if len(sys.argv) > 1:
        cfgfile = str(sys.argv[1])
    if len(sys.argv) > 2:
        simulate = True

    # this class automatically creates all camera + calibration objects
    camvis = CameraVision(cfgfile, simulate=simulate)
    cam0 = camvis.cameras[0]
    cam1 = camvis.cameras[1]

    # configure networks tables
    nt_table = camvis.ntinst.getTable("gridcam")
    apriltag_NT = nt_table.getStringTopic("tags").publish()
    frame = np.zeros(shape=(cam0.config["height"], cam0.config["width"], 3), dtype=np.uint8)
    frame1 = np.zeros_like(frame)
    # retrieve the camera calibration information needed by apriltags

    apriltag = apriltag_funcs.AprilTagDetector((1280, 720))
    apriltag.register_NT_vars(nt_table)

    gpdetector = detect_colors.GamePieceDetector()
    gpdetector.register_NT_vars(nt_table)
    gptracker.register_NT_vars(nt_table)

    count = 0
    avg = 0
    while True:

        try:
            capture_start = time.perf_counter()
            # grab a new frame or disk image
            if simulate:
                # make sure to make a copy of the original image
                frame = copy.copy(cam0.images[int(count) % (len(cam0.images))])
                frame1 = copy.copy(cam0.images[int(count + 12) % (len(cam0.images))])
            else:
                cam0.sink.grabFrame(frame)
                cam1.sink.grabFrame(frame1)

            # get server time for which last frame was captured
            timeoffset = camvis.ntinst.getServerTimeOffset()
            offset_start = time.perf_counter()
        
            # reset tag locations on every loop
            apriltag.reset_taglocs()

            # TODO: do we care if image is undistorted?
            # asked another way --> is AprilTags using the camera matrix values
            # to  undistort?  Seemed to cause lockup issues early in testing      
            # ALSO -- VERY EXPENSIVE TO DO ON RASPBERRY PI (3.5/sec vs 7/sec)     
            # frame = cam0.cal.undistort(frame)
            # frame1 = cam1.cal.undistort(frame1)
            orig = copy.copy(frame)  # create a copy for color detection
            orig1 = copy.copy(frame1)

            # process AprilTags
            frame, roi_rects = apriltag.runPipeline(frame, cam0.cal, 0)
            frame1, roi_rects1 = apriltag.runPipeline(frame1, cam1.cal, 1)
            tag_end = time.perf_counter()

            # process gamepieces
            orig = gpdetector.runPipeline(orig, roi_rects, colorize=0.5)
            orig1 = gpdetector.runPipeline(orig1, roi_rects1, colorize=0.5)
            gp_end = time.perf_counter()

            # update NetworkTable Variable for Tags.
            # We do it here since we are running detection on multiple cameras
            # and we want to return one and only one tag location json string
            apriltag.taglocs["timestamp"] = timeoffset
            jstr = json.dumps(apriltag.taglocs, separators=(",", ":"))

            apriltag_NT.set(jstr)  # send data to networks table
            gpdetector.update_NT_vars()
            gptracker.update_NT_vars()
            nt_end = time.perf_counter()
                
            if simulate:
                frame = cv2.addWeighted(frame, 0.5, orig, 0.5, 0)
                frame1 = cv2.addWeighted(frame1, 0.5, orig1, 0.5, 0)

            frame = add_text_toimg(frame, f"{1/g_avg:0.2f}/sec")
            stacked = cv2.resize(np.hstack((frame, frame1)), None, fx=0.5, fy=0.5)

            cam0.outstream.putFrame(stacked)
            # track processing time, average ten updates.
            end = time.perf_counter()
            avg += end - capture_start
            count += 1
            if count % 10 == 0:
                g_avg = avg / 10.0
                log.info(f"Processing took Avg: {g_avg:.4f} sec Tag:{tag_end-offset_start:.4f}\tGP:{gp_end-tag_end:.4f}\tNT:{nt_end-gp_end:.4f}")
                avg = 0
            # if simulate:  # the following slows down image processing w/out messing up web server function
            #    for i in range(0, 30):
            #        cam0.outstream.putFrame(stacked)
            #        time.sleep(0.05)
        except Exception as e:
            # raise(e)
            log.exception(f"ERROR: {e}")
            pass
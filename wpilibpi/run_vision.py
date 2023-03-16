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
import detect_apriltags
import detect_colors

# class used to encapsulate Camera + Calibration information
from cameravision import CameraVision
from calibration import CameraCalibration
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

cam0_cal = CameraCalibration.load_cal_dict(
    {
        "mtx": [911.424067477301, 0.0, 713.8769225688438, 0.0, 914.3959334705401, 331.0440107788305, 0.0, 0.0, 1.0],
        "dst": [
            0.043635681303603034,
            -0.03869905321945732,
            -0.007039532851637899,
            -0.0058404588116693626,
            0.001758340632447729,
        ],
        "img_size": [1280, 720],
        "newmtx": [
            918.4147338867188,
            0.0,
            705.0359130240649,
            0.0,
            913.0012817382812,
            326.65768869504063,
            0.0,
            0.0,
            1.0,
        ],
        "roi": [8, 9, 1265, 704],
    }
)
cam1_cal = CameraCalibration.load_cal_dict(
    {
        "mtx": [934.6621237941703, 0.0, 691.4435720100407, 0.0, 935.8448952233725, 312.6243916947416, 0.0, 0.0, 1.0],
        "dst": [
            0.1399030918638301,
            -0.3529700852096354,
            0.00427705498057264,
            -0.015428783659568061,
            0.4776956619701835,
        ],
        "img_size": [1280, 720],
        "newmtx": [968.441650390625, 0.0, 669.3759763489797, 0.0, 945.6002807617188, 314.9049061216647, 0.0, 0.0, 1.0],
        "roi": [19, 18, 1248, 673],
    }
)


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

    if simulate:
        cam1 = camvis.cameras[0]
    else:
        cam1 = camvis.cameras[1]
        cam0.cal = cam0_cal
        cam1.cal = cam1_cal
    log.info(cam0.cal.to_json())
    log.info(cam1.cal.to_json())

    # configure networks tables
    nt_table = camvis.ntinst.getTable("gridcam")
    apriltag_NT = nt_table.getStringTopic("tags").publish()
    nt_btnboard = camvis.ntinst.getTable("buttonboard")
    nt_capture = nt_table.getBooleanTopic("capture_image")
    frame = np.zeros(shape=(cam0.config.height, cam0.config.width, 3), dtype=np.uint8)
    frame1 = np.zeros_like(frame)
    # retrieve the camera calibration information needed by apriltags

    apriltag = detect_apriltags.AprilTagDetector((cam0.config.height, cam0.config.width))
    apriltag.register_NT_vars(nt_btnboard)

    gpdetector = detect_colors.GamePieceDetector()
    gpdetector.register_NT_vars(nt_table)
    gptracker.register_NT_vars(nt_table)

    count = 0
    avg = 0
    time_offset = time.time() + camvis.ntinst.getServerTimeOffset()/1_000_000.0
    while True:

        try:
            capture_start = time.perf_counter()
            # grab a new frame or disk image
            if simulate:
                # make sure to make a copy of the original image
                frame = copy.copy(cam0.images[int(count) % (len(cam0.images))])
                frame1 = copy.copy(cam0.images[int(count + 12) % (len(cam0.images))])
            else:
                #TODO: Worthwhile to thread this so both captures occur ~same time
                ct0 = cam0.sink.grabFrame(frame)
                ct1 = cam1.sink.grabFrame(frame1)

            # get server time for which last frame was captured
            t = time.time()
            o = camvis.ntinst.getServerTimeOffset()
            timeoffset = t + o/1_000_000.0
            offset_start = time.perf_counter()
            log.info(f"TIME: SYS={t},t0={ct0},t1={ct1},offset={o},first_offset={time_offset}")
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

            # send stacked frame to DS
            camvis.outputstream.putFrame(stacked)

            # track processing time, average ten updates.
            end = time.perf_counter()
            avg += end - capture_start
            count += 1
            if count % 10 == 0:
                g_avg = avg / 10.0
                log.info(
                    f"Processing took Avg: {g_avg:.4f} sec Grab:{offset_start-capture_start:.4f}\tTag:{tag_end-offset_start:.4f}\tGP:{gp_end-tag_end:.4f}\tNT:{nt_end-gp_end:.4f}"
                )
                avg = 0
            # if simulate:  # the following slows down image processing w/out messing up web server function
            #    for i in range(0, 30):
            #        cam0.outstream.putFrame(stacked)
            #        time.sleep(0.05)
        except Exception as e:
            # raise(e)
            log.exception(f"ERROR: {e}")
            pass

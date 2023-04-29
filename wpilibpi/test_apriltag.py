import copy
import cv2
import numpy as np
import time
import glob
from detect_apriltags import AprilTagDetector
from calibration import image_cal
import logging
logging.basicConfig(level="DEBUG")

# A required callback method that goes into the trackbar function.
def nothing(x):
    pass


# Use pre-captured images
imgs = []
fimages = glob.glob(".\\images\\*.png")
print(fimages)
for fname in fimages:
    imgs.append(np.ascontiguousarray(cv2.imread(fname)))
# Create a window named trackbars.
cv2.namedWindow("AprilTag")

count = 0
april = AprilTagDetector() #AprilTagDetector([720, 1280], 1000, 1000, 1280 / 2, 720 / 2, "")

while True:

    # Start reading the webcam feed frame by frame.
    frame = copy.copy(imgs[count % len(imgs)])
    # ret, frame = cap.read()
    # if not ret:
    #    break

    # Convert the BGR image to HSV image.
    # gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    frame, roi_rects = april.runPipeline(frame, image_cal, 0)

    # Show this stacked frame at 40% of the size.
    cv2.imshow("AprilTag", frame)#cv2.resize(frame, None, fx=0.5, fy=0.5))

    # If the user presses ESC then exit the program
    key = cv2.waitKey(20)
    if key == 27:
        break
    if key == ord("r"):
        count -= 1
    if key == ord("n"):
        count += 1

    # If the user presses `s` then print this array.
    if key == ord("s"):

        # thearray = [[l_h,l_s,l_v],[u_h, u_s, u_v]]
        # print(thearray)

        # Also save this array as penval.npy
        # np.save('hsv_value',thearray)
        break
    # time.sleep(5)

# Release the camera & destroy the windows.
cv2.destroyAllWindows()

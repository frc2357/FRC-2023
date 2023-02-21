#
# based on code from:
# https://www.geeksforgeeks.org/multiple-color-detection-in-real-time-using-python-opencv/
#
# modified to be a class instance
#
# Python code for Multiple Color Detection


import numpy as np
import cv2


# Capturing video through webcam


def detect_colors(imageFrame):
    
    # Convert the imageFrame in
    # BGR(RGB color space) to
    # HSV(hue-saturation-value)
    # color space
    hsvFrame = cv2.cvtColor(imageFrame, cv2.COLOR_BGR2HSV)
    
    # create a lower and upper HSV value to detect Yellow
    yel_lower = np.array([15,128,0],'uint8')
    yel_upper = np.array([78,255,255],'uint8')
    # Filter the image and get the binary mask, where white represents 
    # your target color
    mask = cv2.inRange(hsvFrame, yel_lower, yel_upper)
 
    # You can also visualize the real part of the target color (Optional)
    res = cv2.bitwise_and(imageFrame, imageFrame, mask=mask)

    # Morphological Transform, Dilation
    # for each color and bitwise_and operator
    # between imageFrame and mask determines
    # to detect only that particular color    
    # CURRENTLY UNUSED:
    #kernal = np.ones((5, 5), "uint8")
    #mask = cv2.dilate(mask, kernal)
    #result_yel = cv2.bitwise_and(imageFrame, imageFrame, mask=mask)

    # Creating contour to track red color
    contours, hierarchy = cv2.findContours(mask,
                                        cv2.RETR_TREE,
                                        cv2.CHAIN_APPROX_SIMPLE)
    
    for pic, contour in enumerate(contours):
        area = cv2.contourArea(contour)
        if(area > 300):
            x, y, w, h = cv2.boundingRect(contour)
            imageFrame = cv2.rectangle(imageFrame, (x, y),
                                    (x + w, y + h),
                                    (0, 0, 255), 2)
            
            cv2.putText(imageFrame, "Yellow Color", (x, y),
                        cv2.FONT_HERSHEY_SIMPLEX, 1.0,
                        (0, 0, 255))	

    return res

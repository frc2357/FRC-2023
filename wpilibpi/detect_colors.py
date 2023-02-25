#
# based on code from:
# https://www.geeksforgeeks.org/multiple-color-detection-in-real-time-using-python-opencv/
#
# modified to be a class instance
#
# Python code for Multiple Color Detection

import logging
import numpy as np
import cv2
log = logging.getLogger(__name__)
log.addHandler(logging.NullHandler())
# YELLOW CONE
# Lower HSV (15, 90,100) -- (15,136,255)
# Upper HSV (60,255,255) -- (31,255,255)

def runPipeline(frame,roipts):
    """
    name main function runPipeline to stay consistent
    """
    x1 = roipts[0]
    x2 = roipts[2]
    y1 = roipts[1]
    y2 = roipts[3]
    
    ret= detect_colors(frame,[y1,y2,x1,x2])
    if ret > .8:
        frame[y1:y2,x1:x2,:]= (255,255,255)
    return frame


def detect_colors(frame, roi):
    
    # Convert the imageFrame in
    # BGR(RGB color space) to
    # HSV(hue-saturation-value)
    # color space
    imageFrame = frame[roi[0]:roi[1],roi[2]:roi[3],:]
    hsvFrame = cv2.cvtColor(imageFrame, cv2.COLOR_BGR2HSV)
    
    # create a lower and upper HSV value to detect Yellow
    yel_lower = np.array([  5,100,140],'uint8')
    yel_upper = np.array([100,255,237],'uint8')

    # create a lwoer and upper HSV value to detect Violet
    vio_lower = np.array([ 76, 55, 55],'uint8')
    vio_upper = np.array([153,255,255],'uint8')    
    # Filter the image and get the binary mask, where white represents 
    # your target color
    mask_yel = cv2.inRange(hsvFrame, yel_lower, yel_upper)
    mask_vio = cv2.inRange(hsvFrame, vio_lower, vio_upper)
 
    percent_yellow = np.count_nonzero(mask_yel)/np.product(mask_yel.shape)
    percent_violet = np.count_nonzero(mask_vio)/np.product(mask_vio.shape)
    if percent_yellow > 0.5 or percent_violet > 0.5:
        log.debug(f"^Y={percent_yellow:0.2f}\t%V={percent_violet:0.2f}")
    #return frame
    return max([percent_yellow,percent_violet])

def highlight(imageFrame, mask, txt):
    contours, hierarchy = cv2.findContours(mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    
    for pic, contour in enumerate(contours):
        area = cv2.contourArea(contour)
        if(area > 0):
            x, y, w, h = cv2.boundingRect(contour)
            imageFrame = cv2.rectangle(imageFrame, (x, y),
                                    (x + w, y + h),
                                    (0, 255, 0), 2)
            
            cv2.putText(imageFrame, txt, (x, y),
                        cv2.FONT_HERSHEY_SIMPLEX, 1.0,
                        (0, 0, 255))	
    return imageFrame

#finding hsv range of target object(pen)
# Possible starting values:

# YELLOW CONE
# Lower HSV (15, 90,100) -- (15,136,255)
# Upper HSV (60,255,255) -- (31,255,255)

# VIOLET BALL
# Lower HSV (44, 63, 55)	-- (120,79,255)
# Upper HSV (158, 255, 255)	-- (153,255,255)

import cv2
import numpy as np
import time
import glob
# A required callback method that goes into the trackbar function.
def nothing(x):
    pass

# Initializing the webcam feed.
#cap = cv2.VideoCapture(0)
#cap.set(3,1280)
#cap.set(4,720)


# Use pre-captured images
imgs = []
fimages = glob.glob(".\\images\\*.png")
print(fimages)
for fname in fimages:
    imgs.append(np.ascontiguousarray(cv2.imread(fname)))
# Create a window named trackbars.
cv2.namedWindow("Trackbars")

# Now create 6 trackbars that will control the lower and upper range of 
# H,S and V channels. The Arguments are like this: Name of trackbar, 
# window name, range,callback function. For Hue the range is 0-179 and
# for S,V its 0-255.
cv2.createTrackbar("L - H", "Trackbars", 0, 179, nothing)
cv2.createTrackbar("L - S", "Trackbars", 0, 255, nothing)
cv2.createTrackbar("L - V", "Trackbars", 0, 255, nothing)
cv2.createTrackbar("U - H", "Trackbars", 179, 179, nothing)
cv2.createTrackbar("U - S", "Trackbars", 255, 255, nothing)
cv2.createTrackbar("U - V", "Trackbars", 255, 255, nothing)
count = 0

while True:
    
    # Start reading the webcam feed frame by frame.
    frame = imgs[count % len(imgs)]    
    #ret, frame = cap.read()
    #if not ret:
    #    break

    # Convert the BGR image to HSV image.
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
    
    # Get the new values of the trackbar in real time as the user changes 
    # them
    l_h = cv2.getTrackbarPos("L - H", "Trackbars")
    l_s = cv2.getTrackbarPos("L - S", "Trackbars")
    l_v = cv2.getTrackbarPos("L - V", "Trackbars")
    u_h = cv2.getTrackbarPos("U - H", "Trackbars")
    u_s = cv2.getTrackbarPos("U - S", "Trackbars")
    u_v = cv2.getTrackbarPos("U - V", "Trackbars")
 
    # Set the lower and upper HSV range according to the value selected
    # by the trackbar
    lower_range = np.array([l_h, l_s, l_v])
    upper_range = np.array([u_h, u_s, u_v])
    
    # Filter the image and get the binary mask, where white represents 
    # your target color
    mask = cv2.inRange(hsv, lower_range, upper_range)
 
    # You can also visualize the real part of the target color (Optional)
    res = cv2.bitwise_and(frame, frame, mask=mask)
    
    # Converting the binary mask to 3 channel image, this is just so 
    # we can stack it with the others
    mask_3 = cv2.cvtColor(mask, cv2.COLOR_GRAY2BGR)
    
    # stack the mask, orginal frame and the filtered result
    stacked = np.hstack((mask_3,frame,res))
    
    # Show this stacked frame at 40% of the size.
    cv2.imshow('Trackbars',cv2.resize(stacked,None,fx=0.4,fy=0.4))
    
    # If the user presses ESC then exit the program
    key = cv2.waitKey(20)
    if key == 27:
        break

    if key == ord('n'):
        count += 1

    
    # If the user presses `s` then print this array.
    if key == ord('s'):
        
        thearray = [[l_h,l_s,l_v],[u_h, u_s, u_v]]
        print(thearray)
        
        # Also save this array as penval.npy
        np.save('hsv_value',thearray)
        break
    #time.sleep(5)
    
# Release the camera & destroy the windows.    
cv2.destroyAllWindows()
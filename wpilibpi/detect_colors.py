#
# based on code from:
# https://www.geeksforgeeks.org/multiple-color-detection-in-real-time-using-python-opencv/
#
# modified to be a class instance
#
# Python code for Multiple Color Detection

import json
import logging
import numpy as np
from numpy import ndarray
import cv2
log = logging.getLogger(__name__)
log.addHandler(logging.NullHandler())
# YELLOW CONE
# Lower HSV (15, 90,100) -- (15,136,255)
# Upper HSV (60,255,255) -- (31,255,255)

class GamePieceDetector:
    #the following are all singletons
    gamepiece_results = np.zeros((3,9,2),dtype=float)
    #TODO: gamepiece_poses = np.zeros((3,9,1),dtype=float)?
    color_violet = (255,0,217)  #BGR
    color_yellow = (28,215,215) #BGR
    _yel_lower = np.array((  5, 100, 140),'uint8') #HSV
    _yel_upper = np.array((100, 255, 237),'uint8') #HSV
    _vio_lower = np.array((118,  55,  55),'uint8') #HSV
    _vio_upper = np.array((153, 255, 255),'uint8') #HSV
    # first index is row ( 0 = low, 1 = mid, 2 = high )
    # second index is column (left most  =0 to right most = 8)
    # third index is color (yellow = 0, violet = 1)

    def __init__(self):
        """
        """
        pass

    def set_yellow_range(self,low=None,upp=None):
        if low is not None:
            self._yel_lower = np.array(low,'uint8')
        if upp is not None:
            self._yel_upper = np.array(upp,'uint8')

    def set_violet_range(self,low=None,upp=None):
        if low is not None:
            self._vio_lower = np.array(low,'uint8')
        if upp is not None:
            self._vio_upper = np.array(upp,'uint8')

    def map_gamepiece_results(self, tag_id:int, idx:int, yel_pct:float, vio_pct:float):
        # from left to right tag locations
        # tag [3]  [2]  [1]
        # tag [8]  [7]  [6]
        if tag_id in [4,5]:
            log.error(f"tag_id out of range, was {tag_id}")
        if idx < 0 or idx > 8:  # bounds check
            log.error(f"idx out of range, was {idx}")
            return
        tag_map = {3:0, 2:3, 1:6,8:0, 7:3, 6:6}
        tag_offset = tag_map[tag_id]
        col = tag_offset + idx//3
        row = idx % 3
        log.debug(f"map_gamepiece:tag_id {tag_id}\trow = {row}\tcol = {col}\ttag_offset = {tag_offset}")
        # write new values to gamepiece_resultsj
        # TODO: make this sticky
        self.gamepiece_results[row,col,:]=[yel_pct,vio_pct]

    def to_json(self):
        return json.dumps({'cone':self.gamepiece_results[:,:,0].tolist(),
                'cube':self.gamepiece_results[:,:,1].tolist()},separators=(',',':'))
    
    def runPipeline(self, frame:ndarray, roi_result:list, colorize:float=0.0):
        """ main function used for color detection

            Args:
                frame: ndarray (image)
                roi_result: tuple of (tag_id, roi_rects), where roi_rects is a list of [x1,y1,x2,y2] roi regions in pixel coordinates
                colorize: float used to add colored frames. 0.0 = do not colorize, > 0.0 -- threshold for 'detection' 
        """
        yel_pct = -1.0
        vio_pct = -1.0
        # TODO: need to rework this
        for tag_id, roi_rects in roi_result:
            for idx,roi in enumerate(roi_rects):
                try:
                    x1,y1,x2,y2 = roi 
                    yel_pct,vio_pct = self.detect_colors(frame,[y1,y2,x1,x2])
                    self.map_gamepiece_results(tag_id,idx,yel_pct,vio_pct)
                    # add colorization to the image if colorize is non-zero
                    if colorize > 0.0:
                        if yel_pct > colorize:
                            frame[y1:y2,x1:x2,:] = self.color_yellow
                        if vio_pct > colorize:
                            frame[y1:y2,x1:x2,:] = self.color_violet
                except Exception as e:
                    log.exception(f"Exception when processing roi_rect[{idx}] for tag={tag_id} msg was {e}")
        return frame, yel_pct, vio_pct

    def detect_colors(self, frame:ndarray, roi:list)->tuple:
        
        yel_pct = -1.0
        vio_pct = -1.0
        try:
            # grab the region of interest, convert color space from RGB to HSV
            # literature informs that color separation is easier in HSV
            hsvFrame = cv2.cvtColor(frame[roi[0]:roi[1],roi[2]:roi[3],:], cv2.COLOR_BGR2HSV)
             
            # Filter the image and get a binary mask, where white (255) is a match, black (0) is not a match
            # use test_findcolor.py to find lower/upper values to use
            mask_yel = cv2.inRange(hsvFrame, self._yel_lower, self._yel_upper)
            mask_vio = cv2.inRange(hsvFrame, self._vio_lower, self._vio_upper)
        
            #calculate the area that matched, converted to a percentage of total area
            yel_pct = np.count_nonzero(mask_yel)/np.product(mask_yel.shape)
            vio_pct  = np.count_nonzero(mask_vio)/np.product(mask_vio.shape)

            if yel_pct > 0.1 or vio_pct > 0.1:
                log.debug(f"^Y={yel_pct:0.2f}\t%V={vio_pct:0.2f}")
        except Exception as e:
            log.exception(e)
        return yel_pct, vio_pct
    
    @staticmethod
    def highlight(imageFrame:ndarray, mask:ndarray, txt:str):
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

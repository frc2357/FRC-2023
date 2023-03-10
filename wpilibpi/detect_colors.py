#
# based on code from:
# https://www.geeksforgeeks.org/multiple-color-detection-in-real-time-using-python-opencv/
#
# modified to be a class instance
#
# Python code for Multiple Color Detection

import logging
import numpy as np
from numpy import ndarray
from gamepiece import gamepiecetracker
from ntcore import NetworkTable 
import cv2

log = logging.getLogger(__name__)
log.addHandler(logging.NullHandler())


class GamePieceDetector:
    # the following are all singletons
    # gamepiece_results = np.zeros((3,9,2),dtype=float)
    # gamepiece_chars = np.chararray((3,9),unicode=True)
    # gamepiece_chars[:] = '-' #set default value
    color_violet = (255, 0, 217)  # BGR for 'COLORIZING' game pieces
    color_yellow = (28, 215, 215)  # BGR for 'COLORIZING' game pieces
    _yel_lower = np.array((5, 100, 140), "uint8")  # HSV
    _yel_upper = np.array((100, 255, 237), "uint8")  # HSV
    _vio_lower = np.array((118, 55, 55), "uint8")  # HSV
    _vio_upper = np.array((153, 255, 255), "uint8")  # HSV
    last_NT_update = 0

    def __init__(self):
        """ """
        pass

    def register_NT_vars(self, ntable: NetworkTable):
        # first index is row ( 0 = low, 1 = mid, 2 = high )
        # second index is column (left most  =0 to right most = 8)
        # third index is color (yellow = 0, violet = 1)
        # gamepieces = GamePieceTracker()
        self.NT_yel_lower = ntable.getIntegerArrayTopic("yellow_lower_HSV").getEntry([5, 100, 140])
        self.NT_yel_upper = ntable.getIntegerArrayTopic("yellow_upper_HSV").getEntry([100, 255, 237])
        self.NT_vio_lower = ntable.getIntegerArrayTopic("violet_lower_HSV").getEntry([118, 55, 55])
        self.NT_vio_upper = ntable.getIntegerArrayTopic("violet_upper_HSV").getEntry([153, 255, 255])
        self.NT_yel_lower.set([5, 100, 140])
        self.NT_yel_upper.set([100, 255, 237])
        self.NT_vio_lower.set([118, 55, 55])
        self.NT_vio_upper.set([153, 255, 255])

    def update_NT_vars(self):
        return
        self.set_yellow_range(self.NT_yel_lower.get(), self.NT_yel_upper.get())
        self.set_violet_range(self.NT_vio_lower.get(), self.NT_vio_upper.get())

    def set_yellow_range(self, low=None, upp=None):
        """update the lower / upper ranges (HSV)! for yellow color determination
        Args:
            low: tuple like (H,S,V) value.  If none, no update is performed
            upp: tuple like (H,S,V) value.  If none, no update is performed
        """
        if low is not None:
            self._yel_lower = np.array(low, "uint8")
        if upp is not None:
            self._yel_upper = np.array(upp, "uint8")

    def set_violet_range(self, low=None, upp=None):
        """update the lower / upper ranges (HSV)! for yellow color determination
        Args:
            low: tuple like (H,S,V) values.  If none, no update is performed
            upp: tuple like (H,S,V) values.  If none, no update is performed
        """
        if low is not None:
            self._vio_lower = np.array(low, "uint8")
        if upp is not None:
            self._vio_upper = np.array(upp, "uint8")

    def runPipeline(self, frame: ndarray, roi_result: list, colorize: float = 0.0):
        """main function used for color detection

        Args:
            frame: ndarray (image)
            roi_result: tuple of (tag_id, roi_rects), where roi_rects is a list of [x1,y1,x2,y2] roi regions in pixel coordinates
            colorize: float used to add colored frames. 0.0 = do not colorize, > 0.0 -- threshold for 'detection'
        """
        yel_pct = -1.0
        vio_pct = -1.0
        w, h = frame.shape[0:2]

        # TODO:  this isn't yet working (see notes above where create the NT variables)
        # self.set_yellow_range(self.NT_yel_lower.get(), self.NT_yel_upper.get())
        # self.set_violet_range(self.NT_vio_lower.get(), self.NT_vio_upper.get())

        for tag_id, roi_rects in roi_result:  # for a given tag, there are 9 roi_rects
            for idx, roi in enumerate(roi_rects):
                # idx 0..9, roi is a tuple of (x1,y1,x2,y2) corners defining the region of interest
                try:
                    x1, y1, x2, y2 = roi
                    # need to bounds check that roi is within image
                    if (
                        any((x1 < 0, x1 > w, x2 < 0, x2 > w, y1 < 0, y1 > w, y2 < 0, y2 > w))
                        or (x2 - x1) <= 0
                        or (y2 - y1) <= 0
                    ):
                        continue
                    yel_pct, vio_pct = self.detect_colors(frame, roi)
                    gamepiecetracker.map_gamepiece_results(tag_id, idx, yel_pct, vio_pct)
                    # add colorization to the image if colorize is non-zero
                    if colorize > 0.0:  # TODO: make this a class method
                        if yel_pct > colorize:
                            frame[y1:y2, x1:x2, :] = self.color_yellow
                        if vio_pct > colorize:
                            frame[y1:y2, x1:x2, :] = self.color_violet
                except Exception as e:
                    log.exception(f"Exception when processing roi_rect[{idx}] for tag={tag_id} msg was {e}")
        return frame

    def detect_colors(self, frame: ndarray, roi: list) -> tuple:
        yel_pct = -1.0
        vio_pct = -1.0
        try:
            # grab the region of interest, convert color space from RGB to HSV
            # literature informs that color separation is easier in HSV
            hsvFrame = cv2.cvtColor(frame[roi[1] : roi[3], roi[0] : roi[2], :], cv2.COLOR_BGR2HSV)
            # Filter the image and get a binary mask, where white (255) is a match, black (0) is not a match
            # use test_findcolor.py to find lower/upper values to use
            mask_yel = cv2.inRange(hsvFrame, self._yel_lower, self._yel_upper)
            mask_vio = cv2.inRange(hsvFrame, self._vio_lower, self._vio_upper)
            # calculate the area that matched, converted to a percentage of total area
            yel_pct = np.count_nonzero(mask_yel) / np.product(mask_yel.shape)
            vio_pct = np.count_nonzero(mask_vio) / np.product(mask_vio.shape)
        except Exception as e:
            log.exception(e)
        return yel_pct, vio_pct

    # @staticmethod
    # def highlight(imageFrame:ndarray, mask:ndarray, txt:str):
    #     contours, hierarchy = cv2.findContours(mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    #     for pic, contour in enumerate(contours):
    #         area = cv2.contourArea(contour)
    #         if(area > 0):
    #             x, y, w, h = cv2.boundingRect(contour)
    #             imageFrame = cv2.rectangle(imageFrame, (x, y),
    #                                     (x + w, y + h),
    #                                     (0, 255, 0), 2)
    #             cv2.putText(imageFrame, txt, (x, y),
    #                         cv2.FONT_HERSHEY_SIMPLEX, 1.0,
    #                         (0, 0, 255))
    #     return imageFrame

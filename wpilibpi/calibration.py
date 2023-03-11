# json cal info from calibration performed at https://www.calibdb.net/
# python implementation is available here: https://github.com/paroj/pose_calib
#
# some code from https://docs.opencv.org/4.x/dc/dbb/tutorial_py_calibration.html
#
import numpy as np
import cv2
import json


class CameraCalibration:
    """
    Class to hold Calibration Coefficients
    Has functions (undistort / projectPoints) as instance methods to consume camera calibration values
    Can load calibration info from json file
    """

    def __init__(self, mtx, dst, img_size=[1280, 800], newmtx=None, roi=None):
        self.mtx = mtx
        self.dst = dst
        self.img_size = img_size
        self.newmtx = newmtx
        self.roi = [0, 0, img_size[0], img_size[1]]
        if roi is not None:
            self.roi = roi

    def undistort(self, img):
        # cv.undistort(	src, cameraMatrix, distCoeffs[, dst[, newCameraMatrix]]	) ->	dst
        dest = None
        ret = cv2.undistort(img, self.mtx, self.dst, dest, self.newmtx)
        x, y, w, h = self.roi
        return ret[y : y + h, x : x + w]

    def projectPoints(self, pts, rvec=[0, 0, 0], tvec=[0, 0, 0]):
        return cv2.projectPoints(pts, rvec, tvec, self.mtx, self.dst)

    def to_json(self):
        return json.dumps(
            {
                "mtx": self.mtx.ravel().tolist(),
                "dst": self.dst.ravel().tolist(),
                "img_size": self.img_size,
                "newmtx": self.newmtx.ravel().tolist(),
                "roi": self.roi,
            }
        )
    
    @staticmethod
    def load_cal_dict(c):
        #j = json.loads(jstr)
        mtx = np.array(c["mtx"]).reshape(3, 3)
        dst = np.array(c["dst"])
        img_size = c["img_size"]
        newmtx = np.array(c["newmtx"]).reshape(3, 3)
        roi = c["roi"]
        return CameraCalibration(mtx, dst, img_size, newmtx, roi)        

    @staticmethod
    def load_cal_json(jfile):
        """json calibration file parser
        this is a direct json import based on to_json format
        """
        j = json.loads(open(jfile, "r").read())
        mtx = np.array(j["mtx"]).reshape(3, 3)
        dst = np.array(j["dst"])
        img_size = j["img_size"]
        newmtx = np.array(j["newmtx"]).reshape(3, 3)
        roi = j["roi"]
        return CameraCalibration(mtx, dst, img_size, newmtx, roi)

    @staticmethod
    def load_cal_json_calibdb(jstr):
        """calibdb.net json output parser

        Args:
            jstr: json calibration string from calibdb.net

        Returns:
            CameraCalibration object
        """
        j = json.loads(jstr)
        mtx = np.array(j["camera_matrix"]).reshape(3, 3)
        dst = np.array(j["distortion_coefficients"])
        img_size = j["img_size"]
        newmtx, roi = cv2.getOptimalNewCameraMatrix(mtx, dst, img_size, 1, img_size)
        return CameraCalibration(mtx, dst, img_size, newmtx, roi)


# create image_cal and cam0 CameraCalibration instances on import
image_cal = CameraCalibration(
    mtx=np.array([1000, 0, 1280 / 2, 0, 1000, 720 / 2, 0, 0, 1]).reshape(3, 3), dst=np.float32([0, 0, 0, 0])
)
# cam0 = CameraCalibration.load_cal_json("left_camera_cal.json")  # load calibration data in CameraCalibration class

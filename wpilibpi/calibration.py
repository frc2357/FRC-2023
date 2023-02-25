# json cal info from calibration performed at https://www.calibdb.net/
# some code from https://docs.opencv.org/4.x/dc/dbb/tutorial_py_calibration.html
import numpy as np
import cv2
import glob
import json
import numpy as np
from functools import partial

cam0_json="""{
    "camera": "SPCA2688 AV Camera (1bcf:0b15)",
    "platform": "Windows NT 10.0; Win64; x64",
    "camera_matrix": {
        "type_id": "opencv-matrix",
        "rows": 3,
        "cols": 3,
        "dt": "d",
        "data": [
            1028.909042781684,
            0,
            638.5700108455009,
            0,
            1028.4927641450363,
            337.3638203241455,
            0,
            0,
            1
        ]
    },
    "distortion_coefficients": {
        "type_id": "opencv-matrix",
        "rows": 5,
        "cols": 1,
        "dt": "d",
        "data": [
            -0.028306160076120947,
            0.10007185327022922,
            -0.0008945602599457421,
            -0.003429637337026977,
            -0.37368732957571205
        ]
    },
    "distortion_model": "rectilinear",
    "avg_reprojection_error": 1.0892598742472148,
    "img_size": {
        "type_id": "opencv-matrix",
        "rows": 2,
        "cols": 1,
        "dt": "d",
        "data": [
            1280,
            720
        ]
    },
    "keyframes": 29,
    "calibration_time": "Tue, 14 Feb 2023 02:27:37 GMT"
}"""
#cal_cam0={"mtx":[1028.90904278,0.,638.57001085,0., 1028.49276415,337.36382032,0., 0., 1.],
#    "dst":[-0.028306160076120947, 0.10007185327022922, -0.0008945602599457421, -0.003429637337026977, -0.37368732957571205],
#    "img_size":[1280,720],
#    "newmtx":[964.6962890625, 0.0, 629.3880578097305,0.0, 962.7504272460938, 332.8399478557767,0.0, 0.0, 1.0],
#    "roi":[23, 17, 1224, 676]
#    }

class CameraCalibration:
    """ 
    Class to hold Calibration Coefficients and mapped undistort function
    Can load calibration info from json file
    """
    def __init__(self,mtx,dst,img_size=(1280,720),newmtx=None,roi=None):
        self.mtx = mtx
        self.dst = dst
        self.img_size = img_size
        self.newmtx = newmtx
        if roi is None:
            self.roi = [0,0,img_size[0],img_size[1]]

    def undistort(self,img):
        #cv.undistort(	src, cameraMatrix, distCoeffs[, dst[, newCameraMatrix]]	) ->	dst
        dest = None
        ret = cv2.undistort(img,self.mtx,self.dst,dest,self.newmtx)
        x, y, w, h = self.roi 
        return ret[y:y+h, x:x+w]

    def projectPoints(self, pts, rvec=[0, 0, 0], tvec=[0, 0, 0] ):
        #cv.projectPoints(	objectPoints, rvec, tvec, cameraMatrix, distCoeffs[, imagePoints[, jacobian[, aspectRatio]]]	) ->	imagePoints, jacobian
        return cv2.projectPoints(pts, rvec, tvec, self.mtx, self.dst)
    
    def to_json(self):
        return json.dumps({"mtx":self.mtx.ravel(),
                "dst":self.dst.ravel(),
                "img_size":[1280,720],
                "newmtx":self.newmtx.ravel(),
                "roi": self.roi})

    @staticmethod
    def load_cal_json(jfile):
        """ json calibration file parser
            this is a direct json import based on to_json format
        """
        j = json.loads(open(jfile,'r').read())
        mtx = np.array(j['mtx']).reshape(3,3)
        dst = np.array(j['dst'])
        img_size = j['image_size']
        newmtx = np.array(j['newmtx']).reshape(3,3)
        roi = j['roi']
        return CameraCalibration(mtx,dst,img_size,newmtx,roi)

    @staticmethod
    def load_cal_json_calibdb(jstr):
        """  calibdb.net json output parser
            
            Args:
                jstr: json calibration string from calibdb.net

            Returns:
                CameraCalibration object
        """
        j = json.loads(jstr)
        mtx = np.array(j['camera_matrix']['data']).reshape(3,3)
        dst = np.array(j['distortion_coefficients']['data'])
        img_size = j['img_size']['data']
        newmtx,roi = cv2.getOptimalNewCameraMatrix(mtx,dst,img_size,1,img_size)
        return CameraCalibration(mtx, dst, img_size, newmtx, roi)

# create image_cal and cam0 CameraCalibration instances on import
image_cal = CameraCalibration(mtx = np.array([1000, 0, 1280/2,0,1000,720/2,0,0,1]).reshape(3,3),dst=np.float32([0,0,0,0]))
cam0 = CameraCalibration.load_cal_json(cam0_json) # load calibration data in CameraCalibration class

   
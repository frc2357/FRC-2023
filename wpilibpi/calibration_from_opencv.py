
# code from https://docs.opencv.org/4.x/dc/dbb/tutorial_py_calibration.html
import numpy as np
import cv2
import glob

jsonstr="""{
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
cal_json="""{"mtx":[1028.90904278,0.,638.57001085,0., 1028.49276415,337.36382032,0., 0., 1.]],
    "dst":[-0.028306160076120947, 0.10007185327022922, -0.0008945602599457421, -0.003429637337026977, -0.37368732957571205],
    "img_size":[1280,720],
    "newmtx":[964.6962890625, 0.0, 629.3880578097305,0.0, 962.7504272460938, 332.8399478557767,0.0, 0.0, 1.0]],
    "roi":[23, 17, 1224, 676]
    }"""

# def perform_calibration(json=None,find_images=False):# termination criteria
#     criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 30, 0.001)
#     # prepare object points, like (0,0,0), (1,0,0), (2,0,0) ....,(6,5,0)
#     objp = np.zeros((6*7,3), np.float32)
#     objp[:,:2] = np.mgrid[0:7,0:6].T.reshape(-1,2)
#     # Arrays to store object points and image points from all the images.
#     objpoints = [] # 3d point in real world space
#     imgpoints = [] # 2d points in image plane.
    
#     if find_images:
#         images = glob.glob('*.jpg')

#         for fname in images:
#             img = cv2.imread(fname)
#             gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
#             # Find the chess board corners
#             ret, corners = cv2.findChessboardCorners(gray, (7,6), None)
#             # If found, add object points, image points (after refining them)
#             if ret == True:
#                 objpoints.append(objp)
#                 corners2 = cv2.cornerSubPix(gray,corners, (11,11), (-1,-1), criteria)
#                 imgpoints.append(corners2)
#                 # Draw and display the corners
#                 cv2.drawChessboardCorners(img, (7,6), corners2, ret)
#                 cv2.imshow('img', img)
#                 cv2.waitKey(500)
#         cv2.destroyAllWindows()
#         w,h,_ = img.shape
#     #calculate needed coeffs
#     ret, mtx, dst, rvecs, tvecs = cv2.calibrateCamera(objpoints, imgpoints, gray.shape[::-1], None, None)
#     newcameramtx, roi = cv2.getOptimalNewCameraMatrix(mtx, dst, (w,h), 1, (w,h))
#     np.savez("CAL.npz",mtx,dist,rvecs,tvecs,newcameramtx,roi)

#def load_cal_npz(filename='CAL.npz'):
#    with np.load(filename) as X:
#        mtx, dist, rvecs, tvecs, newcameramtx, roi= [X[i] for i in ('mtx','dist','rvecs','tvecs')]
#    return mtx, dist, newcameramtx, roi

class CameraCalibration:
    """ 
    Lightweight class to hold Calibration Coefficients and mapped undistort function
    using __slots__ is the method to make the most lightweight class in python
    """
    def __init__(self, mtx,dst,img_size=(1280,720),newmtx=None,roi=None):
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

def load_cal_json(jstr):
    # calibdb.net json output
    import json
    import numpy as np
    from functools import partial
    j = json.loads(jstr)
    mtx= np.array(j['camera_matrix']['data']).reshape(3,3)
    dst= np.array(j['distortion_coefficients']['data'])
    img_size = j['img_size']['data']
    newmtx,roi= cv2.getOptimalNewCameraMatrix(mtx,dst,img_size,1,img_size)
    return CameraCalibration(mtx, dst, img_size, newmtx,roi)

# create imag_cal and cam0 CameraCalibration instances on import
image_cal = CameraCalibration(mtx = np.array([1000, 0, 1280/2,0,1000,720/2,0,0,1]).reshape(3,3),dst=np.float32([0,0,0,0]))
cam0 = load_cal_json(jsonstr) # load calibration data in CameraCalibration class

   
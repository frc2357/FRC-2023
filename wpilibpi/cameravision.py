import glob
import json
import sys
import cv2
import numpy as np

# to use on your PC vs Raspberry PI, need numpy, robotpy, opencv installed to run
# See https://robotpy.readthedocs.io/en/stable/getting_started.html

from cscore import CameraServer, VideoSource, UsbCamera
from ntcore import NetworkTableInstance
from calibration import CameraCalibration, image_cal


class CameraConfig:
    # name: str
    # path: str
    # config: dict
    pass


class CameraObject:
    # team: int
    # config: dict
    # camera: object
    # sink: object
    # outstream: object
    # images: list
    # cal: dict
    pass


def list_cameras():
    """
    Helper function.  Will list all USB cameras in system.
    Can be used to generate json config file for use in CameraVision class
    """
    cams = []
    for c in UsbCamera.enumerateUsbCameras():
        cam = UsbCamera(c.name, c.path)
        ret = (c.name, c.path, cam.getConfigJsonObject())
        cams.append(ret)
        print(ret)
    return cams


class CameraVision:
    """
    A consolidated class to configure and start cameras, for use on wpilipbi / opencv

    Reads and processes a config file (json) to create CameraObject instances.
    Creates a networks table (client or server depending on config file).

    Can also be used in simulate mode to read images from disk in place of a Camera
    """

    # the following are singleton -  by defining them here, all instances of
    # CameraVision will share these variables
    configFile = None
    team = None
    server = False
    cameraConfigs = []
    cameras = []
    outputstream = None

    def __init__(self, configFile=".\\pc.json", simulate=False):
        self.configFile = configFile
        self.readConfig(configFile)  # sets team, server, cameraConfigs[]
        self.ntinst = ntinst = NetworkTableInstance.getDefault()
        CameraServer.enableLogging()
        self.outputstream = CameraServer.putVideo("Processed", 1280, 360)
        # TODO: define networks table variable for passing json
        # good start: https://docs.wpilib.org/en/stable/docs/software/networktables/client-side-program.html
        if self.server:
            print("Setting up NetworkTables server")
            ntinst.startServer()
        else:
            print("Setting up NetworkTables client for team {}".format(self.team))
            ntinst.startClient4("wpilibpi")
            ntinst.setServerTeam(self.team)
            ntinst.startDSClient()

        if simulate:  # simulate mode loads a set of images into the camera class
            imgs = []
            fimages = glob.glob("./images/*.png")
            print(fimages)

            for fname in fimages:
                i = cv2.imread(fname)
                cv2.putText(i, f"{fname}", (10, 20), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (0, 0, 0), 5)
                cv2.putText(i, f"{fname}", (10, 20), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (255, 255, 255), 1)
                imgs.append(np.ascontiguousarray(i))
            camConfig = CameraConfig()
            camConfig.width = imgs[0].shape[1]  # all images should be same dimensions!!!
            camConfig.height = imgs[0].shape[0]
            self.cameraConfigs.append(camConfig)

            c = CameraObject()
            c.camera = None
            c.config = camConfig
            c.sink = None
            c.outstream = CameraServer.putVideo("SIMULATE", camConfig.width, camConfig.height)
            c.images = imgs
            c.cal = image_cal  # bandaid for now
            self.cameras.append(c)
        else:  # real cameras
            for config in self.cameraConfigs:
                self.cameras.append(self.startCamera(config))

    def parseError(self, str) -> None:
        """Report parse error."""
        print("config error in '" + self.configFile + "': " + str, file=sys.stderr)

    def readCameraConfig(self, config) -> bool:
        """Read single camera configuration."""
        cam = CameraConfig()
        # name
        try:
            cam.name = config["name"]
        except KeyError:
            self.parseError("could not read camera name")
            return False
        # path
        try:
            cam.path = config["path"]
        except KeyError:
            self.parseError("camera '{}': could not read path".format(cam.name))
            return False
        try:
            cam.width = config["width"]
            cam.height = config["height"]
        except Exception:
            pass
        # stream properties
        cam.streamConfig = config.get("stream")
        cam.config = config
        try:
            cam.calibration = CameraCalibration.load_cal_dict(config["calibration"])
        except KeyError:
            cam.calibration = image_cal  # use default calibration
        self.cameraConfigs.append(cam)
        return True

    def readConfig(self, configFile=None) -> None:
        """Read configuration file (json)."""
        if configFile:
            self.configFile = configFile
        cfg = self.configFile
        # parse file
        try:
            with open(cfg, "rt", encoding="utf-8") as f:
                j = json.load(f)
        except OSError as err:
            print("could not open '{}': {}".format(cfg, err), file=sys.stderr)
            return False
        # top level must be an object
        if not isinstance(j, dict):
            self.parseError("must be JSON object")
            return False
        # team number
        try:
            self.team = j["team"]
        except KeyError:
            self.parseError("could not read team number")
            return False
        # ntmode (optional)
        if "ntmode" in j:
            str = j["ntmode"]
            if str.lower() == "client":
                self.server = False
            elif str.lower() == "server":
                self.server = True
            else:
                self.parseError("could not understand ntmode value '{}'".format(str))
        # cameras
        try:
            cameras = j["cameras"]
        except KeyError:
            self.parseError("could not read cameras")
            return False
        for camera in cameras:
            if not self.readCameraConfig(camera):
                return False
        return True

    def getComponents(self, cam_id=0):
        """get the sink and outstream for camera by index"""
        return (self.cameras[cam_id].sink, self.cameras[cam_id].outstream)

    def startCamera(self, config: CameraConfig) -> CameraObject:
        """Start running the camera.

        creates a connection to the camera, starts the CameraServer and
        references to the sink and outputstream.

        Args:
            config: CameraConfig class or dictionary

        Returns:
            configured CameraObject
        """

        print("Starting camera '{}' on {}".format(config.name, config.path))
        camera = UsbCamera(config.name, config.path)
        CameraServer.startAutomaticCapture(camera)

        camera.setConfigJson(json.dumps(config.config))
        camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kConnectionKeepOpen)

        # if config.streamConfig is not None:
        #    print(json.dumps(config.streamConfig))
        #    server.setConfigJson(json.dumps(config.streamConfig))

        camera.setConfigJson(config.config)
        # create object to hold camera state
        c = CameraObject()
        c.camera = camera
        c.config = config
        c.outstream = None  # not needed when using startAutomaticCapture CameraServer.putVideo(config.name, config.width, config.height)
        c.sink = CameraServer.getVideo(camera)
        c.cal = config.calibration
        return c


if __name__ == "__main__":
    import sys

    if len(sys.argv) == 1:
        print("usage: python cameravision.py cfgfile [sample_interval]")
        print("    where cfgfile is a json file to configure camera properties")
        print("    optional sample_interval, when provided enables storing camera frames to disk")
        print("    and is an integer representing the number of frames per capture")
        sys.exit()
    cfgfile = sys.argv[1]
    sample_interval = None
    if len(sys.argv) > 2:
        sample_interval = int(sys.argv[2])
    basename = "./images"
    c = CameraVision(cfgfile)
    frame = np.zeros(shape=(720, 1280, 3), dtype="uint8")
    count = 0
    while True:
        for id, cam in enumerate(c.cameras):
            cam.sink.grabFrame(frame)
            if sample_interval is not None and (count % sample_interval) == 0:
                fstr = f"{basename}/cam{id:02d}_{count}.png"
                print(f"writing {fstr}")
                cv2.imwrite(fstr, frame)
        count += 1

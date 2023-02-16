#!/usr/bin/env python3
import glob
import json
import time
import sys
import cv2
import numpy as np
# need numpy, robotpy, opencv installed to run

from cscore import CameraServer, VideoSource, UsbCamera, MjpegServer
from ntcore import NetworkTableInstance, EventFlags

class CameraConfig: pass

def list_cameras():
    """
    Helper function.  Will list all USB cameras in system.
    Can be used to generate json config file for use in CameraVision class
    """
    cams = []
    for c in UsbCamera.enumerateUsbCameras():
        cams.append(UsbCamera(c.name,c.path))
        cam = cams[-1]
        print(c.name,f"{c.path}")
        print(cam.getConfigJsonObject())
    return cams

class CameraObject:
    """ a lightweight class for cameras """
    pass

class CameraVision:
    """
    A consolidated class to configure and start cameras, for use on wpilipbi / opencv
    """
    
    # the following are singletons - i.e. we don't want to create more instances
    #__slots__ = ["configFile","team","server","cameraConfigs","cameras","sinks","outstreams"]
    configFile = None
    team = None
    server = False 
    cameraConfigs = []
    cameras = []
    outstreams = []

    def __init__(self, configFile=".\\pc.json", simulate=False):
        self.configFile = configFile 
        #self.team = None
        #self.server = False
        #self.cameraConfigs = []
        #self.cameras = []
        #self.outstreams = []
        self.readConfig(configFile)
        ntinst = NetworkTableInstance.getDefault()
        if self.server:
            print("Setting up NetworkTables server")
            ntinst.startServer()
        else:
            print("Setting up NetworkTables client for team {}".format(self.team))
            ntinst.startClient4("wpilibpi")
            ntinst.setServerTeam(self.team)
            ntinst.startDSClient()

        if simulate:
            imgs = []
            fimages = glob.glob(".\images\*.png")
            print(fimages)
            for fname in fimages:
                imgs.append(np.ascontiguousarray(cv2.imread(fname)))
            camConfig = dict()
            camConfig['width'] = imgs[0].shape[1]
            camConfig["height"] = imgs[0].shape[0]    
            self.cameraConfigs.append(camConfig)   
            c = CameraObject()
            c.camera = None
            c.config = camConfig 
            c.sink = None
            c.outstream = CameraServer.putVideo("VideoStream", camConfig["width"], camConfig["height"])
            c.images = imgs
            self.cameras.append(c)
        else:
            for config in self.cameraConfigs:
                self.cameras.append(self.startCamera(config))        

    def parseError(self, str)->None:
        """Report parse error."""
        print("config error in '" + self.configFile + "': " + str, file=sys.stderr)
    
    def readCameraConfig(self, config)->None:
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
        except:
            pass
        # stream properties
        cam.streamConfig = config.get("stream")
        cam.config = config
        self.cameraConfigs.append(cam)
        return True
    
    def readConfig(self, configFile=None)->None:
        """Read configuration file."""
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
        return (CameraServer.getVideo(self.cameras[cam_id]), self.outstreams[cam_id])

    def startCamera(self, config)->None:
        """Start running the camera."""
        
        print("Starting camera '{}' on {}".format(config.name, config.path))
        camera = UsbCamera(config.name, config.path)
        server = CameraServer.startAutomaticCapture(camera=camera)

        camera.setConfigJson(json.dumps(config.config))
        camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kConnectionKeepOpen)

        if config.streamConfig is not None:
            server.setConfigJson(json.dumps(config.streamConfig))

        CameraServer.enableLogging()
        #frame = np.zeros(shape=(camConfig["height"], camConfig["width"], 3), dtype=np.uint8)
        #blackFrame = np.zeros(shape=(camConfig["height"], camConfig["width"], 3), dtype=np.uint8)
        camConfig = camera.getConfigJsonObject()
        outputStream = CameraServer.putVideo("VideoStream", camConfig["width"], camConfig["height"])
        c = CameraObject()
        c.camera = camera
        c.config = camConfig 
        c.outstream = outputStream 
        c.sink = CameraServer.getVideo(camera)
        return c


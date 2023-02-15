#!/usr/bin/env python3

# Copyright (c) FIRST and other WPILib contributors.
# Open Source Software; you can modify and/or share it under the terms of
# the WPILib BSD license file in the root directory of this project.

import json
import time
import sys
import cv2
import numpy as np

from cscore import CameraServer, VideoSource, UsbCamera, MjpegServer
from ntcore import NetworkTableInstance, EventFlags

CHECKERBOARD = (7,10)

class CameraConfig: pass

def list_cameras():
    cams = []
    for c in UsbCamera.enumerateUsbCameras():
        cams.append(UsbCamera(c.name,c.path))
        cam = cams[-1]
        print(c.name,f"{c.path}")
        print(cam.getConfigJsonObject())
    return cams

class CameraVision:
    """
    A consolidated class to configure and start cameras, for use on wpilipbi / opencv
    """
    
    # the following are singletons - i.e. we don't want to create more instances
    __slots__ = ["configFile","team","server","cameraConfigs","cameras","sinks","outstreams"]

    def __init__(self, configFile=".\\pc.json"):
        self.configFile = configFile 
        self.team = None
        self.server = False
        self.cameraConfigs = []
        self.cameras = []
        self.outstreams = []
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

        # start cameras
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

        self.cameras.append(camera)
        self.outstreams.append(outputStream)
        #return { "camera": camera, "sink": CameraServer.getVideo(camera=camera) }


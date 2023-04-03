package com.team2357.frc2023.controls;

import com.team2357.frc2023.Constants;

import edu.wpi.first.wpilibj.XboxController;

public class ControllerManager {
    private static ControllerManager m_instance;

    public static ControllerManager getInstance() {
        if (m_instance == null) {
            m_instance = new ControllerManager();
        }
        return m_instance;
    }

    private XboxController m_driveController;
    private XboxController m_gunnerController;

    public ControllerManager() {
        m_driveController = new XboxController(Constants.CONTROLLER.DRIVE_CONTROLLER_PORT);
        m_gunnerController = new XboxController(Constants.CONTROLLER.GUNNER_CONTROLLER_PORT);
    }

    public XboxController getDriveController() {
        return m_driveController;
    }

    public XboxController getGunnerController() {
        return m_gunnerController;
    }
}

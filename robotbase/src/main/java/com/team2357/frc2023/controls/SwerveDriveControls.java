package com.team2357.frc2023.controls;

import edu.wpi.first.wpilibj.XboxController;

public class SwerveDriveControls {
    private XboxController m_controller;
    private double m_deadband;

    public SwerveDriveControls(XboxController controller, double deadband) {
        m_controller = controller;
        m_deadband = deadband;
    }

    public double getX() {
        return -modifyAxis(m_controller.getLeftX());
    }

    public double getY() {
        return -modifyAxis(m_controller.getLeftY());
    }

    public double getRotation() {
        return -modifyAxis(m_controller.getRightX());
    }

    public double deadband(double value, double deadband) {
        if (Math.abs(value) > deadband) {
            if (value > 0.0) {
                return (value - deadband) / (1.0 - deadband);
            } else {
                return (value + deadband) / (1.0 - deadband);
            }
        } else {
            return 0.0;
        }
    }

    public double modifyAxis(double value) {
        value = deadband(value, m_deadband);
        value = Math.copySign(value * value, value);
        return value;
    }
}

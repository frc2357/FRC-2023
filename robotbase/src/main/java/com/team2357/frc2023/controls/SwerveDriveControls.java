package com.team2357.frc2023.controls;

import com.team2357.frc2023.commands.AutoBalanceCommand;
import com.team2357.frc2023.commands.ReverseIntakeCommand;
import com.team2357.frc2023.commands.RunIntakeCommand;
import com.team2357.frc2023.subsystems.IntakeSubsystem;
import com.team2357.frc2023.commands.auto.TranslateToAprilTag;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.util.XboxRaw;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class SwerveDriveControls {
    private XboxController m_controller;
    private double m_deadband;
    private JoystickButton m_backButton;
    private JoystickButton m_rightBumper;
    private JoystickButton m_leftBumper;

    public static boolean isFlipped;

    private JoystickButton m_button;
    public SwerveDriveControls(XboxController controller, double deadband) {
        m_controller = controller;
        m_deadband = deadband;
        m_backButton = new JoystickButton(m_controller, XboxRaw.Back.value);
        m_rightBumper = new JoystickButton(m_controller, XboxRaw.BumperRight.value);
        m_leftBumper = new JoystickButton(m_controller, XboxRaw.BumperLeft.value);

        m_backButton.onTrue(new InstantCommand(() -> SwerveDriveSubsystem.getInstance().zeroGyroscope()));

        m_rightBumper.whileTrue(new RunIntakeCommand());
        m_leftBumper.whileTrue(new ReverseIntakeCommand());
        m_button.whileTrue(new TranslateToAprilTag());
    }

    public double getX() {
        if (isFlipped) {
            return modifyAxis(m_controller.getLeftX());
        }
        return -modifyAxis(m_controller.getLeftX());
    }

    public double getY() {
        if (isFlipped) {
            return modifyAxis(m_controller.getLeftY());
        }
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

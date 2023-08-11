package com.team2357.frc2023.controls;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.cubeBotIntake.IntakeEjectCubeCommand;
import com.team2357.frc2023.commands.cubeBotIntake.IntakeIndexCubeCommand;
import com.team2357.frc2023.commands.cubeBotIntake.IntakePickupCubeCommand;
import com.team2357.frc2023.commands.cubeBotIntake.IntakeRollCubeCommand;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.triggers.AxisThresholdTrigger;
import com.team2357.lib.util.XboxRaw;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Axis;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class SwerveDriveControls implements RumbleInterface {
    private static SwerveDriveControls s_instance;

    public static SwerveDriveControls getInstance() {
        return s_instance;
    }

    private XboxController m_controller;
    private double m_deadband;

    private JoystickButton m_backButton;
    private JoystickButton m_aButton;
    private JoystickButton m_bButton;
    private JoystickButton m_startButton;

    private AxisThresholdTrigger m_rightTrigger;
    private AxisThresholdTrigger m_leftTrigger;
    private JoystickButton m_rightBumper;
    private JoystickButton m_leftBumper;

    private boolean m_elementary = false;

    public static boolean isFlipped;

    public SwerveDriveControls(XboxController controller, double deadband) {
        s_instance = this;

        m_controller = controller;
        m_deadband = deadband;

        m_rightTrigger = new AxisThresholdTrigger(m_controller, Axis.kRightTrigger, 0.0);
        m_leftTrigger = new AxisThresholdTrigger(m_controller, Axis.kLeftTrigger, 0.0);
        m_rightBumper = new JoystickButton(m_controller, XboxRaw.BumperRight.value);
        m_leftBumper = new JoystickButton(m_controller, XboxRaw.BumperLeft.value);

        m_backButton = new JoystickButton(m_controller, XboxRaw.Back.value);
        m_startButton = new JoystickButton(m_controller, XboxRaw.Start.value);

        mapControls();
    }

    public void mapControls() {
        // Zero swerve drive
        m_backButton.whileTrue(new InstantCommand(() -> SwerveDriveSubsystem.getInstance().zeroGyroscope()));
        m_startButton.whileTrue(new InstantCommand(() -> SwerveDriveSubsystem.getInstance().setGyroScope(180)));

        m_rightTrigger.whileTrue(new IntakePickupCubeCommand());
        m_leftTrigger.whileTrue(new IntakeEjectCubeCommand());
        m_rightBumper.whileTrue(new IntakeRollCubeCommand());
        m_leftBumper.whileTrue(new IntakeIndexCubeCommand());
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

    public double applyElementaryReduction(double value) {
        return value / Constants.DRIVE.ELEMENTARY_DRIVE_REDUCTION;
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

        if (m_elementary) {
            return applyElementaryReduction(value);
        }
        return value;
    }

    public void setRumble(RumbleType type, double intensity) {
        m_controller.setRumble(type, intensity);
    }

    public void toggleElementary() {
        m_elementary = !m_elementary;
    }
}

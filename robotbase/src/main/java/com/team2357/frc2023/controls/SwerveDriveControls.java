package com.team2357.frc2023.controls;

import com.team2357.frc2023.arduino.GamepieceLED;
import com.team2357.frc2023.arduino.GamepieceLED.SIGNAL_COLOR;
import com.team2357.frc2023.commands.drive.Test1AutoBalanceCommand;
import com.team2357.frc2023.commands.drive.Test2AutoBalanceCommand;
import com.team2357.frc2023.commands.intake.IntakeDeployCommandGroup;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerRunCommand;
import com.team2357.frc2023.commands.intake.IntakeStowCommandGroup;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.triggers.AxisThresholdTrigger;
import com.team2357.lib.util.XboxRaw;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Axis;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class SwerveDriveControls {
    private XboxController m_controller;
    private double m_deadband;

    private JoystickButton m_backButton;
    private JoystickButton m_rightBumper;
    private JoystickButton m_leftBumper;
    private JoystickButton m_aButton;
    private JoystickButton m_bButton;
    private JoystickButton m_yButton;
    private JoystickButton m_startButton;

    private AxisThresholdTrigger m_leftTrigger;
    private AxisThresholdTrigger m_rightTrigger;

    public static boolean isFlipped;

    public SwerveDriveControls(XboxController controller, double deadband) {
        m_controller = controller;
        m_deadband = deadband;

        m_aButton = new JoystickButton(m_controller, XboxRaw.A.value);
        m_bButton = new JoystickButton(m_controller, XboxRaw.B.value);
        m_yButton = new JoystickButton(m_controller, XboxRaw.Y.value);
        
        m_backButton = new JoystickButton(m_controller, XboxRaw.Back.value);
        m_startButton = new JoystickButton(m_controller, XboxRaw.Start.value);
        m_aButton = new JoystickButton(m_controller, XboxRaw.A.value);
        
        m_rightBumper = new JoystickButton(m_controller, XboxRaw.BumperRight.value);
        m_leftBumper = new JoystickButton(m_controller, XboxRaw.BumperLeft.value);

        m_rightTrigger = new AxisThresholdTrigger(m_controller, Axis.kRightTrigger, 0.05);
        m_leftTrigger = new AxisThresholdTrigger(m_controller, Axis.kLeftTrigger, 0.05);

        mapControls();
    }

    public void mapControls() {
        // Zero swerve drive
        m_backButton.whileTrue(new InstantCommand(() -> SwerveDriveSubsystem.getInstance().zeroGyroscope()));
        m_startButton.whileTrue(new InstantCommand(() -> SwerveDriveSubsystem.getInstance().setGyroScope(180)));
        // Intake commands
        // TODO: Remove these bindings
        m_rightBumper.whileTrue(new IntakeRollerRunCommand());
        m_leftBumper.whileTrue(new IntakeRollerReverseCommand());

        // Intake deploy/stow
        m_leftTrigger.whileTrue(new IntakeDeployCommandGroup().alongWith(new InstantCommand(() -> GamepieceLED.getInstance().setSignalColor(SIGNAL_COLOR.PURPLE))));
        m_leftTrigger.onFalse(new IntakeStowCommandGroup());

        m_rightTrigger.whileTrue(new IntakeDeployCommandGroup().alongWith(new InstantCommand(() -> GamepieceLED.getInstance().setSignalColor(SIGNAL_COLOR.YELLOW))));
        m_rightTrigger.onFalse(new IntakeStowCommandGroup());

        m_aButton.whileTrue(new Test1AutoBalanceCommand());
        m_bButton.whileTrue(new Test2AutoBalanceCommand());

        //Teleop auto
        //m_rightBumper.whileTrue(new HeartlandAutoTranslateCommand(m_controller));
        //m_leftBumper.whileTrue(new HeartlandAutoScoreCommand());

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

package com.team2357.frc2023.controls;

import com.team2357.frc2023.commands.drive.ToggleRobotCentricDriveCommand;
import com.team2357.frc2023.commands.intake.IntakeConeCommandGroup;
import com.team2357.frc2023.commands.intake.IntakeCubeCommandGroup;
import com.team2357.frc2023.commands.intake.IntakePreSignalConeCommandGroup;
import com.team2357.frc2023.commands.intake.IntakePreSignalCubeCommandGroup;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.triggers.AxisThresholdTrigger;
import com.team2357.lib.util.XboxRaw;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Axis;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class SwerveDriveControls implements RumbleInterface {
    private static SwerveDriveControls s_instance;

    public static SwerveDriveControls getInstance() {
        return s_instance;
    }

    private SwerveDriveSubsystem m_swerve;

    private XboxController m_controller;
    private double m_deadband;

    private JoystickButton m_backButton;
    private JoystickButton m_rightBumper;
    private JoystickButton m_leftBumper;
    private JoystickButton m_aButton;
    private JoystickButton m_bButton;
    private JoystickButton m_yButton;
    private JoystickButton m_startButton;

    private Trigger m_rightJoystickButton;

    private AxisThresholdTrigger m_leftTriggerPre;
    private AxisThresholdTrigger m_leftTriggerFull;
    private AxisThresholdTrigger m_rightTriggerPre;
    private AxisThresholdTrigger m_rightTriggerFull;

    private ProfiledPIDController m_directionController;

    public static boolean isFlipped;

    public SwerveDriveControls(XboxController controller, double deadband) {
        s_instance = this;

        m_swerve = SwerveDriveSubsystem.getInstance();

        m_directionController = m_swerve.getAutoAlignThetaController();
        m_directionController.enableContinuousInput(-Math.PI, Math.PI);

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

        m_rightTriggerPre = new AxisThresholdTrigger(m_controller, Axis.kRightTrigger, 0.05);
        m_rightTriggerFull = new AxisThresholdTrigger(m_controller, Axis.kRightTrigger, 0.75);

        m_leftTriggerPre = new AxisThresholdTrigger(m_controller, Axis.kLeftTrigger, 0.05);
        m_leftTriggerFull = new AxisThresholdTrigger(m_controller, Axis.kLeftTrigger, 0.75);

        m_rightJoystickButton = new Trigger(() -> m_controller.getRightStickButtonPressed());

        mapControls();
    }

    public void mapControls() {
        // Zero swerve drive
        m_backButton.whileTrue(new InstantCommand(() -> m_swerve.zeroGyroscope()));
        m_startButton.whileTrue(new InstantCommand(() -> m_swerve.setGyroScope(180)));

        // Intake commands

        // Intake pre-signal (for human player)
        m_leftTriggerPre.onTrue(new IntakePreSignalConeCommandGroup());
        m_rightTriggerPre.onTrue(new IntakePreSignalCubeCommandGroup());

        // Cone Intake deploy/stow
        m_leftTriggerFull.whileTrue(new IntakeConeCommandGroup());

        // Cone Intake deploy/stow
        m_rightTriggerFull.whileTrue(new IntakeCubeCommandGroup());

        m_aButton.onTrue(new ToggleRobotCentricDriveCommand());

        m_rightJoystickButton.onTrue(new InstantCommand(() -> {
            m_directionController.reset(m_swerve.getPose().getRotation().getRadians());
        }));
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
        if (!m_controller.getRightStickButton()) {
            return -modifyAxis(m_controller.getRightX());
        }

        double currentRotation = m_swerve.getPose().getRotation().getRadians();
        double targetRotation = getNearestCardinalDirection(m_swerve.getYaw0To360());

        double velocity = m_directionController.calculate(currentRotation, targetRotation);
        velocity = m_directionController.atGoal() ? 0 : velocity;

        return velocity;
    }

    // Cardinal direction relative to field
    public double getNearestCardinalDirection(double yaw) {
        double direction = Math.PI; // Default to face grids (South)
        if ((0 <= yaw && yaw < 45) || (315 <= yaw && yaw <= 360)) { // North
			direction = 0;
		} else if (45 <= yaw && yaw < 135) { // East
			direction = Math.PI/2;
		} else if (135 <= yaw && yaw < 225) { // South
			direction = Math.PI;
		} else if (225 <= yaw && yaw < 315) { // West
			direction = -Math.PI/2;
		}
		return direction;
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

    public void setRumble(RumbleType type, double intensity) {
        m_controller.setRumble(type, intensity);
    }
}

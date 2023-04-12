package com.team2357.frc2023.controls;

import com.team2357.frc2023.Constants.CONTROLLER;
import com.team2357.frc2023.commands.armextension.ArmExtendAmpZeroCommand;
import com.team2357.frc2023.commands.human.panic.ArmExtensionAxisCommand;
import com.team2357.frc2023.commands.human.panic.ArmRotationAxisCommand;
import com.team2357.frc2023.commands.human.panic.EverybotClawRollerAxisCommand;
import com.team2357.frc2023.commands.human.panic.EverybotWristAxisCommand;
import com.team2357.frc2023.commands.human.panic.IntakeArmToggleCommand;
import com.team2357.frc2023.commands.human.panic.IntakeRollerAxisCommand;
import com.team2357.frc2023.commands.human.panic.IntakeWinchAxisCommand;
import com.team2357.frc2023.commands.intake.IntakeConeCommandGroup;
import com.team2357.frc2023.commands.intake.IntakeCubeCommandGroup;
import com.team2357.frc2023.commands.intake.IntakePreSignalConeCommandGroup;
import com.team2357.frc2023.commands.intake.IntakePreSignalCubeCommandGroup;
import com.team2357.frc2023.commands.intake.WinchAmpZeroCommand;
import com.team2357.frc2023.commands.scoring.AutoLineupCommand;
import com.team2357.frc2023.commands.scoring.DumpGamePieceCommand;
import com.team2357.frc2023.commands.scoring.GunnerAutoScoreCommand;
import com.team2357.frc2023.commands.scoring.GunnerScoreHighCommand;
import com.team2357.frc2023.commands.scoring.GunnerScoreLowCommand;
import com.team2357.frc2023.commands.scoring.GunnerScoreMidCommand;
import com.team2357.frc2023.commands.scoring.HomeMechanismsCommand;
import com.team2357.frc2023.commands.util.ZeroAllCommand;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;
import com.team2357.lib.triggers.AxisThresholdTrigger;
import com.team2357.lib.util.Utility;
import com.team2357.lib.util.XboxRaw;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Axis;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

// ! GUNNER CONTROLLER IS FOR ROBOTICS TEAM MEMBERS ONLY! NO CHILDREN

/**
 * These are the controls for the gunner.
 * 
 * @category Drive
 */
public class GunnerControls implements RumbleInterface {

    private static GunnerControls s_instance;

    public static GunnerControls getInstance() {
        return s_instance;
    }

    XboxController m_controller;

    // Triggers
    public AxisThresholdTrigger m_leftTrigger;
    public AxisThresholdTrigger m_rightTrigger;

    // Buttons
    public JoystickButton m_leftStickButton;
    public JoystickButton m_backButton;
    public JoystickButton m_startButton;
    public JoystickButton m_leftBumper;
    public JoystickButton m_rightBumper;
    public Trigger m_aButton;
    public Trigger m_bButton;
    public Trigger m_xButton;
    public Trigger m_yButton;

    // Dpad
    public POVButton m_upDPad;
    public POVButton m_rightDPad;
    public POVButton m_downDPad;
    public POVButton m_leftDPad;

    private AxisThresholdTrigger m_leftTriggerPre;
    private AxisThresholdTrigger m_leftTriggerFull;
    private AxisThresholdTrigger m_rightTriggerPre;
    private AxisThresholdTrigger m_rightTriggerFull;

    /**
     * @param builder The GunnerControlsBuilder object
     */
    public GunnerControls(XboxController controller) {
        s_instance = this;

        m_controller = controller;

        m_rightTriggerPre = new AxisThresholdTrigger(m_controller, Axis.kRightTrigger, 0.05);
        m_rightTriggerFull = new AxisThresholdTrigger(m_controller, Axis.kRightTrigger, 0.75);

        m_leftTriggerPre = new AxisThresholdTrigger(m_controller, Axis.kLeftTrigger, 0.05);
        m_leftTriggerFull = new AxisThresholdTrigger(m_controller, Axis.kLeftTrigger, 0.75);

        // Triggers
        m_leftTrigger = new AxisThresholdTrigger(controller, Axis.kLeftTrigger, .1);
        m_rightTrigger = new AxisThresholdTrigger(controller, Axis.kRightTrigger, .1);

        // Buttons
        m_leftStickButton = new JoystickButton(controller, XboxRaw.StickPressLeft.value);
        m_backButton = new JoystickButton(controller, XboxRaw.Back.value);
        m_startButton = new JoystickButton(controller, XboxRaw.Start.value);
        m_leftBumper = new JoystickButton(controller, XboxRaw.BumperLeft.value);
        m_rightBumper = new JoystickButton(controller, XboxRaw.BumperRight.value);
        m_aButton = new JoystickButton(controller, XboxRaw.A.value);
        m_bButton = new JoystickButton(controller, XboxRaw.B.value);
        m_xButton = new JoystickButton(controller, XboxRaw.X.value);
        m_yButton = new JoystickButton(controller, XboxRaw.Y.value);

        // Dpad
        m_upDPad = new POVButton(controller, 0);
        m_rightDPad = new POVButton(controller, 90);
        m_downDPad = new POVButton(controller, 180);
        m_leftDPad = new POVButton(controller, 270);

        mapControls();
    }

    public double getLeftXAxis() {
        double value = m_controller.getLeftX();
        return Utility.deadband(value, CONTROLLER.GUNNER_CONTROLLER_DEADBAND);
    }

    public double getRightYAxis() {
        double value = m_controller.getRightY();
        return Utility.deadband(value, CONTROLLER.GUNNER_CONTROLLER_DEADBAND);
    }

    public double getRightTriggerAxis() {
        return m_controller.getRightTriggerAxis();
    }

    public double getLeftTriggerAxis() {
        return m_controller.getLeftTriggerAxis();
    }

    private void mapControls() {

        // Intake pre-signal (for human player)
        m_leftTriggerPre.onTrue(new IntakePreSignalConeCommandGroup());
        m_rightTriggerPre.onTrue(new IntakePreSignalCubeCommandGroup());

        // Cone Intake deploy/stow
        m_leftTriggerFull.whileTrue(new IntakeConeCommandGroup());

        // Cone Intake deploy/stow
        m_rightTriggerFull.whileTrue(new IntakeCubeCommandGroup());

        AxisInterface axisLeftStickX = () -> {
            return getLeftXAxis();
        };

        AxisInterface axisRightStickY = () -> {
            return getRightYAxis();
        };

        AxisInterface intakeRollerForwardAxis = () -> {
            return getRightTriggerAxis();
        };

        AxisInterface intakeRollerReverseAxis = () -> {
            return -getLeftTriggerAxis();
        };

        Trigger noDPad = new Trigger(() -> m_upDPad.getAsBoolean() || m_rightDPad.getAsBoolean()
                || m_downDPad.getAsBoolean() || m_leftDPad.getAsBoolean()).negate();

        Trigger noLetterButtons = m_aButton.or(m_bButton).or(m_xButton).or(m_yButton).negate();
        Trigger upDPadOnly = m_upDPad.and(noLetterButtons);
        Trigger downDPadOnly = m_downDPad.and(noLetterButtons);
        Trigger leftDPadOnly = m_leftDPad.and(noLetterButtons);
        Trigger rightDPadOnly = m_rightDPad.and(noLetterButtons);

        Trigger upDPadAndA = m_upDPad.and(m_aButton);

        Trigger upDPadAndX = m_upDPad.and(m_xButton);
        Trigger upDPadAndY = m_upDPad.and(m_yButton);
        Trigger upDPadAndB = m_upDPad.and(m_bButton);

        Trigger downDPadAndA = m_downDPad.and(m_aButton);
        Trigger downDPadAndX = m_downDPad.and(m_xButton);
        Trigger downDPadAndY = m_downDPad.and(m_yButton);
        Trigger downDPadAndB = m_downDPad.and(m_bButton);

        Trigger leftDPadAndA = m_leftDPad.and(m_aButton);
        Trigger leftDPadAndX = m_leftDPad.and(m_xButton);
        Trigger leftDPadAndY = m_leftDPad.and(m_yButton);
        Trigger leftDPadAndB = m_leftDPad.and(m_bButton);

        Trigger rightDPadAndA = m_rightDPad.and(m_aButton);
        Trigger rightDPadAndX = m_rightDPad.and(m_xButton);
        Trigger rightDPadAndY = m_rightDPad.and(m_yButton);
        Trigger rightDPadAndB = m_rightDPad.and(m_bButton);
        Trigger rightDPadAndRightTrigger = m_rightDPad.and(m_rightTrigger);
        Trigger rightDPadAndLeftTrigger = m_rightDPad.and(m_leftTrigger);

        Trigger downDPadAndRightTrigger = m_downDPad.and(m_rightTrigger);
        Trigger downDPadAndLeftTrigger = m_downDPad.and(m_leftTrigger);

        Trigger aButton = m_aButton.and(noDPad);
        Trigger bButton = m_bButton.and(noDPad);
        Trigger yButton = m_yButton.and(noDPad);
        Trigger xButton = m_xButton.and(noDPad);

        // Arm rotation
        upDPadOnly.whileTrue(new ArmRotationAxisCommand(axisRightStickY));

        upDPadAndY.onTrue(new InstantCommand(() -> {
            ArmRotationSubsystem.getInstance().resetEncoder();
        }));

        // Arm extension / claw / wrist
        leftDPadOnly.whileTrue(new ArmExtensionAxisCommand(axisRightStickY));

        // leftDPadAndA.onTrue(new WristToggleCommand());
        // leftDPadAndB.onTrue(new ClawToggleCommand());

        leftDPadAndY.onTrue(new ArmExtendAmpZeroCommand());

        // Intake
        rightDPadOnly.whileTrue(new IntakeWinchAxisCommand(axisRightStickY));

        rightDPadAndRightTrigger.whileTrue(new IntakeRollerAxisCommand(intakeRollerForwardAxis));
        rightDPadAndLeftTrigger.whileTrue(new IntakeRollerAxisCommand(intakeRollerReverseAxis));

        // End effector
        downDPadAndRightTrigger.whileTrue(new EverybotClawRollerAxisCommand(intakeRollerForwardAxis));
        downDPadAndLeftTrigger.whileTrue(new EverybotClawRollerAxisCommand(intakeRollerReverseAxis));
        downDPadOnly.whileTrue(new EverybotWristAxisCommand(axisRightStickY));

        rightDPadAndA.onTrue(new IntakeArmToggleCommand());
        rightDPadAndY.onTrue(new WinchAmpZeroCommand());

        // Auto score
        yButton.whileTrue(new GunnerScoreHighCommand());
        xButton.whileTrue(new GunnerScoreMidCommand());
        aButton.whileTrue(new GunnerScoreLowCommand());
        bButton.whileTrue(new DumpGamePieceCommand());

        m_leftBumper.whileTrue(new AutoLineupCommand());
        m_rightBumper.whileTrue(new GunnerAutoScoreCommand());

        // Zero all
        m_backButton.whileTrue(new ZeroAllCommand());

        m_startButton.whileTrue(new HomeMechanismsCommand());
    }

    public void setRumble(RumbleType type, double intensity) {
        m_controller.setRumble(type, intensity);
    }
}
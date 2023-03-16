package com.team2357.frc2023.commands.scoring.cone;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armextension.ArmExtendToPositionCommand;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.claw.ClawInstantCloseCommand;
import com.team2357.frc2023.commands.claw.ClawInstantOpenCommand;
import com.team2357.frc2023.commands.intake.IntakeArmStowCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerStopCommand;
import com.team2357.frc2023.commands.intake.WinchRotateToPositionCommand;
import com.team2357.frc2023.commands.wrist.WristInstantExtendCommand;
import com.team2357.frc2023.commands.wrist.WristInstantRetractCommand;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class ConeAutoScoreHighCommandGroup extends ParallelCommandGroup {

        public ConeAutoScoreHighCommandGroup() {
                this(true);
        }

        public ConeAutoScoreHighCommandGroup(boolean stowIntake) {

                Command intakeStow = new WaitCommand(0);
                if (stowIntake) {
                        intakeStow = new IntakeArmStowCommand();
                }

                addCommands(
                        // Arm Rotation
                        new WaitCommand(0.3)
                                        .andThen(new ArmRotateToPositionCommand(
                                                        Constants.ARM_ROTATION.AUTO_SCORE_HIGH_ROTATIONS))
                                        .andThen(new WaitCommand(2.5))
                                        .andThen(new ArmRotateToPositionCommand(
                                                        Constants.ARM_ROTATION.RETRACTED_ROTATIONS)),

                        // Wrist
                        new WaitCommand(0.9)
                                        .andThen(new WristInstantExtendCommand())
                                        .andThen(new WaitCommand(2.5))
                                        .andThen(new WristInstantRetractCommand()),

                        // Arm Extension
                        new WaitCommand(1)
                                        .andThen(new ArmExtendToPositionCommand(
                                                        Constants.ARM_EXTENSION.AUTO_SCORE_HIGH_ROTATIONS)
                                                        .withTimeout(2))
                                        .andThen(new ArmExtendToPositionCommand(
                                                        Constants.ARM_EXTENSION.RETRACTED_ROTATIONS)),

                        // Clamp
                        new ClawInstantCloseCommand() // Ensure we're clamped while we swing the arm
                                        .andThen(new WaitCommand(2.8))
                                        .andThen(new ClawInstantOpenCommand())
                                        .andThen(new WaitCommand(0.6))
                                        .andThen(new ClawInstantCloseCommand()),

                        // Intake rotation
                        new WinchRotateToPositionCommand(Constants.INTAKE_ARM.INTAKE_HANDOFF_WINCH_ROTATIONS)
                                        .andThen(new WaitCommand(0.75))
                                        .andThen(intakeStow),

                        // Intake rollers
                        new IntakeRollerReverseCommand().withTimeout(1)
                                        .andThen(new IntakeRollerStopCommand())
                );
        }
}

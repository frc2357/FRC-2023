package com.team2357.frc2023.commands.scoring.cube;

import org.littletonrobotics.junction.Logger;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armextension.ArmExtendToPositionCommand;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.armrotation.ArmWaitForGreaterThanPositionCommand;
import com.team2357.frc2023.commands.everybot.ClawHoldCubeCommand;
import com.team2357.frc2023.commands.everybot.ClawIntakeCubeCommand;
import com.team2357.frc2023.commands.everybot.WristRotateToPositionCommand;
import com.team2357.frc2023.commands.intake.IntakeArmStowCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.intake.WinchRotateToPositionCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.subsystems.ArmExtensionSubsystem;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;
import com.team2357.frc2023.subsystems.WristSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class CubeHighPrePoseCommand extends SequentialCommandGroup {
    public CubeHighPrePoseCommand() {
        super(
                new ParallelCommandGroup(
                        new SetRobotStateCommand(RobotState.State.ROBOT_PRE_SCORE_CUBE_HIGH),

            // Claw Rollers
            new SequentialCommandGroup(
                new ClawIntakeCubeCommand(),
                new ClawHoldCubeCommand()
            ),

            // Intake Rollers
            new IntakeRollerReverseCommand().withTimeout(1),

            // Intake Arm
            new SequentialCommandGroup(
                new WaitCommand(0.5),
                new WinchRotateToPositionCommand(Constants.INTAKE_ARM.INTAKE_HANDOFF_WINCH_ROTATIONS),
                new WaitCommand(0.25),
                new IntakeArmStowCommand()
            ),

            // Arm
            new ArmRotateToPositionCommand(Constants.ARM_ROTATION.SCORE_CUBE_HIGH_ROTATIONS),

            // Wrist
            new SequentialCommandGroup(
                new ArmWaitForGreaterThanPositionCommand(Constants.ARM_ROTATION.WRIST_CLEAR_INTAKE_ROTATIONS),
                new WristRotateToPositionCommand(Constants.WRIST.SCORE_CUBE_HIGH_ROTATIONS)
            ),

            // Extension
            new SequentialCommandGroup(
                new ArmWaitForGreaterThanPositionCommand(Constants.ARM_ROTATION.EXTENSION_HIGH_START_ROTATIONS),
                new ArmExtendToPositionCommand(Constants.ARM_EXTENSION.SCORE_CUBE_HIGH_ROTATIONS)
            ),
                new InstantCommand(() -> Logger.getInstance().recordOutput("Pre Pose/Cube High prePose",
                        new double[] { ArmRotationSubsystem.getInstance().getMotorRotations(),
                                ArmExtensionSubsystem.getInstance().getMotorRotations(),
                                WristSubsystem.getInstance().getRotations() }))));
    }
}

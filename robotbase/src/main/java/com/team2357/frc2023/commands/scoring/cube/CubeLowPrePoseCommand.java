package com.team2357.frc2023.commands.scoring.cube;

import org.littletonrobotics.junction.Logger;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.subsystems.ArmExtensionSubsystem;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;
import com.team2357.frc2023.subsystems.WristSubsystem;
import com.team2357.frc2023.commands.everybot.ClawReleaseCubeCommand;
import com.team2357.frc2023.commands.intake.WinchRotateToPositionCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class CubeLowPrePoseCommand extends SequentialCommandGroup {
    public CubeLowPrePoseCommand() {
        super(
                new ParallelCommandGroup(
                        new SetRobotStateCommand(RobotState.State.ROBOT_PRE_SCORE_CUBE_LOW),
                new WinchRotateToPositionCommand(Constants.INTAKE_ARM.AUTO_SCORE_LOW_ROTATIONS),
                new ParallelDeadlineGroup(
                    new WaitCommand(0.25),
                    new ClawReleaseCubeCommand()
                )
            ),
                new InstantCommand(() -> Logger.getInstance().recordOutput("Pre Pose/Cube Low prePose",
                        new double[] { ArmRotationSubsystem.getInstance().getMotorRotations(),
                                ArmExtensionSubsystem.getInstance().getMotorRotations(),
                                WristSubsystem.getInstance().getRotations() })));
    }
}

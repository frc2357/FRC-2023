package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.commands.intake.IntakeArmStowCommand;

import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.everybot.ClawReleaseCubeCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.intake.WinchRotateToPositionCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;


public class DumpGamePieceCommand extends SequentialCommandGroup {
    public DumpGamePieceCommand() {
        super(
            new ParallelCommandGroup(
                new WinchRotateToPositionCommand(Constants.INTAKE_ARM.DUMP_WINCH_ROTATIONS),
                new ArmRotateToPositionCommand(Constants.ARM_ROTATION.SCORE_CONE_LOW_ROTATIONS),
                new ClawReleaseCubeCommand(),
                new IntakeRollerReverseCommand()
            ).finallyDo(
                (boolean interrupted) -> {
                    new ParallelCommandGroup(
                        new ArmRotateToPositionCommand(Constants.ARM_ROTATION.RETRACTED_ROTATIONS),
                        new IntakeArmStowCommand(),
                        new SetRobotStateCommand(RobotState.State.ROBOT_STOWED_EMPTY)
                    ).schedule();
                }
            )
        );
    }
}

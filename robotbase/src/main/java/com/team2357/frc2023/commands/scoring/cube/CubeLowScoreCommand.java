package com.team2357.frc2023.commands.scoring.cube;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.everybot.ClawReleaseCubeCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class CubeLowScoreCommand extends ParallelCommandGroup {

    public CubeLowScoreCommand() {
        super(
                new ClawReleaseCubeCommand().withTimeout(0.5),
                new SetRobotStateCommand(RobotState.State.ROBOT_STOWED_EMPTY),
                new SequentialCommandGroup(
                        new WaitCommand(0.1),
                        new ArmRotateToPositionCommand(Constants.ARM_ROTATION.RETRACTED_ROTATIONS)));
    }
}

package com.team2357.frc2023.commands.auto.support;

import com.team2357.frc2023.commands.everybot.ClawReleaseCubeCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class CubeHighScoreClaw extends SequentialCommandGroup  {
    public CubeHighScoreClaw() {
        super(
            new ClawReleaseCubeCommand().withTimeout(0.5),
            new SetRobotStateCommand(RobotState.State.ROBOT_STOWED_EMPTY)
        );
    }
}

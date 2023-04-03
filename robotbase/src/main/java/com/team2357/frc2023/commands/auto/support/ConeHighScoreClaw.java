package com.team2357.frc2023.commands.auto.support;

import com.team2357.frc2023.commands.everybot.ClawReleaseConeCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ConeHighScoreClaw extends SequentialCommandGroup  {
    public ConeHighScoreClaw() {
        super(
            new ClawReleaseConeCommand().withTimeout(0.5),
            new SetRobotStateCommand(RobotState.State.ROBOT_STOWED_EMPTY)
        );
    }
}

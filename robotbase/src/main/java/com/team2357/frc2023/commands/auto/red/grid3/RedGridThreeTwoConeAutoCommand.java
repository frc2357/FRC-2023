package com.team2357.frc2023.commands.auto.red.grid3;

import com.team2357.frc2023.util.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class RedGridThreeTwoConeAutoCommand extends SequentialCommandGroup{
    public RedGridThreeTwoConeAutoCommand() {
        addCommands(new WaitCommand(3)); // Score cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("red node9 to stage4", true));
        addCommands(new WaitCommand(2)); // Pick up cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("red stage4 to node7", true));
        addCommands(new WaitCommand(3)); // Score cone
    }
}

package com.team2357.frc2023.commands.auto.blue.grid3;

import com.team2357.frc2023.util.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class BlueGridThreeTwoConeAutoCommand extends SequentialCommandGroup{
    public BlueGridThreeTwoConeAutoCommand() {
        addCommands(new WaitCommand(3)); // Score cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("blue node9 to stage4", true));
        addCommands(new WaitCommand(2)); // Pick up cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("blue stage4 to node7", true));
        addCommands(new WaitCommand(3)); // Score cone
    }
}

package com.team2357.frc2023.commands.auto.blue.grid2;

import com.team2357.frc2023.util.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class BlueGridTwoTwoConeAutoCommand extends SequentialCommandGroup{
    public BlueGridTwoTwoConeAutoCommand() {
        addCommands(new WaitCommand(3)); // Score cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("blue node4 to stage2", true));
        addCommands(new WaitCommand(2)); // Pick up cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("blue stage2 to node6", true));
        addCommands(new WaitCommand(3)); // Score cone
    }
}

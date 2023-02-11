package com.team2357.frc2023.commands.auto.red.grid2;

import com.team2357.frc2023.util.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class RedGridTwoTwoConeAutoCommand extends SequentialCommandGroup{
    public RedGridTwoTwoConeAutoCommand() {
        addCommands(new WaitCommand(3)); // Score cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("red node4 to stage2", true));
        addCommands(new WaitCommand(2)); // Pick up cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("red stage2 to node6", true));
        addCommands(new WaitCommand(3)); // Score cone
    }
}

package com.team2357.frc2023.commands.auto.red;

import com.team2357.frc2023.util.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class RedGridOneTwoConeAutoCommand extends SequentialCommandGroup{
    public RedGridOneTwoConeAutoCommand() {
        addCommands(new WaitCommand(3)); // Score cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("red node1 to stage1", true));
        addCommands(new WaitCommand(2)); // Pick up cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("red stage1 to node3", true));
        addCommands(new WaitCommand(3)); // Score cone
    }
}

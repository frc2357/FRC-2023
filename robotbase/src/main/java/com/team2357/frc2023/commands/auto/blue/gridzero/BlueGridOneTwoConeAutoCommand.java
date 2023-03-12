package com.team2357.frc2023.commands.auto.blue.gridzero;

import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class BlueGridOneTwoConeAutoCommand extends SequentialCommandGroup{
    public BlueGridOneTwoConeAutoCommand() {
        addCommands(new WaitCommand(3)); // Score cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("blue node1 to stage1", true));
        addCommands(new WaitCommand(2)); // Pick up cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("blue stage1 to node3", true));
        addCommands(new WaitCommand(3)); // Score cone
    }
}

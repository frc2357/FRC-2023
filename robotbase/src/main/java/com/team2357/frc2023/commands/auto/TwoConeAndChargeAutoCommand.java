package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.util.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class TwoConeAndChargeAutoCommand extends SequentialCommandGroup{
    public TwoConeAndChargeAutoCommand() {
        addCommands(new WaitCommand(3)); // Score cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("node1 to stage1", true));
        addCommands(new WaitCommand(2)); // Pick up cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("stage1 to node3", true));
        addCommands(new WaitCommand(3)); // Score cone
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("node3 to charge", true));
        addCommands(new WaitCommand(2)); // Auto balance
    }
}

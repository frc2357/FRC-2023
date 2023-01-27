package com.team2357.frc2023.commands.auto.red.grid3;

import com.team2357.frc2023.util.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class RedGridThreeTwoConeBalanceAutoCommand extends SequentialCommandGroup {
    public RedGridThreeTwoConeBalanceAutoCommand() {
        addCommands(new RedGridThreeTwoConeAutoCommand());
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("red node7 to charge", true));
        addCommands(new WaitCommand(2));
    }
}

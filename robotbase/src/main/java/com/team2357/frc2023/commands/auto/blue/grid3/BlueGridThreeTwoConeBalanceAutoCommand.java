package com.team2357.frc2023.commands.auto.blue.grid3;

import com.team2357.frc2023.util.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class BlueGridThreeTwoConeBalanceAutoCommand extends SequentialCommandGroup {
    public BlueGridThreeTwoConeBalanceAutoCommand() {
        addCommands(new BlueGridThreeTwoConeAutoCommand());
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("blue node7 to charge", true));
        addCommands(new WaitCommand(2));
    }
}

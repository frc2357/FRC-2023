package com.team2357.frc2023.commands.auto.blue.grid3;

import com.team2357.frc2023.commands.drive.AutoBalanceCommand;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class BlueGridThreeTwoConeBalanceAutoCommand extends SequentialCommandGroup {
    public BlueGridThreeTwoConeBalanceAutoCommand() {
        addCommands(new BlueGridThreeTwoConeAutoCommand());
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("blue node7 to charge", true));
        addCommands(new AutoBalanceCommand());
    }
}

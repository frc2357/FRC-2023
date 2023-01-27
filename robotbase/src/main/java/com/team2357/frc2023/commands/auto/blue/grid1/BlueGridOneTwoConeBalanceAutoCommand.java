package com.team2357.frc2023.commands.auto.blue.grid1;

import com.team2357.frc2023.util.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class BlueGridOneTwoConeBalanceAutoCommand extends SequentialCommandGroup {
    public BlueGridOneTwoConeBalanceAutoCommand() {
        addCommands(new BlueGridOneTwoConeAutoCommand());
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("blue node3 to charge", true));
        addCommands(new WaitCommand(2));
    }
}

package com.team2357.frc2023.commands.auto.blue.grid2;

import com.team2357.frc2023.util.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class BlueGridTwoTwoConeBalanceAutoCommand extends SequentialCommandGroup {
    public BlueGridTwoTwoConeBalanceAutoCommand() {
        addCommands(new BlueGridTwoTwoConeAutoCommand());
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("blue node6 to charge", true));
        addCommands(new WaitCommand(2));
    }
}

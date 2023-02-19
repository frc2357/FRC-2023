package com.team2357.frc2023.commands.auto.red.grid2;

import com.team2357.frc2023.commands.drive.AutoBalanceCommand;
import com.team2357.frc2023.util.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class RedGridTwoTwoConeBalanceAutoCommand extends SequentialCommandGroup {
    public RedGridTwoTwoConeBalanceAutoCommand() {
        addCommands(new RedGridTwoTwoConeAutoCommand());
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("red node6 to charge", true));
        addCommands(new AutoBalanceCommand());
    }
}

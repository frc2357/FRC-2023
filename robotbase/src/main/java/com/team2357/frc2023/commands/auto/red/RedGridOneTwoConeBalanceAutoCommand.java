package com.team2357.frc2023.commands.auto.red;

import com.team2357.frc2023.util.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class RedGridOneTwoConeBalanceAutoCommand extends SequentialCommandGroup {
    public RedGridOneTwoConeBalanceAutoCommand() {
        addCommands(new RedGridOneTwoConeAutoCommand());
        addCommands(TrajectoryUtil.createTrajectoryPathCommand("red node3 to charge", true));
        addCommands(new WaitCommand(2));
    }
}

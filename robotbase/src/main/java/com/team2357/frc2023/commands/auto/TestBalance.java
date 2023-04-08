package com.team2357.frc2023.commands.auto;

import com.pathplanner.lib.PathConstraints;
import com.team2357.frc2023.commands.drive.AutoBalanceCommand;
import com.team2357.frc2023.commands.drive.DetectChargeBreakCommand;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class TestBalance extends SequentialCommandGroup {

    public TestBalance() {
        addCommands(
                new ParallelRaceGroup(
                        TrajectoryUtil.createTrajectoryPathCommand(getClass().getSimpleName(),
                                new PathConstraints(3.0, 1.5), true),
                        new DetectChargeBreakCommand()),
                new AutoBalanceCommand());
    }

    @Override
    public String toString() {
        return "Test balance";
    }

}

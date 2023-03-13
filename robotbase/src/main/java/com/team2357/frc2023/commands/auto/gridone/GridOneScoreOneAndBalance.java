package com.team2357.frc2023.commands.auto.gridone;

import com.team2357.frc2023.commands.scoring.cone.ConeAutoScoreHighCommandGroup;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class GridOneScoreOneAndBalance extends ParallelCommandGroup{

    public GridOneScoreOneAndBalance() {
        addCommands(
            new WaitCommand(0)
                    .andThen(new ConeAutoScoreHighCommandGroup()
                            .deadlineWith(new WaitCommand(7))),

            new WaitCommand(7)
                    .andThen(TrajectoryUtil.createTrajectoryPathCommand("", true)));    }
}

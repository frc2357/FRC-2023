package com.team2357.frc2023.commands.auto.gridzero;

import com.team2357.frc2023.commands.intake.IntakeDeployCommandGroup;
import com.team2357.frc2023.commands.intake.IntakeStowCommandGroup;
import com.team2357.frc2023.commands.scoring.cone.ConeAutoScoreHighCommandGroup;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class GridZeroTwoConeAutoCommand extends ParallelCommandGroup {
    public GridZeroTwoConeAutoCommand() {
        addCommands(new WaitCommand(0)
                .andThen(new ConeAutoScoreHighCommandGroup()) // abt 7 seconds
                .andThen(new IntakeDeployCommandGroup()
                        .withTimeout(3))
                .andThen(new IntakeStowCommandGroup())
                .andThen(new WaitCommand(2))
                .andThen(new ConeAutoScoreHighCommandGroup()),

                new WaitCommand(0) // 6
                        .andThen(TrajectoryUtil.createTrajectoryPathCommand("node0 to stage0", true)) // abt 4 seconds
                        .andThen(new WaitCommand(2))
                        .andThen(TrajectoryUtil.createTrajectoryPathCommand("stage0 to node2", isFinished())), // abt 4
                                                                                                               // seconds

                new WaitCommand(8));
    }
}
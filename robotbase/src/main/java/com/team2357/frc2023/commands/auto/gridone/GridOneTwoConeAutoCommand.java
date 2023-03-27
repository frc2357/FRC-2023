package com.team2357.frc2023.commands.auto.gridone;

import com.team2357.frc2023.commands.intake.IntakeArmRotateDumbCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerRunCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeAutoScoreHighCommandGroup;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class GridOneTwoConeAutoCommand extends ParallelCommandGroup {
    public GridOneTwoConeAutoCommand() {
        addCommands(
                new WaitCommand(0)
                        .andThen(new ConeAutoScoreHighCommandGroup(false)
                                .deadlineWith(new WaitCommand(7)))
                        .andThen(new WaitCommand(1))
                        .andThen(new ParallelCommandGroup(
                                new IntakeArmRotateDumbCommand(0.6).withTimeout(0.75),

                                new WaitCommand(1.5).andThen(
                                        new IntakeRollerRunCommand()
                                                .withTimeout(2)))),

                new WaitCommand(7).andThen(
                        TrajectoryUtil.createTrajectoryPathCommand("grid1 2 cone", true)));
    }
}

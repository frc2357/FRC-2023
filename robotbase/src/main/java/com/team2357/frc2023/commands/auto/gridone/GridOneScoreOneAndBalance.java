package com.team2357.frc2023.commands.auto.gridone;

import com.team2357.frc2023.commands.claw.ClawInstantOpenCommand;
import com.team2357.frc2023.commands.intake.IntakeArmRotateDumbCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerRunCommand;
import com.team2357.frc2023.commands.intake.IntakeStowCommandGroup;
import com.team2357.frc2023.commands.scoring.cone.ConeAutoScoreHighCommandGroup;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class GridOneScoreOneAndBalance extends ParallelCommandGroup{

    public GridOneScoreOneAndBalance() {
        addCommands(
            new WaitCommand(0)
                        .andThen(new ConeAutoScoreHighCommandGroup(true)
                           .deadlineWith(new WaitCommand(7)))
                        .andThen(new WaitCommand(0.5))
                        .andThen(new ParallelCommandGroup(
                                new ClawInstantOpenCommand(),
                                new IntakeArmRotateDumbCommand(0.4).withTimeout(2),

                                new WaitCommand(0.75).andThen(
                                        new IntakeRollerRunCommand()
                                                .withTimeout(2.5))))
                        .andThen(new IntakeStowCommandGroup()),

            new WaitCommand(5.5)
                    .andThen(TrajectoryUtil.createTrajectoryPathCommand("grid1 1 cone balance", true)));    }
}

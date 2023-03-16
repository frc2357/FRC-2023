package com.team2357.frc2023.commands.auto.gridzero;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.claw.ClawInstantOpenCommand;
import com.team2357.frc2023.commands.intake.IntakeArmRotateDumbCommand;
import com.team2357.frc2023.commands.intake.IntakeDeployCommandGroup;
import com.team2357.frc2023.commands.intake.IntakeRollerRunCommand;
import com.team2357.frc2023.commands.intake.IntakeStowCommandGroup;
import com.team2357.frc2023.commands.scoring.cone.ConeAutoScoreHighCommandGroup;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class GridZeroTwoConeAutoCommand extends ParallelCommandGroup {
    public GridZeroTwoConeAutoCommand() {
        addCommands(
                new WaitCommand(0)
                        .andThen(new ConeAutoScoreHighCommandGroup(false)
                                .deadlineWith(new WaitCommand(7)))
                        .andThen(new WaitCommand(1))
                        .andThen(new ParallelCommandGroup(
                                new ClawInstantOpenCommand(),
                                new IntakeArmRotateDumbCommand(0.4).withTimeout(1),

                                new WaitCommand(1).andThen(
                                        new IntakeRollerRunCommand()
                                                .withTimeout(2.5))))
                        .andThen(new IntakeStowCommandGroup()),

                new WaitCommand(7).andThen(
                        TrajectoryUtil.createTrajectoryPathCommand("grid0 2 cone", Constants.DRIVE.GRID_ZERO_PATH_CONSTRAINTS, true)));
    }
}

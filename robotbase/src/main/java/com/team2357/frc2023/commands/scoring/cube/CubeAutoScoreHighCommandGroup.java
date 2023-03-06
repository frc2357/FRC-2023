package com.team2357.frc2023.commands.scoring.cube;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.intake.WinchRotateToPositionCommand;
import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class CubeAutoScoreHighCommandGroup extends SequentialCommandGroup {
    public CubeAutoScoreHighCommandGroup() {
        addCommands(new WinchRotateToPositionCommand(Constants.INTAKE_ARM.HIGH_SHOT_SETPOINT_ROTATIONS));
        addCommands(
            new ParallelDeadlineGroup(
                new WaitCommand(Constants.INTAKE_ROLLER.HIGH_SHOT_DELAY_SECONDS),
                new InstantCommand(() -> {
                    IntakeRollerSubsystem.getInstance()
                            .manualRunIntake(Constants.INTAKE_ROLLER.HIGH_SHOT_PERCENT_OUTPUT);
                })
            )
        );
        addCommands(new InstantCommand(() -> {
            IntakeRollerSubsystem.getInstance().stopIntake();
        }));
    }
}

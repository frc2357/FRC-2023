package com.team2357.frc2023.commands.intake;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class IntakeCubeCommandGroup extends SequentialCommandGroup {
    public IntakeCubeCommandGroup() {
        super(
            new SequentialCommandGroup(
                new ParallelDeadlineGroup(
                    new IntakeStallCommand(),
                    new IntakeDeployCubeCommandGroup()
                )
            ).finallyDo((interrupted) -> {
                new ParallelDeadlineGroup(
                    new WaitCommand(2), 
                    new IntakeStowCubeCommandGroup()
                ).schedule();
            })
        );
    }
}

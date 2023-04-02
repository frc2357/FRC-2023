package com.team2357.frc2023.commands.intake;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class IntakeConeCommandGroup extends SequentialCommandGroup {
    public IntakeConeCommandGroup() {
        super(
            new SequentialCommandGroup(
                new ParallelDeadlineGroup(
                    new IntakeStallCommand(),
                    new IntakeDeployConeCommandGroup()
                )
            ).finallyDo((interrupted) -> {
                new ParallelDeadlineGroup(
                    new WaitCommand(2), 
                    new IntakeStowConeCommandGroup()
                ).schedule();
            })
        );
        
    }
}

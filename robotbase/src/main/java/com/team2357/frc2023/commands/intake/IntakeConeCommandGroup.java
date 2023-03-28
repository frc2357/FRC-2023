package com.team2357.frc2023.commands.intake;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class IntakeConeCommandGroup extends SequentialCommandGroup {
    public IntakeConeCommandGroup() {
        super(
            new ParallelDeadlineGroup(
                new IntakeStallCommand(),
                new IntakeDeployConeCommandGroup()
            ),
            new IntakeStowConeCommandGroup()
        );
    }
}

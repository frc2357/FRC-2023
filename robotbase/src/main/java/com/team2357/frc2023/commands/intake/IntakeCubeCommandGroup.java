package com.team2357.frc2023.commands.intake;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class IntakeCubeCommandGroup extends SequentialCommandGroup {
    public IntakeCubeCommandGroup() {
        super(
            new ParallelDeadlineGroup(
                new IntakeStallCommand(),
                new IntakeDeployCubeCommandGroup()
            ),
            new IntakeStowCubeCommandGroup()
        );
    }
}

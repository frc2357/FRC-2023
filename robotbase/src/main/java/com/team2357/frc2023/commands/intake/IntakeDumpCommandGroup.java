package com.team2357.frc2023.commands.intake;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class IntakeDumpCommandGroup extends ParallelCommandGroup {
    public IntakeDumpCommandGroup() {
        addCommands(new IntakeArmDeployCommand());
        addCommands(new IntakeRollerReverseCommand());
    }
}

package com.team2357.frc2023.commands.intake;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class IntakeStowCommandGroup extends SequentialCommandGroup {
    public IntakeStowCommandGroup() {
        addCommands(
                new ParallelCommandGroup(
                        new IntakeRollerStopCommand(),
                        new IntakeArmStowCommand()));
                        
        addCommands(new IntakeHandoffCommandGroup());
    }
}
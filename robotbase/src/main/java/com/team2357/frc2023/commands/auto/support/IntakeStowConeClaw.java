package com.team2357.frc2023.commands.auto.support;

import com.team2357.frc2023.commands.everybot.ClawIntakeConeCommand;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class IntakeStowConeClaw extends SequentialCommandGroup {
    public IntakeStowConeClaw() {
        addCommands(
            new ClawIntakeConeCommand()
        );
    }
}

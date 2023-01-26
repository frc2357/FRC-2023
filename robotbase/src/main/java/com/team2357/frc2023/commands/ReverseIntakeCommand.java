package com.team2357.frc2023.commands;

import com.team2357.frc2023.subsystems.IntakeSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

public class ReverseIntakeCommand extends CommandLoggerBase {
    public ReverseIntakeCommand() {
        addRequirements(IntakeSubsystem.getInstance());
    }

    @Override
    public void execute() {
        IntakeSubsystem.getInstance().runIntake(true);
    }

    @Override
    public void end(boolean interrupted) {
        IntakeSubsystem.getInstance().stopIntake();
    }
}

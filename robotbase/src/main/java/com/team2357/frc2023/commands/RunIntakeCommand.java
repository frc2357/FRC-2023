package com.team2357.frc2023.commands;

import com.team2357.frc2023.subsystems.IntakeSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

public class RunIntakeCommand extends CommandLoggerBase {
    public RunIntakeCommand() {
        addRequirements(IntakeSubsystem.getInstance());
    }

    @Override
    public void execute() {
        IntakeSubsystem.getInstance().runIntake(false);
    }

    @Override
    public void end(boolean interrupted) {
        IntakeSubsystem.getInstance().stopIntake();
    }
}

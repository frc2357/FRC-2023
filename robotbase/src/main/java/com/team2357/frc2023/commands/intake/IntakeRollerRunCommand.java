package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

public class IntakeRollerRunCommand extends CommandLoggerBase {
    public IntakeRollerRunCommand() {
        addRequirements(IntakeRollerSubsystem.getInstance());
    }

    @Override
    public void execute() {
        IntakeRollerSubsystem.getInstance().runIntake(false);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        IntakeRollerSubsystem.getInstance().stopIntake();
    }
}

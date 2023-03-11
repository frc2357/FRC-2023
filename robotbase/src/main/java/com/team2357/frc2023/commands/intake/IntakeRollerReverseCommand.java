package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

public class IntakeRollerReverseCommand extends CommandLoggerBase {
    public IntakeRollerReverseCommand() {
        addRequirements(IntakeRollerSubsystem.getInstance());
    }

    @Override
    public void execute() {
        System.out.println("reversing 1");
        IntakeRollerSubsystem.getInstance().runIntake(true);
    }

    @Override
    public void end(boolean interrupted) {
        System.out.println("end reverse");
        IntakeRollerSubsystem.getInstance().stopIntake();
    }
}

package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeRollerStopCommand extends CommandBase{
    public IntakeRollerStopCommand() {
        addRequirements(IntakeRollerSubsystem.getInstance());
    }
    
    @Override
    public void initialize() {
        IntakeRollerSubsystem.getInstance().stopIntake();
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}

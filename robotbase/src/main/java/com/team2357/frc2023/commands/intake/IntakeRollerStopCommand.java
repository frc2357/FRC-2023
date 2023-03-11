package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeRollerStopCommand extends CommandBase{
    
    @Override
    public void initialize() {
        IntakeRollerSubsystem.getInstance().stopIntake();
        System.out.println("roller stop");
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}

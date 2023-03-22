package com.team2357.frc2023.commands.everybot;

import com.team2357.frc2023.subsystems.EverybotClawSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ClawRollerReverseCommand extends CommandBase {
    
    public ClawRollerReverseCommand() {
        addRequirements(EverybotClawSubsystem.getInstance());
    }

    @Override
    public void execute() {
        EverybotClawSubsystem.getInstance().runRollers(true);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        EverybotClawSubsystem.getInstance().stopRollers();
    }
    
}

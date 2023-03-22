package com.team2357.frc2023.commands.everybot;

import com.team2357.frc2023.subsystems.EverybotClawSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ClawRollerRunCommand extends CommandBase {
    
    public ClawRollerRunCommand() {
        addRequirements(EverybotClawSubsystem.getInstance());
    }

    @Override
    public void execute() {
        EverybotClawSubsystem.getInstance().runRollers(false);
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

package com.team2357.frc2023.commands.everybot;

import com.team2357.frc2023.subsystems.ClawSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ClawRollerRunCommand extends CommandBase {
    
    public ClawRollerRunCommand() {
        addRequirements(ClawSubsystem.getInstance());
    }

    @Override
    public void execute() {
        ClawSubsystem.getInstance().runRollers(false);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        ClawSubsystem.getInstance().stopRollers();
    }
    
}

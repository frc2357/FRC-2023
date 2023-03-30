package com.team2357.frc2023.commands.everybot;

import com.team2357.frc2023.subsystems.ClawSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ClawReleaseCubeCommand extends CommandBase {
    public ClawReleaseCubeCommand() {
        addRequirements(ClawSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        ClawSubsystem.getInstance().releaseCube();
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
 
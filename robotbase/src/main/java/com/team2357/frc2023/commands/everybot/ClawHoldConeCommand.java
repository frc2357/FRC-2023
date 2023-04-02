package com.team2357.frc2023.commands.everybot;

import com.team2357.frc2023.subsystems.ClawSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ClawHoldConeCommand extends CommandBase {
    public ClawHoldConeCommand() {
        addRequirements(ClawSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        ClawSubsystem.getInstance().holdCone();
    }

    @Override
    public boolean isFinished() {
        return true;
    }
    
}
 
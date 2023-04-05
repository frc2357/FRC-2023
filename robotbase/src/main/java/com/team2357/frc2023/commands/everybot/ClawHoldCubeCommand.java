package com.team2357.frc2023.commands.everybot;

import com.team2357.frc2023.subsystems.ClawSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ClawHoldCubeCommand extends CommandBase {
    public ClawHoldCubeCommand() {
        addRequirements(ClawSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        ClawSubsystem.getInstance().holdCube();
    }

    @Override
    public boolean isFinished() {
        return false;
    }    
}
 
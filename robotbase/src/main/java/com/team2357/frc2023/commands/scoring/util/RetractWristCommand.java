package com.team2357.frc2023.commands.scoring.util;

import com.team2357.frc2023.subsystems.WristSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class RetractWristCommand extends CommandBase {
    
    public RetractWristCommand() {
        addRequirements(WristSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        WristSubsystem.getInstance().retract();
    }

    @Override
    public boolean isFinished() {
        return WristSubsystem.getInstance().isRetracted();
    }

}

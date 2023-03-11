package com.team2357.frc2023.commands.wrist;

import com.team2357.frc2023.subsystems.WristSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class WristInstantExtendCommand extends CommandBase {
    
    public WristInstantExtendCommand() {
        addRequirements(WristSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        WristSubsystem.getInstance().extend();
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}

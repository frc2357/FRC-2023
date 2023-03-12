package com.team2357.frc2023.commands.wrist;

import com.team2357.frc2023.subsystems.WristSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class WristInstantRetractCommand extends CommandBase {
    
    public WristInstantRetractCommand() {
        addRequirements(WristSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        WristSubsystem.getInstance().retract();
    }

    @Override
    public boolean isFinished() {
        return true;
    }

}

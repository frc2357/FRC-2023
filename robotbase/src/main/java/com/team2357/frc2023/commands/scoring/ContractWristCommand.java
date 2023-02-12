package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.subsystems.WristSubsystem;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ContractWristCommand extends CommandBase {
    
    public ContractWristCommand() {
        addRequirements(WristSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        WristSubsystem.getInstance().contract();
    }

    @Override
    public boolean isFinished() {
        return true;
    }

}

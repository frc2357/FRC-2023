package com.team2357.frc2023.commands;

import com.team2357.frc2023.subsystems.WristSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class PanicModeWristCommand extends CommandBase {
    public PanicModeWristCommand() {
        addRequirements(WristSubsystem.getInstance());
    }

    @Override
    public void execute() {
        if (WristSubsystem.getInstance().isExtended() || WristSubsystem.getInstance().isExtending()) {
            WristSubsystem.getInstance().contract();
        } else {
            WristSubsystem.getInstance().extend();
        }
    }
}

package com.team2357.frc2023.commands.human.panic;

import com.team2357.frc2023.subsystems.WristSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class WristToggleCommand extends CommandBase {
    public WristToggleCommand() {
        addRequirements(WristSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        if (WristSubsystem.getInstance().isExtended() || WristSubsystem.getInstance().isExtending()) {
            WristSubsystem.getInstance().retract();
        } else {
            WristSubsystem.getInstance().extend();
        }
    }
}

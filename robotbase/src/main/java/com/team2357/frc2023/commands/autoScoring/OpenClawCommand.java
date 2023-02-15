package com.team2357.frc2023.commands.autoScoring;

import com.team2357.frc2023.subsystems.ClawSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class OpenClawCommand extends CommandBase {
    public OpenClawCommand() {
        addRequirements(ClawSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        ClawSubsystem.getInstance().open();
    }

    @Override
    public boolean isFinished() {
        return ClawSubsystem.getInstance().isOpen();
    }
}

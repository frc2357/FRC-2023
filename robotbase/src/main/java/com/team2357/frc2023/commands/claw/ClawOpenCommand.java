package com.team2357.frc2023.commands.claw;

import com.team2357.frc2023.subsystems.ClawSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ClawOpenCommand extends CommandBase {
    public ClawOpenCommand() {
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

package com.team2357.frc2023.commands.everybot;

import com.team2357.frc2023.subsystems.WristSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class WristRotateToPositionCommand extends CommandBase {
    private double m_rotations;

    public WristRotateToPositionCommand(double rotations) {
        m_rotations = rotations;
        addRequirements(WristSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        WristSubsystem.getInstance().setRotations(m_rotations);
    }

    @Override
    public boolean isFinished() {
        return WristSubsystem.getInstance().isAtRotations();
    }

    @Override
    public void end(boolean interrupted) {
        WristSubsystem.getInstance().stopMotor();
    }
}

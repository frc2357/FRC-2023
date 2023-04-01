package com.team2357.frc2023.commands.everybot;

import com.team2357.frc2023.subsystems.WristSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class WristRotateToPositionCommand extends CommandBase {
    private double m_rotations;
    private boolean m_isInstant;

    public WristRotateToPositionCommand(double rotations) {
        this(rotations, false);
    }

    public WristRotateToPositionCommand(double rotations, boolean isInstant) {
        m_rotations = rotations;
        m_isInstant = isInstant;
        addRequirements(WristSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        WristSubsystem.getInstance().setRotations(m_rotations);
    }

    @Override
    public boolean isFinished() {
        return WristSubsystem.getInstance().isAtRotations() || m_isInstant;
    }
}

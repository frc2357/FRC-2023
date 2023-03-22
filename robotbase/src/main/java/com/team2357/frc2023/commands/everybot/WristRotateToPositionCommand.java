package com.team2357.frc2023.commands.everybot;

import com.team2357.frc2023.subsystems.EverybotWristSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class WristRotateToPositionCommand extends CommandBase {
    private double m_rotations;

    public WristRotateToPositionCommand(double rotations) {
        m_rotations = rotations;
        addRequirements(EverybotWristSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        EverybotWristSubsystem.getInstance().setRotations(m_rotations);
    }

    @Override
    public boolean isFinished() {
        return EverybotWristSubsystem.getInstance().isAtRotations();
    }

    @Override
    public void end(boolean interrupted) {
        EverybotWristSubsystem.getInstance().stopMotor();
    }
}

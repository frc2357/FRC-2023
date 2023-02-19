package com.team2357.frc2023.commands.scoring.util;

import com.team2357.frc2023.subsystems.ElevatorSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ExtendElevatorToPositionCommand extends CommandBase {
    public double m_rotations;

    public ExtendElevatorToPositionCommand(double rotations) {
        m_rotations = rotations;
        addRequirements(ElevatorSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        ElevatorSubsystem.getInstance().setElevatorRotations(m_rotations);
    }

    @Override
    public void end(boolean interrupted) {
        ElevatorSubsystem.getInstance().stopExtensionMotors();
    }

    @Override
    public boolean isFinished() {
        return ElevatorSubsystem.getInstance().isElevatorAtRotations();
    }
}

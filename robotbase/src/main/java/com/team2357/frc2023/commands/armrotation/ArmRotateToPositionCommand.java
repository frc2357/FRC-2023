package com.team2357.frc2023.commands.armrotation;

import com.team2357.frc2023.subsystems.ArmRotationSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmRotateToPositionCommand extends CommandBase {
    private double m_rotations;

    public ArmRotateToPositionCommand(double rotations) {
        m_rotations = rotations;
        addRequirements(ArmRotationSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        ArmRotationSubsystem.getInstance().setPosition(m_rotations);
    }
    @Override
    public void end(boolean interrupted){
        ArmRotationSubsystem.getInstance().setTargetPositionToCurrentPosition();
    }
    @Override
    public boolean isFinished() {
        return ArmRotationSubsystem.getInstance().isAtPosition();
    }
    
}

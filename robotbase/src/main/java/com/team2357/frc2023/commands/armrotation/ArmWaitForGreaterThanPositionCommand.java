package com.team2357.frc2023.commands.armrotation;

import com.team2357.frc2023.subsystems.ArmRotationSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmWaitForGreaterThanPositionCommand extends CommandBase {
    private double m_rotations;

    public ArmWaitForGreaterThanPositionCommand(double rotations) {
        m_rotations = rotations;
    }

    @Override
    public boolean isFinished() {
        return ArmRotationSubsystem.getInstance().getMotorRotations() > m_rotations;
    }
    
}

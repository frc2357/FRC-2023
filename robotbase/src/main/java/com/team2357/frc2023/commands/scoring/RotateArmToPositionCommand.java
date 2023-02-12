package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.subsystems.ArmRotationSubsystem;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RotateArmToPositionCommand extends CommandBase {
    private double m_rotations;

    public RotateArmToPositionCommand(double rotations) {
        m_rotations = rotations;
        addRequirements(ArmRotationSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        //TODO: Fix ArmRotationSubsystem
        // ArmRotationSubsystem.getInstance().
    }

    @Override
    public void end(boolean interrupted) {
        ArmRotationSubsystem.getInstance().stopRotationMotors();
    }

    @Override
    public boolean isFinished() {
        return ArmRotationSubsystem.getInstance().isRotatorAtRotations();
    }
}

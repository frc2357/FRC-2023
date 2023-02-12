package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.subsystems.ArmExtendSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ExtendArmToPositionCommand extends CommandBase {
    private double m_rotations;

    public ExtendArmToPositionCommand(double rotations) {
        m_rotations = rotations;
        addRequirements(ArmExtendSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        ArmExtendSubsystem.getInstance().setExtenderRotations(m_rotations);
    }

    @Override
    public void end(boolean interrupted) {
        ArmExtendSubsystem.getInstance().stopExtensionMotors();
    }

    @Override
    public boolean isFinished() {
        return ArmExtendSubsystem.getInstance().isExtenderRotatorAtRotations();
    }

}

package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.subsystems.ArmExtensionSubsystem;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ExtendArmToPositionCommand extends CommandBase {
    private double m_rotations;

    public ExtendArmToPositionCommand(double rotations) {
        m_rotations = rotations;
        addRequirements(ArmExtensionSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        ArmExtensionSubsystem.getInstance().extend(m_rotations);
    }

    @Override
    public void end(boolean interrupted) {
        ArmExtensionSubsystem.getInstance().stopExtensionMotors();
    }

    @Override
    public boolean isFinished() {
        return ArmExtensionSubsystem.getInstance().isExtenderRotatorAtRotations();
    }

}

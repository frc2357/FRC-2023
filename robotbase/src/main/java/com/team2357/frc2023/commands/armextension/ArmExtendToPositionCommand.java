package com.team2357.frc2023.commands.armextension;

import com.team2357.frc2023.subsystems.ArmExtensionSubsystem;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmExtendToPositionCommand extends CommandBase {
    private double m_rotations;

    public ArmExtendToPositionCommand(double rotations) {
        m_rotations = rotations;
        addRequirements(ArmExtensionSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        ArmExtensionSubsystem.getInstance().setExtensionRotations(m_rotations);
    }

    @Override
    public void end(boolean interrupted) {
        ArmExtensionSubsystem.getInstance().stopMotor();
    }

    @Override
    public boolean isFinished() {
        return ArmExtensionSubsystem.getInstance().isMotorAtRotations();
    }

}

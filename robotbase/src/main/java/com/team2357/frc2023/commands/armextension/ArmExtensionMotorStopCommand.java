package com.team2357.frc2023.commands.armextension;

import com.team2357.frc2023.subsystems.ArmExtensionSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmExtensionMotorStopCommand extends CommandBase {
    public ArmExtensionMotorStopCommand() {
        addRequirements(ArmExtensionSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        ArmExtensionSubsystem.getInstance().stopMotor();
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}

package com.team2357.frc2023.commands.armrotation;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmRotationAmpZeroCommand extends CommandBase{
public ArmRotationAmpZeroCommand() {
    addRequirements(ArmRotationSubsystem.getInstance());
}

@Override
public void initialize() {
    ArmRotationSubsystem.getInstance().manualRotate(Constants.ARM_ROTATION.ARM_ROTATION_AMP_ZERO_PERCENT_OUTPUT);
}

@Override
public boolean isFinished() {
    return ArmRotationSubsystem.getInstance().getAmps() >= Constants.ARM_ROTATION.ARM_ROTATION_AMP_ZERO_MAX_AMPS;
}

@Override
public void end(boolean interrupted) {
    ArmRotationSubsystem.getInstance().stopRotationMotors();
    ArmRotationSubsystem.getInstance().resetEncoders();
}
}
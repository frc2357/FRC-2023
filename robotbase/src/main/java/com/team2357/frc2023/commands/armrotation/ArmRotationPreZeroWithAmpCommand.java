package com.team2357.frc2023.commands.armrotation;

import org.littletonrobotics.junction.Logger;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmRotationPreZeroWithAmpCommand extends CommandBase {

    long m_startTime;

    public ArmRotationPreZeroWithAmpCommand() {
        addRequirements(ArmRotationSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        ArmRotationSubsystem.getInstance().setClosedLoopEnabled(false);
        ArmRotationSubsystem.getInstance().manualRotate(Constants.ARM_ROTATION.ARM_ROTATION_AMP_ZERO_PERCENT_OUTPUT);
        m_startTime = System.currentTimeMillis();
    }

    @Override
    public boolean isFinished() {
        return System.currentTimeMillis() - m_startTime >= Constants.ARM_ROTATION.ARM_ROTATION_AMP_ZERO_TIME_MILLIS &&
        ArmRotationSubsystem.getInstance().getAmps() >= Constants.ARM_ROTATION.ARM_ROTATION_AMP_ZERO_MAX_AMPS;
    }

    @Override
    public void end(boolean interrupted) {
        ArmRotationSubsystem.getInstance().stopRotationMotors();
    }
}
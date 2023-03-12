package com.team2357.frc2023.commands.armrotation;

import org.littletonrobotics.junction.Logger;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmRotationAmpZeroCommand extends CommandBase {

    long m_startTime;
    public ArmRotationAmpZeroCommand() {
        addRequirements(ArmRotationSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        ArmRotationSubsystem.getInstance().manualRotate(Constants.ARM_ROTATION.ARM_ROTATION_AMP_ZERO_PERCENT_OUTPUT);
        m_startTime = System.currentTimeMillis();
        System.out.println("Command start");
    }

    @Override
    public boolean isFinished() {
        return Math.abs(
                ArmRotationSubsystem.getInstance().getAmps()) >= Constants.ARM_ROTATION.ARM_ROTATION_AMP_ZERO_MAX_AMPS &&
                Constants.ARM_ROTATION.ARM_ROTATION_AMP_ZERO_TIME_MILLIS + m_startTime <= System.currentTimeMillis();
    }

    @Override
    public void end(boolean interrupted) {
        ArmRotationSubsystem.getInstance().stopRotationMotors();
        if (interrupted) {
            DriverStation.reportError("Amp Zeroing did not finish in time! Arm Rotation not zeroed.", false);
            Logger.getInstance().recordOutput("Arm Rotation Amp Zero fail", true);
        } else {
            ArmRotationSubsystem.getInstance().resetEncoder();
            System.out.println("Correct end");
        }

        System.out.println("Command end");
    }
}
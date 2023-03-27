package com.team2357.frc2023.commands.armrotation;

import org.littletonrobotics.junction.Logger;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmEncoderZeroCommand extends CommandBase {
    private ArmRotationSubsystem m_subsystem;

    public ArmEncoderZeroCommand() {
        m_subsystem = ArmRotationSubsystem.getInstance();
        addRequirements(m_subsystem);
    }

    @Override
    public void initialize() {
        m_subsystem.setClosedLoopEnabled(false);
        m_subsystem.manualRotate(Constants.ARM_ROTATION.ENCODER_ZERO_SPEED);
    }

    @Override
    public boolean isFinished() {
        return m_subsystem.getAbsoluteEncoderPosition() >= Constants.ARM_ROTATION.ENCODER_ZERO_POSITION;
    }

    @Override
    public void end(boolean interrupted) {

        m_subsystem.stopRotationMotors();
        m_subsystem.setClosedLoopEnabled(true);
    
        if (!interrupted) {
            Logger.getInstance().recordOutput("Arm Zero", "success");
            m_subsystem.resetEncoder();
            m_subsystem.setRotations(0);
        } else {
            Logger.getInstance().recordOutput("Arm Zero", "Interrupted");
        }
       }
}

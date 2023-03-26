package com.team2357.frc2023.commands.armrotation;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmEncoderZeroCommand extends CommandBase {
    private ArmRotationSubsystem m_subsystem;
    
    private double m_previousPosition;

    public ArmEncoderZeroCommand() {
        m_subsystem = ArmRotationSubsystem.getInstance();
        addRequirements(m_subsystem);
    }

    @Override
    public void initialize() {
        m_subsystem.setClosedLoopEnabled(false);
        m_previousPosition = ArmRotationSubsystem.getInstance().getAbsoluteEncoderPosition();
    }

    @Override
    public void execute() {
        m_subsystem.setRotationAxisSpeed(Constants.ARM_ROTATION.ENCODER_ZERO_AXIS_SPEED);
        m_previousPosition = m_subsystem.getAbsoluteEncoderPosition();
    }

    @Override
    public boolean isFinished() {
        return Math.abs(m_subsystem.getAbsoluteEncoderPosition() - m_previousPosition) >= Constants.ARM_ROTATION.ENCODER_ZERO_END_TOLERANCE;
    }

    @Override
    public void end(boolean interrupted) {
        m_subsystem.endAxisCommand();
    }
}

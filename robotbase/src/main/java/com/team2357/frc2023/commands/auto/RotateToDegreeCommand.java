package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RotateToDegreeCommand extends CommandBase {

    public SwerveDriveSubsystem m_swerve = SwerveDriveSubsystem.getInstance();
    public double m_targetDegrees;
    public PIDController m_pidController;

    public RotateToDegreeCommand(double targetDegrees) {
        m_targetDegrees = targetDegrees;
        
        addRequirements(m_swerve);
    }

    @Override
    public void initialize() {
        m_swerve.initRotating(m_targetDegrees);    
    }

    @Override
    public void execute() {
        m_swerve.rotateExecute();    
    }

    @Override
    public boolean isFinished() {
        return m_swerve.atRotation();
    }

    @Override
    public void end(boolean isInterrupted) {
        m_swerve.stopRotating();
    }
}

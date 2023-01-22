package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.util.Utility;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RotateToDegree extends CommandBase {

    public SwerveDriveSubsystem m_swerve = SwerveDriveSubsystem.getInstance();
    public double m_targetDegrees;

    public RotateToDegree(double targetDegrees) {
        m_targetDegrees = targetDegrees;
        addRequirements(m_swerve);
    }

    @Override
    public void initialize() {
        m_swerve.drive(0, 0, m_targetDegrees);
    }
    @Override
    public boolean isFinished() {
        return Utility.isWithinTolerance(m_swerve.getGyroscopeRotation().getDegrees(), m_targetDegrees,8.0);
    }
}

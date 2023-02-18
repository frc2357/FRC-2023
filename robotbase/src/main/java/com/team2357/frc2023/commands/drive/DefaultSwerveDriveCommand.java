package com.team2357.frc2023.commands.drive;

import com.team2357.frc2023.controls.SwerveDriveControls;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class DefaultSwerveDriveCommand extends CommandBase {
    private final SwerveDriveSubsystem m_drivetrainSubsystem;
    private final SwerveDriveControls m_controls;

    public DefaultSwerveDriveCommand(SwerveDriveSubsystem drivetrainSubsystem,
            SwerveDriveControls controls) {
        m_drivetrainSubsystem = drivetrainSubsystem;
        m_controls = controls;

        addRequirements(drivetrainSubsystem);
    }

    @Override
    public void execute() {
        if (m_controls.m_isDifferentialDrive) {
            m_drivetrainSubsystem.differentialDrive(m_controls.getY(), m_controls.getRotation());
        } else {
            m_drivetrainSubsystem.drive(
                    m_controls.getY(),
                    m_controls.getX(),
                    m_controls.getRotation());
        }
    }

    @Override
    public void end(boolean interrupted) {
        m_drivetrainSubsystem.drive(new ChassisSpeeds(0.0, 0.0, 0.0));
    }
}

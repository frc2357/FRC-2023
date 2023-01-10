package com.team2357.frc2023.commands;

import com.team2357.frc2023.controls.SwerveDriveControls;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class DefaultDriveCommand extends CommandBase {
    private final SwerveDriveSubsystem m_drivetrainSubsystem;
    private final SwerveDriveControls m_controls;

    public DefaultDriveCommand(SwerveDriveSubsystem drivetrainSubsystem,
                               SwerveDriveControls controls) {
        m_drivetrainSubsystem = drivetrainSubsystem;
        m_controls = controls;

        addRequirements(drivetrainSubsystem);
    }

    @Override
    public void execute() {
        m_drivetrainSubsystem.drive(
                m_controls.getY(),
                m_controls.getX(),
                m_controls.getRotation()
        );
    }

    @Override
    public void end(boolean interrupted) {
        m_drivetrainSubsystem.drive(new ChassisSpeeds(0.0, 0.0, 0.0));
    }
}

package com.team2357.frc2023.commands;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.controls.SwerveDriveControls;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class ChangeToDifferentialDriveCommand extends CommandBase {
    SwerveDriveControls m_controls;
    
    public ChangeToDifferentialDriveCommand(SwerveDriveControls controls) {
        m_controls = controls;
    }

    @Override
    public void initialize() {
        SwerveDriveSubsystem.getInstance().setDefaultCommand(new DefaultDifferentialDriveCommand(SwerveDriveSubsystem.getInstance(), m_controls));
        SwerveDriveSubsystem.getInstance().zeroDifferentialDrive();
        new WaitCommand(Constants.DRIVE.DIFFERENTIAL_ZERO_WAIT_SECONDS);
    }

    @Override
    public void end(boolean interrupted) {
        SwerveDriveSubsystem.getInstance().setDefaultCommand(new DefaultSwerveDriveCommand(SwerveDriveSubsystem.getInstance(), m_controls));
    }
}

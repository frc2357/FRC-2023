package com.team2357.frc2023.commands;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class AutoBalanceCommand extends CommandLoggerBase {

    private double m_error, m_angle, m_power, m_yaw, m_direction;
    
    public AutoBalanceCommand() {
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        SwerveDriveSubsystem.getInstance().zero();
    }

    @Override
    public void execute() {

        SwerveDriveSubsystem.getInstance().balance();

    }

    @Override
    public void end(boolean interrupted) {
        SwerveDriveSubsystem.getInstance().drive(new ChassisSpeeds(0, 0, 0));
    }

}

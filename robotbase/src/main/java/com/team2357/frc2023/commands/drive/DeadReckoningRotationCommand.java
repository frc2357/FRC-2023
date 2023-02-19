package com.team2357.frc2023.commands.drive;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class DeadReckoningRotationCommand extends CommandBase {
    private double m_durationMillis;
    private double m_startTime;

    public DeadReckoningRotationCommand(double durationMillis) {
        m_durationMillis = durationMillis;
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        m_startTime = System.currentTimeMillis();
        SwerveDriveSubsystem.getInstance().drive(0, 0, Constants.DRIVE.DEAD_RECKONING_ROTATION_SPEED_PROPORTION);
    }

    @Override
    public boolean isFinished() {
        return System.currentTimeMillis() - m_startTime >= m_durationMillis;
    }

    @Override
    public void end(boolean interrupted) {
        SwerveDriveSubsystem.getInstance().drive(0, 0, 0);
    }


}

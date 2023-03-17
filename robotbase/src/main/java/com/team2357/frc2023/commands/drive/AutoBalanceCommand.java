package com.team2357.frc2023.commands.drive;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class AutoBalanceCommand extends CommandLoggerBase {

    private double prevAngle = Double.NaN;
    
    public AutoBalanceCommand() {
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        SwerveDriveSubsystem.getInstance().zero();
    }

    @Override
    public void execute() {
        prevAngle = SwerveDriveSubsystem.getInstance().balance(prevAngle);
    }

    @Override
    public boolean isFinished() {
        return SwerveDriveSubsystem.getInstance().isBalanced();
    }
    
    @Override
    public void end(boolean interrupted) {
        SwerveDriveSubsystem.getInstance().drive(new ChassisSpeeds(0, 0, 0));
    }

}

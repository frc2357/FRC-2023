package com.team2357.frc2023.commands;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class AutoBalanceCommand extends CommandLoggerBase {
    public AutoBalanceCommand() {
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        SwerveDriveSubsystem.getInstance().zero();
    }

    @Override
    public void execute() {
        System.out.println("AutoBalanceCommand.execute");
        SwerveDriveSubsystem.getInstance().balance();
    }

    @Override
    public void end(boolean interrupted) {
        System.out.println("AutoBalanceCommand.end");
        SwerveDriveSubsystem.getInstance().drive(new ChassisSpeeds(0, 0, 0));
    }

    @Override
    public boolean isFinished() {
        return SwerveDriveSubsystem.getInstance().isBalanced();
    }
}

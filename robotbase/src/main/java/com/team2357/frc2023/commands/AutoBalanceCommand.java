package com.team2357.frc2023.commands;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutoBalanceCommand extends CommandBase {
    public AutoBalanceCommand() {
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    @Override
    public void execute() {
        //TODO: Auto balance
    }

    @Override
    public void end(boolean interrupted) {
        SwerveDriveSubsystem.getInstance().drive(new ChassisSpeeds(0, 0, 0));
    }
}

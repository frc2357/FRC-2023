package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class SetPoseFromApriltagCommand extends CommandBase{
    public SetPoseFromApriltagCommand() {
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        SwerveDriveSubsystem.getInstance().setOdomertyFromApriltag();
    }

    @Override
    public boolean isFinished(){
        return true;
    }
}

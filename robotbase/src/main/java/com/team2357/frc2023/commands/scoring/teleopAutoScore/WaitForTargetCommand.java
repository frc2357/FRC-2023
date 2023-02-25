package com.team2357.frc2023.commands.scoring.teleopAutoScore;

import com.team2357.lib.subsystems.LimelightSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class WaitForTargetCommand extends CommandBase {
    public WaitForTargetCommand() {
        LimelightSubsystem.getInstance().setAprilTagPipelineActive();
    }

    @Override
    public boolean isFinished() {
        return LimelightSubsystem.getInstance().validTargetExists();
    }
}

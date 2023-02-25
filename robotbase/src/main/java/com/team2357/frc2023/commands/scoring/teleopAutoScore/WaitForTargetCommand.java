package com.team2357.frc2023.commands.scoring.teleopAutoScore;

import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;
import com.team2357.lib.subsystems.LimelightSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class WaitForTargetCommand extends CommandBase {
    public WaitForTargetCommand() {
        DualLimelightManagerSubsystem.getInstance().setAprilTagPipelineActive();
    }

    @Override
    public boolean isFinished() {
        return LimelightSubsystem.getInstance().validTargetExists();
    }
}

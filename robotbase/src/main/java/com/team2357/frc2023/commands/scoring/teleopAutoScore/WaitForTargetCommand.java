package com.team2357.frc2023.commands.scoring.teleopAutoScore;

import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class WaitForTargetCommand extends CommandBase {
    public WaitForTargetCommand() {
        DualLimelightManagerSubsystem.getInstance().setAprilTagPipelineActive();
    }

    @Override
    public boolean isFinished() {
        return DualLimelightManagerSubsystem.getInstance().validTargetExists();
    }
}

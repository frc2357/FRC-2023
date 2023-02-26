package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.subsystems.LimelightSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class TranslateToTargetCommandGroup extends SequentialCommandGroup {
    public TranslateToTargetCommandGroup(SwerveDriveSubsystem.COLUMN_SETPOINT column) {

        // Enable the april tag close range pipeline
        this.addCommands(new InstantCommand(() -> DualLimelightManagerSubsystem.getInstance().setAprilTagPipelineActive()));

        // Rotate to a heading of 0
        this.addCommands(new RotateToDegreeCommand(0));

        // Wait to prevent inaccuracy
        this.addCommands(new WaitCommand(0.1));

        // Translate to the target in the X and Y axis
        this.addCommands(new TranslateToTargetCommand(column));
    }
}

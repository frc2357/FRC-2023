package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class TranslateToTargetCommandGroup extends SequentialCommandGroup {
    public TranslateToTargetCommandGroup(SwerveDriveSubsystem.COLUMN_TARGET targetColumn) {
        this(targetColumn, -1);
    }

    public TranslateToTargetCommandGroup(SwerveDriveSubsystem.COLUMN_TARGET targetColumn, int aprilTagId) {
        // Rotate to a heading of 0
        this.addCommands(new RotateToDegreeCommand(0));

        // Wait to prevent inaccuracy
        this.addCommands(new WaitCommand(0.1));

        // Translate to the target in the X and Y axis
        this.addCommands(new TranslateToTargetCommand(targetColumn, aprilTagId));
    }
}

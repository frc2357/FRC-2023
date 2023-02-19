package com.team2357.frc2023.commands.auto;

import com.team2357.lib.subsystems.LimelightSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class TranslateToTargetCommandGroup extends SequentialCommandGroup {
    public TranslateToTargetCommandGroup(double xSetpoint) {

        // Enable the april tag close range pipeline
        this.addCommands(new InstantCommand(() -> LimelightSubsystem.getInstance().setAprilTagPipelineActive()));

        // Rotate to a heading of 0
        this.addCommands(new RotateToDegreeCommand(0));

        // Wait to prevent inaccuracy
        this.addCommands(new WaitCommand(0.1));

        // Translate to the target in the X and Y axis
        this.addCommands(new TranslateToTargetCommand(xSetpoint));
    }
}

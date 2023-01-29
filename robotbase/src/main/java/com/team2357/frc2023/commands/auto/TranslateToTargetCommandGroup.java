package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.subsystems.LimelightSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class TranslateToTargetCommandGroup extends SequentialCommandGroup {
    public TranslateToTargetCommandGroup() {

        // Enable the april tag close range pipeline
        this.addCommands(new InstantCommand(() -> LimelightSubsystem.getInstance().setAprilTagPipelineActive()));

        // Rotate to a heading of 0
        this.addCommands(new RotateToDegreeCommand(0));

        // Enable open loop ramping for acceleration
        this.addCommands(new InstantCommand(() -> SwerveDriveSubsystem.getInstance().enableOpenLoopRamp()));

        // Wait to prevent inaccuracy
        this.addCommands(new WaitCommand(0.1));

        // Translate along the y axis
        this.addCommands(new TranslateToTargetYCommand());

        // Wait to prevent inaccuracy
        this.addCommands(new WaitCommand(0.1));

        // Translate along the x axis
        this.addCommands(new TranslateToTargetXCommand());

        // Disable open loop ramping for normal control
        this.addCommands(new InstantCommand(() -> SwerveDriveSubsystem.getInstance().disableOpenLoopRamp()));
    }
}

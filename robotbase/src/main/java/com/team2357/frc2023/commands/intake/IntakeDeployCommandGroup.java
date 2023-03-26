package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class IntakeDeployCommandGroup extends ParallelCommandGroup {
    public IntakeDeployCommandGroup() {
        addCommands(new InstantCommand(() -> DualLimelightManagerSubsystem.getInstance().setHumanPipelineActive()));
        addCommands(new IntakeArmDeployCommand());
        addCommands(new IntakeRollerRunCommand());
    }
}

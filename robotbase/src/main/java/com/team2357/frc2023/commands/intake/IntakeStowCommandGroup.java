package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.commands.claw.CloseClawCommand;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class IntakeStowCommandGroup extends ParallelCommandGroup {
    public IntakeStowCommandGroup() {
        addCommands(new InstantCommand(() -> DualLimelightManagerSubsystem.getInstance().setAprilTagPipelineActive()));

        addCommands(new SequentialCommandGroup(
                new ParallelCommandGroup(
                        new IntakeRollerStopCommand(),
                        new IntakeArmStowCommand()),

                new CloseClawCommand()));
    }
}

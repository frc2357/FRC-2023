package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.commands.claw.CloseClawCommand;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class AutoIntakeCommandGroup extends ParallelCommandGroup {
    public AutoIntakeCommandGroup() {
        addCommands(new InstantCommand(() -> DualLimelightManagerSubsystem.getInstance().setAprilTagPipelineActive()));

        addCommands(new SequentialCommandGroup(
                new ParallelCommandGroup(
                        new IntakeRollerStopCommand(),
                        new IntakeArmStowCommand()),

                new CloseClawCommand()));
    }
}

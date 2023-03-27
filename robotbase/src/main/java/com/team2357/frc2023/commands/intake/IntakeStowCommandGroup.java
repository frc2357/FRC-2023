package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class IntakeStowCommandGroup extends ParallelCommandGroup {
    public IntakeStowCommandGroup() {
        addCommands(
            new SetRobotStateCommand(RobotState.State.ROBOT_STOWED_EMPTY),
            new InstantCommand(() -> DualLimelightManagerSubsystem.getInstance().setAprilTagPipelineActive()),
            new SequentialCommandGroup(
                new ParallelCommandGroup(
                        new IntakeRollerStopCommand(),
                        new IntakeArmStowCommand()))
        );
    }
}

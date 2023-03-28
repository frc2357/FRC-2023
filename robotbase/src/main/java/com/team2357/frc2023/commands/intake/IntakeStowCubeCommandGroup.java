package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.commands.everybot.ClawIntakeCubeCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.commands.util.ParallelInterruptCommandGroup;
import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class IntakeStowCubeCommandGroup extends SequentialCommandGroup {
    public IntakeStowCubeCommandGroup() {
        addCommands(
            new ParallelInterruptCommandGroup(
                new SetRobotStateCommand(RobotState.State.ROBOT_STOWED_EMPTY),
                new InstantCommand(() -> DualLimelightManagerSubsystem.getInstance().setAprilTagPipelineActive()),
                new IntakeRollerStopCommand(),
                new IntakeArmStowCommand(),
                new ClawIntakeCubeCommand()
            ),
            new SetRobotStateCommand(RobotState.State.ROBOT_STOWED_CONE)
        );
    }
}

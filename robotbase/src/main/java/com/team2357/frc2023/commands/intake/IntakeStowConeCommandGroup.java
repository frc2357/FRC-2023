package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.commands.everybot.ClawIntakeConeCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.commands.util.ParallelInterruptCommandGroup;
import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class IntakeStowConeCommandGroup extends SequentialCommandGroup {
    public IntakeStowConeCommandGroup() {
        addCommands(
            new ParallelInterruptCommandGroup(
                new SetRobotStateCommand(RobotState.State.ROBOT_STOWED_EMPTY),
                new InstantCommand(() -> DualLimelightManagerSubsystem.getInstance().setAprilTagPipelineActive()),
                new IntakeRollerStopCommand(),
                new IntakeArmStowCommand(),
                new ClawIntakeConeCommand()
            ),
            new SetRobotStateCommand(RobotState.State.ROBOT_STOWED_CONE)
        );
    }
}

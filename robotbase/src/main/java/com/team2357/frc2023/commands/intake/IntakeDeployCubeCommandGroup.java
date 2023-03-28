package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class IntakeDeployCubeCommandGroup extends ParallelCommandGroup {
    public IntakeDeployCubeCommandGroup() {
        addCommands(
            new SetRobotStateCommand(RobotState.State.ROBOT_INTAKING_CUBE),
            new InstantCommand(() -> DualLimelightManagerSubsystem.getInstance().setHumanPipelineActive()),
            new IntakeArmDeployCommand(),
            new IntakeRollerRunCommand()
        );
    }
}

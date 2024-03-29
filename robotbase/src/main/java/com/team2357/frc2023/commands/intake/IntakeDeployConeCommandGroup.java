package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.commands.armextension.ArmExtendAmpZeroCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class IntakeDeployConeCommandGroup extends ParallelCommandGroup {
    public IntakeDeployConeCommandGroup() {
        addCommands(
            new SetRobotStateCommand(RobotState.State.ROBOT_INTAKING_CONE),
            new SequentialCommandGroup(
                new IntakeHalfDeployCommand(),
                new IntakeArmDeployCommand()
            ),
            new ArmExtendAmpZeroCommand(),
            new IntakeRollerRunCommand()
        );
    }
}

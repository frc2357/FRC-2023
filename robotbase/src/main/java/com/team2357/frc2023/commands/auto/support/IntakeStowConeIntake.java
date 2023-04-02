package com.team2357.frc2023.commands.auto.support;

import com.team2357.frc2023.commands.intake.IntakeArmStowCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerStopCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class IntakeStowConeIntake extends SequentialCommandGroup {
    public IntakeStowConeIntake() {
        addCommands(
            new ParallelCommandGroup(
                new IntakeRollerStopCommand(),
                new IntakeArmStowCommand()
            ).handleInterrupt(() -> new SetRobotStateCommand(RobotState.State.ROBOT_STOWED_EMPTY).schedule()),
            new SetRobotStateCommand(RobotState.State.ROBOT_STOWED_CONE)
        );
    }
}

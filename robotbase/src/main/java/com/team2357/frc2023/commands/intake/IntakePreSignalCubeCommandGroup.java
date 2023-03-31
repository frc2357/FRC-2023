package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class IntakePreSignalCubeCommandGroup extends ParallelCommandGroup {
    public IntakePreSignalCubeCommandGroup() {
        addCommands(
            new SetRobotStateCommand(RobotState.State.ROBOT_PRE_INTAKING_CUBE)
        );
    }
}

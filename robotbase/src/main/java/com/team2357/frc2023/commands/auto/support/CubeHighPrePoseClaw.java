package com.team2357.frc2023.commands.auto.support;

import com.team2357.frc2023.commands.everybot.ClawHoldCubeCommand;
import com.team2357.frc2023.commands.everybot.ClawIntakeCubeCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class CubeHighPrePoseClaw extends ParallelCommandGroup {
    public CubeHighPrePoseClaw() {
        super(
            new SetRobotStateCommand(RobotState.State.ROBOT_PRE_SCORE_CUBE_HIGH),

            // Claw Rollers
            new SequentialCommandGroup(
                new ClawIntakeCubeCommand(),
                new ClawHoldCubeCommand()
            )
        );
    }
}

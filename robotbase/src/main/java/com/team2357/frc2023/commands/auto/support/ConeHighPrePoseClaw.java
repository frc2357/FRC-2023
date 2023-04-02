package com.team2357.frc2023.commands.auto.support;

import com.team2357.frc2023.commands.everybot.ClawHoldConeCommand;
import com.team2357.frc2023.commands.everybot.ClawIntakeConeCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ConeHighPrePoseClaw extends ParallelCommandGroup {
    public ConeHighPrePoseClaw() {
        super(
            new SetRobotStateCommand(RobotState.State.ROBOT_PRE_SCORE_CONE_HIGH),

            // Claw Rollers
            new SequentialCommandGroup(
                new ClawIntakeConeCommand(),
                new ClawHoldConeCommand()
            )
        );
    }
}

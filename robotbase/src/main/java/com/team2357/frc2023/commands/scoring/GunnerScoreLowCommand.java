package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.commands.scoring.cone.ConeLowScoreCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeLowPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeLowScoreCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeLowPrePoseCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class GunnerScoreLowCommand extends SequentialCommandGroup {
    public GunnerScoreLowCommand() {
        super(
            new ConditionalCommand(
                new ConditionalCommand(
                    new CubeLowScoreCommand(),
                    new CubeLowPrePoseCommand(),
                    () -> RobotState.getState() == RobotState.State.ROBOT_PRE_SCORE_CUBE_LOW
                ),
                new ConditionalCommand(
                    new ConeLowScoreCommand(),
                    new ConeLowPrePoseCommand(),
                    () -> RobotState.getState() == RobotState.State.ROBOT_PRE_SCORE_CONE_LOW
                ),
                () -> RobotState.hasCube())
        );
    }
}

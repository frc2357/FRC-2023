package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.commands.scoring.cone.ConeMidScoreCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeMidPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeMidScoreCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeMidPrePoseCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class GunnerScoreMidCommand extends SequentialCommandGroup {
    public GunnerScoreMidCommand() {
        super(
            new ConditionalCommand(
                new ConditionalCommand(
                    new CubeMidScoreCommand(),
                    new CubeMidPrePoseCommand(),
                    () -> RobotState.getState() == RobotState.State.ROBOT_PRE_SCORE_CUBE_MID
                ),
                new ConditionalCommand(
                    new ConeMidScoreCommand(),
                    new ConeMidPrePoseCommand(),
                    () -> RobotState.getState() == RobotState.State.ROBOT_PRE_SCORE_CONE_MID
                ),
                () -> RobotState.hasCube())
        );
    }
}

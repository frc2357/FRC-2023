package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.commands.scoring.cone.ConeHighScoreCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeHighPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeHighScoreCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeHighPrePoseCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class GunnerScoreHighCommand extends SequentialCommandGroup {
    public GunnerScoreHighCommand() {
        super(
            new ConditionalCommand(
                new ConditionalCommand(
                    new CubeHighScoreCommand(),
                    new CubeHighPrePoseCommand(),
                    () -> RobotState.getState() == RobotState.State.ROBOT_PRE_SCORE_CUBE_HIGH
                ),
                new ConditionalCommand(
                    new ConeHighScoreCommand(),
                    new ConeHighPrePoseCommand(),
                    () -> RobotState.getState() == RobotState.State.ROBOT_PRE_SCORE_CONE_HIGH
                ),
                () -> RobotState.hasCube())
        );
    }
}

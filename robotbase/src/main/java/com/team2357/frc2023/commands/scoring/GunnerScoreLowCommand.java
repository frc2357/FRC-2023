package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.commands.scoring.cone.ConeLowScoreCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeLowClawPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeLowClawScoreCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeLowPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeLowScoreCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeLowClawPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeLowClawScoreCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeLowPrePoseCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class GunnerScoreLowCommand extends SequentialCommandGroup {
    public GunnerScoreLowCommand() {
        super(
            new ConditionalCommand(
                new ConditionalCommand(
                    new CubeLowClawScoreCommand(),
                    new CubeLowClawPrePoseCommand(),
                    () -> RobotState.getState() == RobotState.State.ROBOT_PRE_SCORE_CUBE_LOW
                ),
                new ConditionalCommand(
                    new ConeLowClawScoreCommand(),
                    new ConeLowClawPrePoseCommand(),
                    () -> RobotState.getState() == RobotState.State.ROBOT_PRE_SCORE_CONE_LOW
                ),
                () -> RobotState.hasCube())
        );
    }
}

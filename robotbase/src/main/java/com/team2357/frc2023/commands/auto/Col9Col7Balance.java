package com.team2357.frc2023.commands.auto;

import com.pathplanner.lib.PathConstraints;
import com.team2357.frc2023.commands.drive.Test1AutoBalanceCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeHighPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeHighScoreCommand;
import com.team2357.frc2023.commands.util.AutonomousZeroCommand;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class Col9Col7Balance extends ParallelCommandGroup {
    public Col9Col7Balance() {
        addCommands(
            new SequentialCommandGroup(
                // Initialize
                new AutonomousZeroCommand(),
                // Score cone high
                new ConeHighPrePoseCommand(true),
                new ConeHighScoreCommand()

                // TODO: Add intake

                // TODO: Add scoring
            ),
            // Path movement
            new SequentialCommandGroup(
                new WaitCommand(3),
                TrajectoryUtil.createTrajectoryPathCommand(getClass().getSimpleName(), new PathConstraints(1.25, 1), true),
                new Test1AutoBalanceCommand()
                )
        );
    }

    @Override
    public String toString() {
        return "Col 9, Col 7, Balance";
    }
}

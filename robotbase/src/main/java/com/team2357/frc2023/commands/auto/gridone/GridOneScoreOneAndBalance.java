package com.team2357.frc2023.commands.auto.gridone;

import com.pathplanner.lib.PathConstraints;
import com.team2357.frc2023.commands.drive.Test1AutoBalanceCommand;
import com.team2357.frc2023.commands.intake.IntakeArmRotateDumbCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerRunCommand;
import com.team2357.frc2023.commands.intake.IntakeStowConeCommandGroup;
import com.team2357.frc2023.commands.scoring.cone.ConeHighPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeHighScoreCommand;
import com.team2357.frc2023.commands.util.AutonomousZeroCommand;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class GridOneScoreOneAndBalance extends ParallelCommandGroup {
    public GridOneScoreOneAndBalance() {
        addCommands(
            new SequentialCommandGroup(
                // Initialize
                new AutonomousZeroCommand(),
                // Score cone high
                new ConeHighPrePoseCommand(true),
                new ConeHighScoreCommand(),

                // Wait until we are over charge station
                new WaitCommand(1),

                // Then deploy intake after we're back on the floor
                new ParallelCommandGroup(
                  new IntakeArmRotateDumbCommand(0.5).withTimeout(1.5),
                    new IntakeRollerRunCommand().withTimeout(4)
                ),

                // Then stow intake
                new IntakeStowConeCommandGroup()
            ),
            // Path movement
            new SequentialCommandGroup(
                new WaitCommand(3),
                TrajectoryUtil.createTrajectoryPathCommand("grid1 1 cone balance", new PathConstraints(1, 1), true),
                new Test1AutoBalanceCommand()
                )
        );
    }
}

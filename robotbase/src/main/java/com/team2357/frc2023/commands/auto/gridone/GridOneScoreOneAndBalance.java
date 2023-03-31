package com.team2357.frc2023.commands.auto.gridone;

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
            // new SequentialCommandGroup(
            //     // Initialize
            //     new AutonomousZeroCommand(),
            //     // Score cone high
            //     new ConeHighPrePoseCommand(),
            //     new ConeHighScoreCommand(),

            //     // Wait until we going over charge station
            //     new WaitCommand(1.5),

            //     // Then deploy intake after we're back on the floor
            //     new ParallelCommandGroup(
            //       // new ClawInstantOpenCommand(),
            //       new IntakeArmRotateDumbCommand(0.6).withTimeout(2.0),
            //       new SequentialCommandGroup(
            //         new IntakeRollerRunCommand().withTimeout(2)
            //       )
            //     ),

            //     // Then stow intake
            //     new IntakeStowConeCommandGroup()
            // ),
            // Path movement
            new SequentialCommandGroup(
                new WaitCommand(0),
                TrajectoryUtil.createTrajectoryPathCommand("grid1 1 cone balance", true)
        )
        );
    }
}

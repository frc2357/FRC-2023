package com.team2357.frc2023.commands.auto;

import com.pathplanner.lib.PathConstraints;
import com.team2357.frc2023.commands.auto.support.ConeHighPrePoseArm;
import com.team2357.frc2023.commands.auto.support.ConeHighPrePoseClaw;
import com.team2357.frc2023.commands.auto.support.ConeHighPrePoseIntake;
import com.team2357.frc2023.commands.auto.support.ConeHighScoreArmReturn;
import com.team2357.frc2023.commands.auto.support.ConeHighScoreClaw; import com.team2357.frc2023.commands.intake.IntakeArmRotateDumbCommand; import com.team2357.frc2023.commands.intake.IntakeRollerRunCommand;
import com.team2357.frc2023.commands.intake.IntakeStowConeCommandGroup;
import com.team2357.frc2023.commands.scoring.cone.ConeHighPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeHighScoreCommand;
import com.team2357.frc2023.commands.util.AutonomousZeroCommand;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class Col9Col7 extends ParallelCommandGroup {
    public Col9Col7() {
        addCommands(
            new SequentialCommandGroup(
                // Step 1: Initialize
                new AutonomousZeroCommand(),

                // Step 2: Score initial cone and deploy intake
                new ParallelCommandGroup(
                    // Arm
                    new SequentialCommandGroup(
                        // Score initial cone
                        new ConeHighPrePoseArm(),
                        new ConeHighScoreArmReturn()
                    ),

                    // Claw
                    new SequentialCommandGroup(
                        // Score initial cone
                        new ConeHighPrePoseClaw(),
                        new WaitCommand(1.0),
                        new ConeHighScoreClaw()
                    ),

                    // Intake
                    new SequentialCommandGroup(
                        // Score initial cone
                        new ConeHighPrePoseIntake(),

                        // Wait until we are clear of the divider wall
                        new WaitCommand(1.5),

                        // Deploy intake
                        new ParallelCommandGroup(
                            new IntakeArmRotateDumbCommand(0.4).withTimeout(1.875),
                            new IntakeRollerRunCommand().withTimeout(3)
                        )
                    )
                ), // End Step 2

                // Step 3: Stow intake
                new IntakeStowConeCommandGroup(),

                // Step 4: Score second cone
                new WaitCommand(1.0),
                new ConeHighPrePoseCommand(),
                new ConeHighScoreCommand()
            ),

            // Path movement
            new SequentialCommandGroup(
                new WaitCommand(3),
                TrajectoryUtil.createTrajectoryPathCommand(getClass().getSimpleName(), new PathConstraints(2.5, 1.5), true)
            )
        );
    }

    @Override
    public String toString() {
        return "Col 9, Col 7";
    }
}

package com.team2357.frc2023.commands.auto.red;

import com.pathplanner.lib.PathConstraints;
import com.team2357.frc2023.commands.auto.support.ConeHighPrePoseArm;
import com.team2357.frc2023.commands.auto.support.ConeHighPrePoseClaw;
import com.team2357.frc2023.commands.auto.support.ConeHighPrePoseIntake;
import com.team2357.frc2023.commands.auto.support.HighScoreArmReturn;
import com.team2357.frc2023.commands.auto.support.ConeHighScoreClaw;
import com.team2357.frc2023.commands.intake.IntakeArmRotateDumbCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerRunCommand;
import com.team2357.frc2023.commands.intake.IntakeStowConeCommandGroup;
import com.team2357.frc2023.commands.util.AutonomousZeroCommand;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class RedCol9Col7 extends ParallelCommandGroup {
    public RedCol9Col7() {
        addCommands(
            new SequentialCommandGroup(
                // Step 1: Initialize
                new AutonomousZeroCommand(),

                // Step 2: Throw initial cone and deploy intake
                new ParallelCommandGroup(
                    // Arm
                    new SequentialCommandGroup(
                        // Score initial cone
                        new ConeHighPrePoseArm(5.0).withTimeout(1.25),
                        new HighScoreArmReturn()
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

                        // Deploy intake
                        new ParallelCommandGroup(
                            new IntakeArmRotateDumbCommand(0.4).withTimeout(1.875),
                            new IntakeRollerRunCommand(0.5).withTimeout(5.0)
                        )
                    )
                ), // End Step 2

                // Step 3: Stow intake
                new IntakeStowConeCommandGroup(),

                // Step 4: Score second cone
                new WaitCommand(1.25),
                new ParallelCommandGroup(
                    // Arm
                    new SequentialCommandGroup(
                        // Score second cone
                        new ConeHighPrePoseArm(1.0).withTimeout(1.6),
                        new HighScoreArmReturn()
                    ),

                    // Claw
                    new SequentialCommandGroup(
                        // Score second cone
                        new ConeHighPrePoseClaw(),
                        new WaitCommand(1.4),
                        new ConeHighScoreClaw()
                    ),

                    // Intake
                    new SequentialCommandGroup(
                        // Score second cone
                        new ConeHighPrePoseIntake()
                    )
                ) // End Step 4
            ),

            // Path movement
            new SequentialCommandGroup(
                new WaitCommand(1.65),
                TrajectoryUtil.createTrajectoryPathCommand(getClass().getSimpleName(), new PathConstraints(3.0, 1.5), true)
            )
        );
    }

    @Override
    public String toString() {
        return "RED Col 9, Col 7";
    }
}

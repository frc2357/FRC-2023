package com.team2357.frc2023.commands.auto;

import com.pathplanner.lib.PathConstraints;
import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armextension.ArmExtendToPositionCommand;
import com.team2357.frc2023.commands.auto.support.ConeHighPrePoseArm;
import com.team2357.frc2023.commands.auto.support.ConeHighPrePoseClaw;
import com.team2357.frc2023.commands.auto.support.ConeHighPrePoseIntake;
import com.team2357.frc2023.commands.auto.support.HighScoreArmReturn;
import com.team2357.frc2023.commands.auto.support.ConeHighScoreClaw;
import com.team2357.frc2023.commands.auto.support.CubeHighPrePoseArm;
import com.team2357.frc2023.commands.auto.support.CubeHighPrePoseClaw;
import com.team2357.frc2023.commands.auto.support.CubeHighScoreClaw;
import com.team2357.frc2023.commands.intake.IntakeArmRotateDumbCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerRunCommand;
import com.team2357.frc2023.commands.intake.IntakeStowConeCommandGroup;
import com.team2357.frc2023.commands.intake.IntakeStowCubeCommandGroup;
import com.team2357.frc2023.commands.scoring.DumpGamePieceCommand;
import com.team2357.frc2023.commands.util.AutonomousZeroCommand;
import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class Col9Col7Col8 extends ParallelCommandGroup {
    public Col9Col7Col8() {
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
                        new IntakeRollerReverseCommand().withTimeout(0.5),
                        // Deploy intake
                        new ParallelCommandGroup(
                            new IntakeArmRotateDumbCommand(0.4).withTimeout(1.875),
                            new IntakeRollerRunCommand(0.5).withTimeout(3.5)
                        )
                    )
                ), // End Step 2

                // Step 3: Stow intake
                new IntakeStowConeCommandGroup().withTimeout(2.0),

                // Step 4: Score second cone (if we got it)
                new WaitCommand(0.25),
                new ConditionalCommand(
                    new ParallelCommandGroup(
                        // Arm
                        new SequentialCommandGroup(
                            // Score second cone
                            new ConeHighPrePoseArm(1.0).withTimeout(1.6),
                            new ArmExtendToPositionCommand(Constants.ARM_EXTENSION.AUTO_SCORE_CONE_HIGH_ROTATIONS - 5),
                            new HighScoreArmReturn()
                        ),

                        // Claw
                        new SequentialCommandGroup(
                            // Score second cone
                            new ConeHighPrePoseClaw(),
                            new WaitCommand(1.25),
                            new ConeHighScoreClaw()
                        ),

                        // Intake
                        // Score second cone
                        new IntakeRollerReverseCommand().withTimeout(0.5)
                    ),
                    new DumpGamePieceCommand().withTimeout(1.0),
                    () -> RobotState.hasCone()
                ), // End Step 4

                // Deploy intake
                new WaitCommand(0.5),
                new ParallelCommandGroup(
                    new IntakeArmRotateDumbCommand(0.4).withTimeout(1.875),
                    new IntakeRollerRunCommand(0.6).withTimeout(3.5)
                ),

                // Step 5: Stow intake
                new IntakeStowCubeCommandGroup(),

                // Step 6: Score cube
                new ParallelCommandGroup(
                    // Arm
                    new SequentialCommandGroup(
                        // Score cube
                        new CubeHighPrePoseArm().withTimeout(1.6),
                        new HighScoreArmReturn()
                    ),

                    // Claw
                    new SequentialCommandGroup(
                        // Score cube
                        new CubeHighPrePoseClaw(),
                        new WaitCommand(1.0),
                        new CubeHighScoreClaw()
                    ),

                    // Intake
                    new SequentialCommandGroup(
                        // Score cube
                        new IntakeRollerReverseCommand().withTimeout(0.5)
                    )
                ) // End Step 6
            ),

            // Path movement
            new SequentialCommandGroup(
                new WaitCommand(1.65),
                TrajectoryUtil.createTrajectoryPathCommand(getClass().getSimpleName(), new PathConstraints(3.25, 2.30), true)
            )
        );
    }

    @Override
    public String toString() {
        return "Col 9, Col 7, Col 8";
    }
}

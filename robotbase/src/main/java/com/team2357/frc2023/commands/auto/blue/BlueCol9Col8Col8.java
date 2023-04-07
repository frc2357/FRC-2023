package com.team2357.frc2023.commands.auto.blue;

import com.pathplanner.lib.PathConstraints;
import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armextension.ArmExtendToPositionCommand;
import com.team2357.frc2023.commands.auto.support.ConeHighPrePoseArm;
import com.team2357.frc2023.commands.auto.support.ConeHighPrePoseClaw;
import com.team2357.frc2023.commands.auto.support.HighScoreArmReturn;
import com.team2357.frc2023.commands.auto.support.ConeHighScoreClaw;
import com.team2357.frc2023.commands.auto.support.CubeHighPrePoseArm;
import com.team2357.frc2023.commands.auto.support.CubePrePoseClaw;
import com.team2357.frc2023.commands.auto.support.CubeScoreClaw;
import com.team2357.frc2023.commands.auto.support.CubeMidPrePoseArm;
import com.team2357.frc2023.commands.intake.IntakeArmRotateDumbCommand;
import com.team2357.frc2023.commands.intake.IntakeArmStowCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerRunCommand;
import com.team2357.frc2023.commands.intake.IntakeStowCubeCommandGroup;
import com.team2357.frc2023.commands.intake.WinchRotateToPositionCommand;
import com.team2357.frc2023.commands.util.AutonomousZeroCommand;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class BlueCol9Col8Col8 extends ParallelCommandGroup {
    public BlueCol9Col8Col8() {
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
                        new ArmExtendToPositionCommand(Constants.ARM_EXTENSION.SCORE_CUBE_HIGH_ROTATIONS - 5),
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
                        new WaitCommand(0.25),
                        // Deploy intake
                        new ParallelCommandGroup(
                            new IntakeArmRotateDumbCommand(0.6).withTimeout(1.3),
                            new IntakeRollerRunCommand(0.7).withTimeout(3.5)
                        )
                    )
                ), // End Step 2

                // Step 3: Stow intake
                new IntakeStowCubeCommandGroup().withTimeout(2.0),
               
                // Step 4: Score first cube
                new ParallelCommandGroup(
                    // Arm
                    new SequentialCommandGroup(
                        // Score first cube 
                        new CubeHighPrePoseArm().withTimeout(1.6),
                        new ArmExtendToPositionCommand(Constants.ARM_EXTENSION.SCORE_CUBE_HIGH_ROTATIONS - 5),
                        new HighScoreArmReturn()
                    ),

                    // Claw
                    new SequentialCommandGroup(
                        // Score first cube
                        new CubePrePoseClaw(),
                        new WaitCommand(0.85),
                        new CubeScoreClaw()
                    ),

                    // Intake
                    // Score first cube
                    new SequentialCommandGroup(
                        new IntakeRollerReverseCommand().withTimeout(0.5),
                        new WaitCommand(0.5),
                        new IntakeRollerRunCommand(0.7).withTimeout(3.5)
                    ),

                    new SequentialCommandGroup(
                        new WinchRotateToPositionCommand(Constants.INTAKE_ARM.INTAKE_HANDOFF_WINCH_ROTATIONS),
                        new IntakeArmStowCommand(),

                        // Deploy intake
                        new IntakeArmRotateDumbCommand(0.6).withTimeout(1.3)
                    )
                ), // End Step 4

                // Step 5: Stow intake
                new IntakeStowCubeCommandGroup().withTimeout(2.0),

                // Step 6: Score second cube
                new ParallelCommandGroup(
                    // Arm
                    new SequentialCommandGroup(
                        // Score second cube 
                        new CubeMidPrePoseArm().withTimeout(1.6),
                        new WaitCommand(1.75),
                        new HighScoreArmReturn()
                    ),

                    // Claw
                    new SequentialCommandGroup(
                        // Score second cube
                        new CubePrePoseClaw(),
                        new WaitCommand(2.0),
                        new CubeScoreClaw()
                    ),

                    // Intake
                    // Score second cube
                    new IntakeRollerReverseCommand().withTimeout(0.5),

                    new SequentialCommandGroup(
                        new WinchRotateToPositionCommand(Constants.INTAKE_ARM.INTAKE_HANDOFF_WINCH_ROTATIONS),
                        new IntakeArmStowCommand()
                    )
                ) // End Step 6
                ),

            // Path movement
            new SequentialCommandGroup(
                new WaitCommand(1.65),
                TrajectoryUtil.createTrajectoryPathCommand(getClass().getSimpleName(), new PathConstraints(3.25, 2.50), true)
            )
        );
    }

    @Override
    public String toString() {
        return "Col 9, Col 8, Col 8";
    }
}

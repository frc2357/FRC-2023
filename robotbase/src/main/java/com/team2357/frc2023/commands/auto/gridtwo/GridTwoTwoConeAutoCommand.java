package com.team2357.frc2023.commands.auto.gridtwo;

import com.team2357.frc2023.commands.claw.ClawInstantOpenCommand;
import com.team2357.frc2023.commands.intake.IntakeArmRotateDumbCommand;
import com.team2357.frc2023.commands.intake.IntakeDeployCommandGroup;
import com.team2357.frc2023.commands.intake.IntakeRollerRunCommand;
import com.team2357.frc2023.commands.intake.IntakeSolenoidExtendCommand;
import com.team2357.frc2023.commands.intake.IntakeStowCommandGroup;
import com.team2357.frc2023.commands.scoring.cone.ConeAutoScoreHighCommandGroup;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class GridTwoTwoConeAutoCommand extends ParallelCommandGroup {
    public GridTwoTwoConeAutoCommand() {
        // addCommands(new WaitCommand(0)
        // .andThen(new ConeAutoScoreHighCommandGroup()) // abt 7 seconds
        // .andThen(new IntakeDeployCommandGroup()
        // .withTimeout(3))
        // .andThen(new IntakeStowCommandGroup())
        // .andThen(new WaitCommand(2))
        // .andThen(new ConeAutoScoreHighCommandGroup()),

        // new WaitCommand(0) // 6
        // .andThen(TrajectoryUtil.createTrajectoryPathCommand("node0 to stage0", true))
        // // abt 4 seconds
        // .andThen(new WaitCommand(2))
        // .andThen(TrajectoryUtil.createTrajectoryPathCommand("stage0 to node2",
        // isFinished())), // abt 4
        // // seconds

        // new WaitCommand(8));

        addCommands(
            new SequentialCommandGroup(
                // Initialize
                new IntakeSolenoidExtendCommand(),

                // Score cone high
                new WaitCommand(0),
                new ConeAutoScoreHighCommandGroup(true).withTimeout(7.0),

                // Then deploy intake
                new WaitCommand(1.25),
                new ParallelCommandGroup(
                  new ClawInstantOpenCommand(),
                  new IntakeArmRotateDumbCommand(0.4).withTimeout(2.0),
                  new SequentialCommandGroup(
                    new WaitCommand(0.75),
                    new IntakeRollerRunCommand().withTimeout(1.5)
                  )
                ),

                // Then stow intake
                new IntakeStowCommandGroup()
            ),
            // Path movement
            new SequentialCommandGroup(
                new WaitCommand(7),
                TrajectoryUtil.createTrajectoryPathCommand("grid2 2 cone", true)
            )
        );
    }
}

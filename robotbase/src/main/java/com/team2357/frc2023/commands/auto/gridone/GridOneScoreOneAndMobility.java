package com.team2357.frc2023.commands.auto.gridone;

import com.team2357.frc2023.commands.claw.ClawInstantOpenCommand;
import com.team2357.frc2023.commands.intake.IntakeArmRotateDumbCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerRunCommand;
import com.team2357.frc2023.commands.intake.IntakeSolenoidExtendCommand;
import com.team2357.frc2023.commands.intake.IntakeStowCommandGroup;
import com.team2357.frc2023.commands.scoring.cone.ConeAutoScoreHighCommandGroup;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class GridOneScoreOneAndMobility extends ParallelCommandGroup {
    public GridOneScoreOneAndMobility() {
        addCommands(
            new SequentialCommandGroup(
                // Initialize
                new IntakeSolenoidExtendCommand(),

                // Score cone high
                new ConeAutoScoreHighCommandGroup(true).withTimeout(6.5),

                // Wait until we going over charge station
                new WaitCommand(1.5),

                // Then deploy intake after we're back on the floor
                new ParallelCommandGroup(
                  new ClawInstantOpenCommand(),
                  new IntakeArmRotateDumbCommand(0.6).withTimeout(2.0),
                  new SequentialCommandGroup(
                    new IntakeRollerRunCommand().withTimeout(2)
                  )
                ),

                // Then stow intake
                new IntakeStowCommandGroup()
            ),
            // Path movement
            new SequentialCommandGroup(
                new WaitCommand(3.5),
                TrajectoryUtil.createTrajectoryPathCommand("grid1 1 cone mobility", true)
        )
        );
    }
}

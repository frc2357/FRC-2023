package com.team2357.frc2023.commands.auto.gridtwo;

import com.team2357.frc2023.commands.intake.IntakeArmRotateDumbCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerRunCommand;
import com.team2357.frc2023.commands.intake.IntakeSolenoidExtendCommand;
import com.team2357.frc2023.commands.intake.IntakeStowConeCommandGroup;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class GridTwoTwoConeAutoCommand extends ParallelCommandGroup {
    public GridTwoTwoConeAutoCommand() {
        addCommands(
            new SequentialCommandGroup(
                // Initialize
                new IntakeSolenoidExtendCommand(),

                // Score cone high
                //new ConeAutoScoreHighCommandGroup(false).withTimeout(6.5),

                // Then deploy intake
                new ParallelCommandGroup(
                //   new ClawInstantOpenCommand(),
                  new IntakeArmRotateDumbCommand(0.6).withTimeout(1.0),
                  new SequentialCommandGroup(
                    new WaitCommand(0.5),
                    new IntakeRollerRunCommand().withTimeout(1.5)
                  )
                ),

                // Then stow intake
                new IntakeStowConeCommandGroup(),

                // Score cone high
                new WaitCommand(1)
                //new ConeAutoScoreHighCommandGroup(true)
            ),
            // Path movement
            new SequentialCommandGroup(
                new WaitCommand(3.5),
                TrajectoryUtil.createTrajectoryPathCommand("grid2 2 cone", true)
            )
        );
    }
}

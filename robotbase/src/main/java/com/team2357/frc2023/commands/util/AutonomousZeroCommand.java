package com.team2357.frc2023.commands.util;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armextension.ArmExtendAmpZeroCommand;
import com.team2357.frc2023.commands.everybot.WristAmpZeroCommand;
import com.team2357.frc2023.commands.intake.WinchAmpZeroCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutonomousZeroCommand extends ParallelDeadlineGroup {
    public AutonomousZeroCommand() {
        super(
            new WaitCommand(Constants.ZEROING.ZERO_ALL_DEADLINE_SECONDS),
            new SequentialCommandGroup(
                new ParallelInterruptCommandGroup(
                    new ParallelCommandGroup(
                        new InstantCommand(() -> RobotState.setRobotZeroed(false)),
                        new LogCommand("Zero All", "Failure", true)
                    ),
                    new WinchAmpZeroCommand(),
                    new ArmExtendAmpZeroCommand(),
                    new WristAmpZeroCommand()
                ),
                new ParallelCommandGroup(
                    new InstantCommand(() -> RobotState.setRobotZeroed(true)),
                    new LogCommand("Zero All", "Success", true)
                )
            ),
            new LogTimerCommand(
                Constants.ZEROING.ZERO_ALL_WARNING_SECONDS,
                "Zero All",
                "Took longer than " + Constants.ZEROING.ZERO_ALL_WARNING_SECONDS + "s"
            )
        );
    }
}

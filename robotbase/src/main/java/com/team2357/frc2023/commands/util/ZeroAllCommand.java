package com.team2357.frc2023.commands.util;

import com.team2357.frc2023.commands.armextension.ArmExtendAmpZeroCommand;
import com.team2357.frc2023.commands.armrotation.ArmZeroCommandGroup;
import com.team2357.frc2023.commands.everybot.WristAmpZeroCommand;
import com.team2357.frc2023.commands.intake.WinchAmpZeroCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ZeroAllCommand extends ParallelCommandGroup {
    public ZeroAllCommand() {
        super(
            new SequentialCommandGroup(
                new ParallelCommandGroup(
                    new WinchAmpZeroCommand(),
                    new ArmExtendAmpZeroCommand(),
                    new WristAmpZeroCommand(),
                    new ArmZeroCommandGroup(),
                    new InterruptCommand(
                        new LogCommand("Zero All", "User Cancelled", true)
                    )
                ),
                new ParallelCommandGroup(
                    new InstantCommand(() -> RobotState.setRobotZeroed(true)),
                    new LogCommand("Zero All", "Success", true)
                )
            )
        );
    }
}

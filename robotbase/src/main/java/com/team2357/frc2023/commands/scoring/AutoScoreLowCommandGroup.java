package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.claw.ClawInstantCloseCommand;
import com.team2357.frc2023.commands.claw.ClawInstantOpenCommand;
import com.team2357.frc2023.commands.intake.IntakeArmStowCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerStopCommand;
import com.team2357.frc2023.commands.intake.WinchRotateToPositionCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoScoreLowCommandGroup extends ParallelCommandGroup {
    public AutoScoreLowCommandGroup() {
        addCommands(
            new WaitCommand(0.25)
                    .andThen(new ClawInstantOpenCommand())
                    .andThen(new WaitCommand(2))
                    .andThen(new ClawInstantCloseCommand()),

            new WaitCommand(0.5)
                    .andThen(new WinchRotateToPositionCommand(Constants.INTAKE_ARM.AUTO_SCORE_LOW_ROTATIONS))
                    .andThen(new WaitCommand(0.75))
                    .andThen(new IntakeArmStowCommand()),

            new WaitCommand(1)
                    .andThen(new IntakeRollerReverseCommand()
                            .withTimeout(0.5))
                    .andThen(new IntakeRollerStopCommand())
        );
    }
}

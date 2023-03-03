package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.claw.ClawOpenCommand;
import com.team2357.frc2023.commands.claw.CloseClawCommand;
import com.team2357.frc2023.commands.intake.IntakeArmDeployCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;

import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoScoreLowCommandGroup extends SequentialCommandGroup {
    public AutoScoreLowCommandGroup() {
        addCommands(new ClawOpenCommand());

        addCommands(new IntakeArmDeployCommand());

        addCommands(new ParallelRaceGroup(
            new IntakeRollerReverseCommand(),
            new WaitCommand(Constants.INTAKE_ROLLER.AUTO_SCORE_LOW_REVERSE_TIME)
        ));

        addCommands(new CloseClawCommand());
    }
}

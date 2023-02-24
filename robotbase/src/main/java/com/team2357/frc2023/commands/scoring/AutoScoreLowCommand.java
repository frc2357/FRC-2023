package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.intake.IntakeArmDeployCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.scoring.util.ExtendArmToPositionCommand;
import com.team2357.frc2023.commands.scoring.util.OpenClawCommand;

import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoScoreLowCommand extends SequentialCommandGroup {
    public AutoScoreLowCommand() {
        addCommands(new OpenClawCommand());

        addCommands(new ParallelRaceGroup(
            new ExtendArmToPositionCommand(Constants.ARM_EXTENSION.RETRACTED_ROTATIONS),
            new WaitCommand(Constants.ARM_EXTENSION.AUTO_SCORE_LOW_RETRACT_WAIT_TIME)
        ));
        addCommands(new IntakeArmDeployCommand());

        addCommands(new ParallelRaceGroup(
            new IntakeRollerReverseCommand(),
            new WaitCommand(Constants.INTAKE_ROLLER.AUTO_SCORE_LOW_REVERSE_TIME)
        ));
    }
}

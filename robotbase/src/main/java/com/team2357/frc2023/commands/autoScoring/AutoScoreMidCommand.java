package com.team2357.frc2023.commands.autoScoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.intake.ReverseIntakeCommand;

import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class AutoScoreMidCommand extends SequentialCommandGroup {
    public AutoScoreMidCommand() {
        // Pull game piece away from intake
        addCommands(new ParallelRaceGroup(
            new ExtendArmToPositionCommand(Constants.ARM_EXTENSION.RETRACTED_ROTATIONS),
            new ReverseIntakeCommand()
        ));

        // Extend to node
        addCommands(new RotateArmToPositionCommand(Constants.ARM_ROTATION.AUTO_SCORE_MID_ROTATIONS));
        addCommands(new ExtendArmToPositionCommand(Constants.ARM_EXTENSION.AUTO_SCORE_MID_ROTATIONS));

        // Release game piece
        addCommands(new ExtendWristCommand());
        addCommands(new OpenClawCommand());
    }
}

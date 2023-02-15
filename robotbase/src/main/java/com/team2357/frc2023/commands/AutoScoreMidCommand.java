package com.team2357.frc2023.commands;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.scoring.ExtendArmToPositionCommand;
import com.team2357.frc2023.commands.scoring.ExtendWristCommand;
import com.team2357.frc2023.commands.scoring.OpenClawCommand;
import com.team2357.frc2023.commands.scoring.RetractWristCommand;
import com.team2357.frc2023.commands.scoring.RotateArmToPositionCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
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

package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.scoring.util.ArmExtendToPositionCommand;
import com.team2357.frc2023.commands.scoring.util.ExtendWristCommand;
import com.team2357.frc2023.commands.scoring.util.OpenClawCommand;
import com.team2357.frc2023.commands.scoring.util.RotateArmToPositionCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoScoreMidCommandGroup extends ParallelCommandGroup {
    public AutoScoreMidCommandGroup() {
        // Pull game piece away from intake
        addCommands(new ParallelRaceGroup(
            new WaitCommand(0),
            new IntakeRollerReverseCommand()
        ));

        addCommands(new SequentialCommandGroup(
            // Extend to node 
            new RotateArmToPositionCommand(Constants.ARM_ROTATION.AUTO_SCORE_MID_ROTATIONS),
            new ArmExtendToPositionCommand(Constants.ARM_EXTENSION.AUTO_SCORE_MID_ROTATIONS),

            // Release the game piece
            new ExtendWristCommand(),
            new OpenClawCommand()
        ));
    }
}

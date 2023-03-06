package com.team2357.frc2023.commands.scoring.cone;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armextension.ArmExtendToPositionCommand;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.claw.ClawOpenCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.scoring.ArmReturnToStartCommandGroup;
import com.team2357.frc2023.commands.wrist.WristExtendCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class ConeAutoScoreHighCommandGroup extends ParallelCommandGroup {
    public ConeAutoScoreHighCommandGroup() {
        // Pull game piece away from intake

        // addCommands(new ParallelRaceGroup(
        //     new WaitCommand(0),
        //     new IntakeRollerReverseCommand()
        // ));

        System.out.println("AutoScoreHighCommandGroup");
        addCommands(new SequentialCommandGroup(
            // Extend to node 
            new ArmRotateToPositionCommand(Constants.ARM_ROTATION.AUTO_SCORE_HIGH_ROTATIONS)
            // new ArmExtendToPositionCommand(Constants.ARM_EXTENSION.AUTO_SCORE_HIGH_ROTATIONS),

            // // Release the game piece
            // new WristExtendCommand(),
            // new ClawOpenCommand(),

            // // Return to starting position
            // new WaitCommand(Constants.AUTO_SCORE_TIMINGS.SECONDS_BEFORE_RETURNING_TO_STARTING_POSITION),
            // new ArmReturnToStartCommandGroup()
        ));
    }
}

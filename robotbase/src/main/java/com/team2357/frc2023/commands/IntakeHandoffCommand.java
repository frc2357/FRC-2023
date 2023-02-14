package com.team2357.frc2023.commands;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.scoring.CloseClawCommand;
import com.team2357.frc2023.commands.scoring.ExtendArmToPositionCommand;
import com.team2357.frc2023.commands.scoring.ExtendWristCommand;
import com.team2357.frc2023.commands.scoring.OpenClawCommand;
import com.team2357.frc2023.commands.scoring.RotateArmToPositionCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class IntakeHandoffCommand extends SequentialCommandGroup {
    public IntakeHandoffCommand() {
        // Get arm into position
        addCommands(new ParallelCommandGroup(
            new StowIntakeCommand(),
            new OpenClawCommand(),
            new ExtendWristCommand(),
            new ExtendArmToPositionCommand(Constants.ARM_EXTENSION.RETRACTED_ROTATIONS)
        ));
        addCommands(new RotateArmToPositionCommand(Constants.ARM_ROTATION.INTAKE_HANDOFF_ROTATIONS));

        // Grab the game piece
        addCommands(new ExtendArmToPositionCommand(Constants.ARM_EXTENSION.INTAKE_HANDOFF_ROTATIONS));
        addCommands(new CloseClawCommand());
    }
}

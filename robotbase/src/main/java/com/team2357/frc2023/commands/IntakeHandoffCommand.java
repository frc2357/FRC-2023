package com.team2357.frc2023.commands;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.autoScoring.CloseClawCommand;
import com.team2357.frc2023.commands.autoScoring.ExtendArmToPositionCommand;
import com.team2357.frc2023.commands.autoScoring.RotateArmToPositionCommand;
import com.team2357.frc2023.commands.intake.StowIntakeCommand;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class IntakeHandoffCommand extends SequentialCommandGroup {
    public IntakeHandoffCommand() {
        // Get arm into position
        addCommands(new StowIntakeCommand());
        addCommands(new RotateArmToPositionCommand(Constants.ARM_ROTATION.INTAKE_HANDOFF_ROTATIONS));

        // Grab the game piece
        addCommands(new ExtendArmToPositionCommand(Constants.ARM_EXTENSION.INTAKE_HANDOFF_ROTATIONS));
        addCommands(new CloseClawCommand());
    }
}
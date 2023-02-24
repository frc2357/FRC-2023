package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.scoring.util.CloseClawCommand;
import com.team2357.frc2023.commands.scoring.util.ExtendArmToPositionCommand;
import com.team2357.frc2023.commands.scoring.util.RotateArmToPositionCommand;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class IntakeHandoffCommandGroup extends SequentialCommandGroup {
    public IntakeHandoffCommandGroup() {
        // Get arm into position
        addCommands(new RotateArmToPositionCommand(Constants.ARM_ROTATION.INTAKE_HANDOFF_ROTATIONS));

        // Grab the game piece
        addCommands(new ExtendArmToPositionCommand(Constants.ARM_EXTENSION.INTAKE_HANDOFF_ROTATIONS));
        addCommands(new CloseClawCommand());
    }
}
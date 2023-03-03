package com.team2357.frc2023.commands.scoring.util;

import com.team2357.frc2023.Constants;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ArmReturnToStartCommandGroup extends SequentialCommandGroup {
    public ArmReturnToStartCommandGroup() {
        addCommands(new CloseClawCommand());
        addCommands(new ArmRotateToPositionCommand(Constants.ARM_ROTATION.RETRACTED_ROTATIONS));
        addCommands(new ArmRetractToPositionCommand(Constants.ARM_EXTENSION.RETRACTED_ROTATIONS));
    }
}

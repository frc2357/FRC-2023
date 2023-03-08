package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armextension.ArmRetractToPositionCommand;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.claw.CloseClawCommand;
import com.team2357.frc2023.commands.wrist.WristRetractCommand;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class ArmReturnToStartCommandGroup extends SequentialCommandGroup {
    public ArmReturnToStartCommandGroup() {
        addCommands(new WristRetractCommand());
        addCommands(new CloseClawCommand());
        //addCommands(new ArmRetractToPositionCommand(Constants.ARM_EXTENSION.RETRACTED_ROTATIONS));
        addCommands(new WaitCommand(2));
        addCommands(new ArmRotateToPositionCommand(Constants.ARM_ROTATION.RETRACTED_ROTATIONS));
    }
}

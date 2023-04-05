package com.team2357.frc2023.commands.auto.support;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armextension.ArmExtendToPositionCommand;
import com.team2357.frc2023.commands.armextension.ArmExtensionMotorStopCommand;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.everybot.WristRotateToPositionCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class HighScoreArmReturn extends SequentialCommandGroup  {
    public HighScoreArmReturn() {
        super(
            new ParallelCommandGroup(
                new ArmExtendToPositionCommand(Constants.ARM_EXTENSION.RETRACTED_ROTATIONS),
                new WristRotateToPositionCommand(Constants.WRIST.WRIST_EXTENSION_RETRACT_ROTATIONS),

                new SequentialCommandGroup(
                    new ArmRotateToPositionCommand(Constants.ARM_ROTATION.RETRACTED_ROTATIONS)
                )
            ),
            new ArmExtensionMotorStopCommand(),
            new WristRotateToPositionCommand(Constants.WRIST.WRIST_RETRACT_ROTATIONS, true)
        );
    }
}

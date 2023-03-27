package com.team2357.frc2023.commands.armrotation;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ArmZeroCommandGroup extends SequentialCommandGroup {
    public ArmZeroCommandGroup() {
        addCommands(new ArmRotationPreZeroWithAmpCommand());
        addCommands(new ArmEncoderZeroCommand());
    }
}

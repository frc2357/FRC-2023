package com.team2357.frc2023.commands.armrotation;

import com.team2357.frc2023.subsystems.ArmRotationSubsystem;

public class ArmRotationZeroCommand extends ArmRotateToPositionCommand {
    public ArmRotationZeroCommand() {
        super(ArmRotationSubsystem.getInstance().getZeroPosition());
    }
}

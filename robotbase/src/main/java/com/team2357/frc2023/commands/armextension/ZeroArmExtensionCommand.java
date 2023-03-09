package com.team2357.frc2023.commands.armextension;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ZeroArmExtensionCommand extends CommandBase {
    public ZeroArmExtensionCommand() {
        addRequirements(ArmExtensionSubsystem.getInstance());
    }    

    @Override
    public void initialize() {
        ArmExtensionSubsystem.getInstance().manualExtend(Constants.ARM_EXTENSION.AMPERAGE_ZERO_PERCENT_OUTPUT);
    }

    @Override
    public boolean isFinished() {
        return ArmExtensionSubsystem.getInstance().getAmps() >= Constants.ARM_EXTENSION.AMPERAGE_ZERO_MAX_AMPS;
    }
}
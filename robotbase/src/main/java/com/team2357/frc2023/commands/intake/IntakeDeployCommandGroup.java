package com.team2357.frc2023.commands.intake;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class IntakeDeployCommandGroup extends ParallelCommandGroup {
    public   IntakeDeployCommandGroup() {
        addCommands(new IntakeArmDeployCommand());
        addCommands(new IntakeRollerRunCommand());
        // addCommands(new ExtendArmToPositionCommand(Constants.ARM_EXTENSION.RETRACTED_ROTATIONS));
        // addCommands(new OpenClawCommand());
    }
}

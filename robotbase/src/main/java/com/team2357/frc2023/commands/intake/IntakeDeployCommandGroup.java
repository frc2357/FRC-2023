package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.autoScoring.ExtendArmToPositionCommand;
import com.team2357.frc2023.commands.autoScoring.OpenClawCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class IntakeDeployCommandGroup extends ParallelCommandGroup {
    public IntakeDeployCommandGroup() {
        addCommands(new DeployIntakeCommand());
        addCommands(new ExtendArmToPositionCommand(Constants.ARM_EXTENSION.RETRACTED_ROTATIONS));
        addCommands(new OpenClawCommand());
    }
}

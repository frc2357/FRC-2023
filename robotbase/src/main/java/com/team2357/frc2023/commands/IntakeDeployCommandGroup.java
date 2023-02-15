package com.team2357.frc2023.commands;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.scoring.ExtendArmToPositionCommand;
import com.team2357.frc2023.commands.scoring.OpenClawCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class IntakeDeployCommandGroup extends ParallelCommandGroup {
    public IntakeDeployCommandGroup() {
        addCommands(new DeployIntakeCommand());
        addCommands(new ExtendArmToPositionCommand(Constants.ARM_EXTENSION.RETRACTED_ROTATIONS));
        addCommands(new OpenClawCommand());
    }
}

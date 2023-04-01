package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armextension.ArmExtendToPositionCommand;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.intake.IntakeArmStowCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class HomeMechanismsCommand extends SequentialCommandGroup {

    public HomeMechanismsCommand() {
        addCommands(
                new ParallelCommandGroup(
                        // new ClawInstantCloseCommand(),
                        // new WristInstantRetractCommand(),
                        new ArmExtendToPositionCommand(Constants.ARM_EXTENSION.RETRACTED_ROTATIONS),
                        new WaitCommand(1.5)));

        addCommands(new ArmRotateToPositionCommand(Constants.ARM_ROTATION.SCORE_CONE_MID_ROTATIONS));

        addCommands(new IntakeArmStowCommand());

        addCommands(new ArmRotateToPositionCommand(Constants.ARM_ROTATION.RETRACTED_ROTATIONS));
    }
}
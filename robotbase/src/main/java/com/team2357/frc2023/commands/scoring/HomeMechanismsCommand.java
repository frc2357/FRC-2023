package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armextension.ArmExtendToPositionCommand;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.armrotation.ArmRotationZeroCommand;
import com.team2357.frc2023.commands.claw.ClawInstantCloseCommand;
import com.team2357.frc2023.commands.intake.IntakeArmStowCommand;
import com.team2357.frc2023.commands.wrist.WristInstantRetractCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class HomeMechanismsCommand extends SequentialCommandGroup {

    public HomeMechanismsCommand() {
        addCommands(
                new ParallelCommandGroup(
                        new ClawInstantCloseCommand(),
                        new WristInstantRetractCommand(),
                        new ArmExtendToPositionCommand(Constants.ARM_EXTENSION.RETRACTED_ROTATIONS),
                        new WaitCommand(1.5)));

        addCommands(new ArmRotateToPositionCommand(Constants.ARM_ROTATION.AUTO_SCORE_MID_POSITION));

        addCommands(new IntakeArmStowCommand());

        addCommands(new ArmRotationZeroCommand());
    }
}
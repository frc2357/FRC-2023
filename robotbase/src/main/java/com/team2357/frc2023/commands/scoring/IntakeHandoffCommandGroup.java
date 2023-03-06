package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.intake.WinchRotateToPositionCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class IntakeHandoffCommandGroup extends ParallelCommandGroup {
    public IntakeHandoffCommandGroup() {
        addCommands(new WinchRotateToPositionCommand(Constants.INTAKE_ARM.INTAKE_HANDOFF_WINCH_ROTATIONS));
        addCommands(new IntakeRollerReverseCommand());
        addCommands(new ArmRotateToPositionCommand(Constants.ARM_ROTATION.ARM_ROTATION_GEAR_RATIO/8));
    }
}

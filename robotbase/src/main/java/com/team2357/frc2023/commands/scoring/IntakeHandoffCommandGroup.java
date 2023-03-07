package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.intake.WinchRotateToPositionCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class IntakeHandoffCommandGroup extends ParallelDeadlineGroup {
    public IntakeHandoffCommandGroup() {
        super(new WinchRotateToPositionCommand(Constants.INTAKE_ARM.INTAKE_HANDOFF_WINCH_ROTATIONS));
        addCommands(new IntakeRollerReverseCommand());
        addCommands(
                new SequentialCommandGroup(
                        new WaitCommand(0.25),
                        new ArmRotateToPositionCommand(Constants.ARM_ROTATION.ARM_HANDOFF_ROTATIONS)));
    }
}

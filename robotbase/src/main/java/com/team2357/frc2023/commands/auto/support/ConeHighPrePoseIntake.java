package com.team2357.frc2023.commands.auto.support;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.intake.IntakeArmStowCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.intake.WinchRotateToPositionCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class ConeHighPrePoseIntake extends ParallelCommandGroup {
    public ConeHighPrePoseIntake() {
        super(
            // Intake Rollers
            new IntakeRollerReverseCommand().withTimeout(1),

            // Intake Arm
            new SequentialCommandGroup(
                new WaitCommand(0.25),
                new WinchRotateToPositionCommand(Constants.INTAKE_ARM.INTAKE_HANDOFF_WINCH_ROTATIONS),
                new WaitCommand(0.25),
                new IntakeArmStowCommand()
            )
        );
    }
}

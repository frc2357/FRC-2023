package com.team2357.frc2023.commands.auto.support;

import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class ConeHighPrePoseIntake extends ParallelCommandGroup {
    public ConeHighPrePoseIntake() {
        super(
            // Intake Rollers
            new IntakeRollerReverseCommand().withTimeout(0.5)
        );
    }
}

package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.commands.claw.ClawOpenCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class IntakeDeployCommandGroup extends ParallelCommandGroup {
    public IntakeDeployCommandGroup() {
        addCommands(new IntakeArmDeployCommand());
        addCommands(new IntakeRollerRunCommand());
        addCommands(new ClawOpenCommand());
    }
}

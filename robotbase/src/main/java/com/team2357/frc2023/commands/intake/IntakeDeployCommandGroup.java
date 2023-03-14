package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.commands.claw.ClawOpenCommand;
import com.team2357.frc2023.commands.util.WaitForGamepieceCommand;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;

public class IntakeDeployCommandGroup extends ParallelDeadlineGroup {
    public IntakeDeployCommandGroup() {
        super(new WaitForGamepieceCommand());
        addCommands(new InstantCommand(() -> DualLimelightManagerSubsystem.getInstance().setHumanPipelineActive()));
        addCommands(new IntakeArmDeployCommand());
        addCommands(new IntakeRollerRunCommand());
        addCommands(new ClawOpenCommand());
    }
}

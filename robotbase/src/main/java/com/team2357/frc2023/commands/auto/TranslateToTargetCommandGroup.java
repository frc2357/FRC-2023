package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class TranslateToTargetCommandGroup extends SequentialCommandGroup {
    public TranslateToTargetCommandGroup() {
        this.addCommands(new RotateToDegreeCommand(0), new WaitCommand(0.1), new TranslateToTargetYCommand(),
        new WaitCommand(0.1),new TranslateToTargetXCommand());
    }
}

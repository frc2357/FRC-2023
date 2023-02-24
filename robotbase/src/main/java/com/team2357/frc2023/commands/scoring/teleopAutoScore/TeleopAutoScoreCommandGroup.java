package com.team2357.frc2023.commands.scoring.teleopAutoScore;

import com.team2357.frc2023.commands.scoring.TranslateToColumnCommand;
import com.team2357.frc2023.networktables.Buttonboard;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class TeleopAutoScoreCommandGroup extends SequentialCommandGroup {
    public TeleopAutoScoreCommandGroup() {
        addCommands(new WaitForTargetCommand());
        // TODO: Find a way to select an april tag to base this off of
        addCommands(new TranslateToColumnCommand(SwerveDriveSubsystem.getSetpoint(Buttonboard.getInstance().getColValue())));
        addCommands(SwerveDriveSubsystem.getAutoScoreCommands(Buttonboard.getInstance().getRowValue()));
    }
}
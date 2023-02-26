package com.team2357.frc2023.commands.scoring.teleopAutoScore;

import com.team2357.frc2023.commands.auto.TranslateToTargetCommandGroup;
import com.team2357.frc2023.networktables.Buttonboard;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.util.function.BooleanConsumer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class TeleopAutoScoreCommandGroup extends CommandBase {

    private Command m_teleopTrajectory;
    private Command m_autoScoreCommand;

    private boolean m_hasScored;

    public TeleopAutoScoreCommandGroup() {
        // addCommands(new WaitForTargetCommand());
        // // TODO: Find a way to select an april tag to base this off of
        // addCommands(new TranslateToTargetCommandGroup((SwerveDriveSubsystem.getSetpoint(Buttonboard.getInstance().getColValue()))));
        // addCommands(SwerveDriveSubsystem.getAutoScoreCommands(Buttonboard.getInstance().getRowValue()));
    }

    @Override
    public void initialize() {
        m_hasScored = false;
    }

    @Override
    public void execute() {

    }

    @Override
    public boolean isFinished() {
        return m_hasScored;
    }   

    @Override 
    public void end(boolean interrupted) {
        m_teleopTrajectory.cancel();
        m_autoScoreCommand.cancel();
    }
}
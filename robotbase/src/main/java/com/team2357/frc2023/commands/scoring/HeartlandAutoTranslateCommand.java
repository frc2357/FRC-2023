package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.auto.TranslateToTargetCommandGroup;
import com.team2357.frc2023.commands.controller.RumbleCommand;
import com.team2357.frc2023.networktables.Buttonboard;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.frc2023.util.Utility;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class HeartlandAutoTranslateCommand extends CommandBase {
    private SequentialCommandGroup m_teleopCommand;

    private boolean m_hasTranslated;

    private XboxController m_controller;

    public HeartlandAutoTranslateCommand(XboxController controller) {
        m_controller = controller;
    }

    @Override
    public void initialize() {
        m_hasTranslated = false;

        int col = Buttonboard.getInstance().getColValue();

        m_teleopCommand = new TranslateToTargetCommandGroup(SwerveDriveSubsystem.getSetpoint(col % 3),
                Utility.gridColumnToAprilTagID(col))
                .andThen(new InstantCommand(() -> m_hasTranslated = true));

        m_teleopCommand.schedule();
    }

    @Override
    public void execute() {
        if (!SwerveDriveSubsystem.getInstance().hasTarget()) {
            RumbleCommand.createRumbleCommand(m_controller,
                    Constants.CONTROLLER.RUMBLE_TIMEOUT_SECONDS_ON_TELEOP_AUTO).schedule();
        }
    }

    @Override
    public boolean isFinished() {
        return m_hasTranslated;
    }

    @Override
    public void end(boolean interrupted) {
        m_teleopCommand.cancel();
    }
}

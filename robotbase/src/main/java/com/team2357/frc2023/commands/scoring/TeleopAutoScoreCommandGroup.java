package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.apriltag.GridCamEstimator;
import com.team2357.frc2023.commands.auto.TranslateToTargetCommandGroup;
import com.team2357.frc2023.commands.controller.RumbleCommand;
import com.team2357.frc2023.networktables.GridCam;
import com.team2357.frc2023.networktables.Buttonboard;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.frc2023.trajectoryutil.AvailableTeleopTrajectories;
import com.team2357.frc2023.util.Utility;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class TeleopAutoScoreCommandGroup extends CommandBase {

    private SequentialCommandGroup m_teleopCommand;

    private boolean m_hasScored;

    private XboxController m_controller;

    public TeleopAutoScoreCommandGroup(XboxController controller) {
        m_controller = controller;
    }

    @Override
    public void initialize() {
        m_hasScored = false;
        m_teleopCommand = null;
    }

    // For fancy auto after Heartland
    @Override
    public void execute() {
        if (m_teleopCommand == null) {
            Pose2d currentPose = GridCamEstimator.getInstance().estimateRobotPose().getPose();
            int col = Buttonboard.getInstance().getColValue();
            Command teleopTrajectory = AvailableTeleopTrajectories.buildTrajectory(col,
                    currentPose);

            if (teleopTrajectory != null) {
                Command autoScore = SwerveDriveSubsystem.getAutoScoreCommands(Buttonboard.getInstance().getRowValue(),
                        Buttonboard.getInstance().getColValue());
                Command translate = new TranslateToTargetCommandGroup(SwerveDriveSubsystem.getSetpoint(col % 3),
                        Utility.gridColumnToAprilTagID(col));

                m_teleopCommand = new SequentialCommandGroup();
                m_teleopCommand.addCommands(teleopTrajectory);
                m_teleopCommand.addCommands(translate);
                m_teleopCommand.addCommands(autoScore);
                m_teleopCommand.addCommands(new InstantCommand(() -> {
                    m_hasScored = true;
                }));
                m_teleopCommand.schedule();
                RumbleCommand.createRumbleCommand(m_controller,
                        Constants.CONTROLLER.RUMBLE_TIMEOUT_SECONDS_ON_TELEOP_AUTO).schedule();
            }
        }
    }

    @Override
    public boolean isFinished() {
        return m_hasScored;
    }

    @Override
    public void end(boolean interrupted) {
        m_teleopCommand.cancel();
    }
}
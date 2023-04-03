package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.auto.DriveToPoseCommand;
import com.team2357.frc2023.networktables.Buttonboard;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;
import com.team2357.frc2023.util.Utility;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutoLineupCommand extends CommandBase {

    DriveToPoseCommand m_driveToPose;
    Pose2d m_targetPose;
    XboxController m_controller;

    public AutoLineupCommand(XboxController controller) {
        m_controller = controller;
    }

    @Override
    public void initialize() {
        int targetCol = Buttonboard.getInstance().getColValue();
        m_targetPose = Utility.gridColumnToTargetPose(targetCol);
        m_driveToPose = null;
    }

    @Override
    public void execute() {
        if (m_driveToPose == null) {
            Pose2d visionPose = DualLimelightManagerSubsystem.getInstance().getAveragePose();
            if (visionPose != null) {
                if (visionPose.getTranslation().getDistance(m_targetPose.getTranslation()) <= Constants.DRIVE.AUTO_LINEUP_RANGE_METERS) {

                    m_driveToPose = new DriveToPoseCommand(visionPose, m_targetPose);
                    m_driveToPose.schedule();
                }
            } else {
               m_controller.setRumble(RumbleType.kBothRumble, Constants.CONTROLLER.RUMBLE_INTENSITY);
            }
        }
    }

    @Override
    public boolean isFinished() {
        return m_driveToPose != null && m_driveToPose.atGoal();
    }

    @Override
    public void end(boolean interrupted) {

        if (m_driveToPose != null) {
            m_driveToPose.cancel();
        }
        m_controller.setRumble(RumbleType.kBothRumble, 0.0);
    }
}

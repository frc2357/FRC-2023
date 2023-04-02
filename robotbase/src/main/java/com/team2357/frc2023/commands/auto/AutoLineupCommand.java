package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.commands.controller.RumbleCommand;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.XboxController;
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
        m_targetPose = new Pose2d(1.77, 3.3, Rotation2d.fromDegrees(180));
        m_driveToPose = null;
    }

    @Override
    public void execute() {
        if (m_driveToPose == null) {
            Pose2d visionPose = DualLimelightManagerSubsystem.getInstance().getLimelightPose2d();
            if (visionPose != null) {
                if (visionPose.getTranslation().getDistance(m_targetPose.getTranslation()) <= 1) {

                    m_driveToPose = new DriveToPoseCommand(visionPose, m_targetPose);
                    m_driveToPose.schedule();
                }
            } else {
                RumbleCommand.createRumbleCommand(m_controller, 0.25).schedule();;
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
    }
}

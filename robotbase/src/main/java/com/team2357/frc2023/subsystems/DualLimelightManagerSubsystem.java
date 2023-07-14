package com.team2357.frc2023.subsystems;

import org.littletonrobotics.junction.Logger;

import com.team2357.lib.subsystems.LimelightSubsystem;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

// Manages two limelight subsystems
public class DualLimelightManagerSubsystem extends SubsystemBase {

    private static DualLimelightManagerSubsystem m_instance;

    public static DualLimelightManagerSubsystem getInstance() {
        return m_instance;
    }

    public enum LIMELIGHT {
        RIGHT,
        LEFT
    };

    private LimelightSubsystem m_leftLimelight;
    private LimelightSubsystem m_rightLimelight;

    /**
     * 
     * @param leftLimelightName        Name of the left side limelight
     * @param rightLimelightName       Name ofr the right side limelight
     * 
     */
    public DualLimelightManagerSubsystem(String leftLimelightName, String rightLimelightName) {
        m_leftLimelight = new LimelightSubsystem(leftLimelightName);
        m_rightLimelight = new LimelightSubsystem(rightLimelightName);

        m_instance = this;
    }

    public LimelightSubsystem getLimelight(LIMELIGHT limelight) {
        switch (limelight) {
            case LEFT:
                return m_leftLimelight;
            case RIGHT:
                return m_rightLimelight;
        }
        return null;
    }

    public void setAprilTagPipelineActive() {
        m_leftLimelight.setAprilTagPipelineActive();
        m_rightLimelight.setAprilTagPipelineActive();
    }

    public boolean isAprilTagPipelineActive() {
        return m_leftLimelight.isAprilTagPipelineActive() && m_rightLimelight.isAprilTagPipelineActive();
    }

    public void setHumanPipelineActive() {
        m_leftLimelight.setHumanPipelineActive();
        m_rightLimelight.setHumanPipelineActive();
    }

    public boolean isHumanPipelineActive() {
        return m_leftLimelight.isHumanPipelineActive() && m_rightLimelight.isHumanPipelineActive();
    }

    public void setGamepieceDetectorPipelineActive() {
        m_rightLimelight.setGamepieceDetectorPipelineActive();
        m_leftLimelight.setAprilTagPipelineActive();
    }

    public boolean isGamepieceDetectorPipelineActive() {
       return m_rightLimelight.isGamepieceDetectorPipelineActive();
    }

    public boolean validTargetExists() {
        return m_leftLimelight.validTargetExists() || m_rightLimelight.validTargetExists();
    }

    public Pose2d getAveragePose() {
        if (m_leftLimelight.validTargetExists() && m_rightLimelight.validTargetExists()) {
            Pose2d leftPose = m_leftLimelight.getCurrentAllianceLimelightPose();
            Pose2d rightPose = m_rightLimelight.getCurrentAllianceLimelightPose();

            double xLocation = (leftPose.getX() + rightPose.getX()) / 2;
            double yLocation = (leftPose.getY() + rightPose.getY()) / 2;
            double rotationDegrees = (leftPose.getRotation().getDegrees() + rightPose.getRotation().getDegrees()) / 2;

            return new Pose2d(xLocation, yLocation, Rotation2d.fromDegrees(rotationDegrees));
        } else if (m_leftLimelight.validTargetExists()) {
            return m_leftLimelight.getCurrentAllianceLimelightPose();
        } else if (m_rightLimelight.validTargetExists()) {
            return m_rightLimelight.getCurrentAllianceLimelightPose();
        } else {
            return null;
        }
    }
}

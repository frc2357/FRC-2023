package com.team2357.frc2023.subsystems;

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

    private LimelightSubsystem m_primaryLimelight;
    private LimelightSubsystem m_secondaryLimelight;

    private double m_leftLimelightTXSetpoint;
    private double m_rightLimelightTXSetpoint;
    private double m_primaryLimelightTXSetpoint;

    private int m_targetAprilTag;

    /**
     * 
     * @param leftLimelightName Name of the left side limelight
     * @param rightLimelightName Name ofr the right side limelight
     * @param leftLimelightTXSetpoint Value of the X angle of the right limelight to get the left limelight in view
     * @param rightLimelightTXSetpoint  Value of the X angle of the left limelight to get the right limelight in view
     */
    public DualLimelightManagerSubsystem(String leftLimelightName, String rightLimelightName,
            double leftLimelightTXSetpoint, double rightLimelightTXSetpoint) {
        m_leftLimelight = new LimelightSubsystem(leftLimelightName);
        m_rightLimelight = new LimelightSubsystem(rightLimelightName);

        m_primaryLimelight = m_leftLimelight;
        m_secondaryLimelight = m_rightLimelight;

        m_leftLimelightTXSetpoint = leftLimelightTXSetpoint;
        m_rightLimelightTXSetpoint = rightLimelightTXSetpoint;

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

    public void setPrimary(LIMELIGHT limelight) {
        switch (limelight) {
            case LEFT:
                m_primaryLimelight = m_leftLimelight;
                m_secondaryLimelight = m_rightLimelight;
                m_primaryLimelightTXSetpoint = m_leftLimelightTXSetpoint;
                break;
            case RIGHT:
                m_primaryLimelight = m_rightLimelight;
                m_secondaryLimelight = m_leftLimelight;
                m_primaryLimelightTXSetpoint = m_rightLimelightTXSetpoint;
                break;
        }
    }

    public LIMELIGHT getPrimaryLimelight() {
        if (m_primaryLimelight == m_rightLimelight) {
            return LIMELIGHT.RIGHT;
        } else if (m_primaryLimelight == m_leftLimelight) {
            return LIMELIGHT.LEFT;
        }
        return null;
    }

    public void setTargetAprilTag(int id) {
        m_targetAprilTag = id;
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

    /**
     * @return The tX angle setpoint on the opposing camera to get the primary camera in view
     */
    public double getPrimaryTXSetpoint() {
        return m_primaryLimelightTXSetpoint;
    }

    public boolean validTargetExists() {
        if(m_targetAprilTag == -1) {
            return m_leftLimelight.validTargetExists() || m_rightLimelight.validTargetExists();
        }
        return m_leftLimelight.validAprilTagTargetExists(m_targetAprilTag) || m_rightLimelight.validAprilTagTargetExists(m_targetAprilTag);
    }

    public boolean validTargetExistsOnPrimary() {
        if(m_targetAprilTag == -1) {
            return m_primaryLimelight.validTargetExists();
        }
        return m_primaryLimelight.validAprilTagTargetExists(m_targetAprilTag);
    }

    public Pose2d getLimelightPose2d() {
        if (m_leftLimelight.validTargetExists() && m_rightLimelight.validTargetExists()) {
            Pose2d leftPose = m_leftLimelight.getLimelightPose2d();
            Pose2d rightPose = m_rightLimelight.getLimelightPose2d();

            double xLocation = (leftPose.getX() + rightPose.getX()) / 2;
            double yLocation = (leftPose.getY() + rightPose.getY()) / 2;
            double rotationDegrees = (leftPose.getRotation().getDegrees() + rightPose.getRotation().getDegrees()) / 2;

            return new Pose2d(xLocation, yLocation, Rotation2d.fromDegrees(rotationDegrees));
        } else if (m_leftLimelight.validTargetExists()) {
            return m_leftLimelight.getLimelightPose2d();
        } else if (m_rightLimelight.validTargetExists()) {
            return m_rightLimelight.getLimelightPose2d();
        } else {
            return null;
        }
    }

    public double getTY() {
        if (m_primaryLimelight.validTargetExists()) {
            return m_primaryLimelight.getTY();
        } else if (m_secondaryLimelight.validTargetExists()) {
            return m_secondaryLimelight.getTY();
        } else {
            return Double.NaN;
        }
    }

    public double getTX() {
        if (m_primaryLimelight.validTargetExists()) {
            return m_primaryLimelight.getTX();
        } else if (m_secondaryLimelight.validTargetExists()) {
            return m_secondaryLimelight.getTX();
        } else {
            return Double.NaN;
        }
    }

    public double getPrimaryTX() {
        return m_primaryLimelight.getTX();
    }

    public double getSecondaryTX() {
        return m_secondaryLimelight.getTX();
    }
}

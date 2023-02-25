package com.team2357.frc2023.subsystems;

import com.team2357.lib.subsystems.LimelightSubsystem;

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

    // Assumes limelights are configured so that the crosshair is the middle of the robot on both limelights
    // This will allow switching 
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
        m_leftLimelight.setAprilTagPipelineActive();
    }
}

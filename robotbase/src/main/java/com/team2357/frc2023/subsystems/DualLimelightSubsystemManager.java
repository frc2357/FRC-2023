package com.team2357.frc2023.subsystems;

import com.team2357.lib.subsystems.LimelightSubsystem;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

// Manages two limelight subsystems
public class DualLimelightSubsystemManager {
    public enum LIMELIGHT {
        RIGHT,
        LEFT
    };

    private LimelightSubsystem m_leftLimelight;
    private LimelightSubsystem m_rightLimelight;

    public DualLimelightSubsystemManager(String leftLimelightName, String rightLimelightName) {
        m_leftLimelight = new LimelightSubsystem(leftLimelightName);
        m_rightLimelight = new LimelightSubsystem(rightLimelightName);
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
}

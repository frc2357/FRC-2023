package com.team2357.frc2023.apriltag;

import com.team2357.frc2023.networktables.GridCam.GRID_CAM;

import edu.wpi.first.math.geometry.Pose2d;

public class AprilTagEstimate {
    private int m_id;
    private double m_timeStamp;
    private Pose2d m_pose;

    private GRID_CAM m_gridCam;

    private double m_ambiguity;

    boolean m_isFieldRelative;

    public AprilTagEstimate(int id, double timeStamp, Pose2d pose, double ambiguity, GRID_CAM gridCam, boolean isFieldRelative) {
        m_id = id;
        m_timeStamp = timeStamp;
        m_pose = pose;
        m_ambiguity = ambiguity;
        m_isFieldRelative = isFieldRelative;
        m_gridCam = gridCam;
    }

    public int getId() {
        return m_id;
    }

    public void setId(int id) {
        m_id = id;
    }

    public double getTimeStamp() {
        return m_timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        m_timeStamp = timeStamp;
    }

    public Pose2d getPose() {
        return m_pose;
    }

    public void setPose(Pose2d estimate) {
        m_pose = estimate;
    }

    public double getAmbiguity() {
        return m_ambiguity;
    }

    public void setAmbiguity(double ambiguity) {
        m_ambiguity = ambiguity;
    }

    public GRID_CAM getGridCam() {
        return m_gridCam;
    }

    public void setGridCam(GRID_CAM gridCam) {
        m_gridCam = gridCam;
    }

    public boolean getIsFieldRelative() {
        return m_isFieldRelative;
    }

    public void setIsFieldRelative(boolean isFieldRelative) {
        m_isFieldRelative = isFieldRelative;
    }

}

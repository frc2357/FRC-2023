package com.team2357.frc2023.apriltag;

import com.team2357.frc2023.networktables.GridCam.GRID_CAM;

import edu.wpi.first.math.geometry.Pose2d;

public class AprilTagEstimate {
    private int m_id;
    private long m_timeStamp;
    private Pose2d m_pose;

    private GRID_CAM m_gridCam;

    private double m_ambiguity;

    boolean m_isFieldRelative;

    public AprilTagEstimate(int id, long timeStamp, Pose2d pose, double ambiguity, GRID_CAM gridCam, boolean isFieldRelative) {
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

    public long getTimeStamp() {
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

    public double getM_ambiguity() {
        return m_ambiguity;
    }

    public void setM_ambiguity(double ambiguity) {
        m_ambiguity = ambiguity;
    }

    public GRID_CAM getgridCam() {
        return m_gridCam;
    }

    public void setgridCam(GRID_CAM gridCam) {
        m_gridCam = gridCam;
    }

    public boolean getIsFieldRelative() {
        return m_isFieldRelative;
    }

    public void setIsFieldRelative(boolean isFieldRelative) {
        m_isFieldRelative = isFieldRelative;
    }

}

package com.team2357.frc2023.apriltag;

import java.io.IOException;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;

public class GridCamEstimator {
    public static GridCamEstimator m_instance;
    private AprilTagFieldLayout m_field;

    public GridCamEstimator getInstance() {
        if(m_instance == null) {
            new GridCamEstimator();
        }
        return m_instance;

        try {
        m_field = new AprilTagFieldLayout(AprilTagFields.k2023ChargedUp.m_resourceFile);
        } catch (IOException e) {
            Logger.getInstance().recordOutput("APRIL TAG FIELD FAILED TO LOAD", true);
        }
    }

    private GridCamEstimator() {
        m_instance = this;
    }

    public estimate()
}

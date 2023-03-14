package com.team2357.frc2023.apriltag;

import java.io.IOException;
import java.util.ArrayList;

import org.littletonrobotics.junction.Logger;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.networktables.GridCam;
import com.team2357.frc2023.networktables.GridCam.GRID_CAM;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;

public class GridCamEstimator {
    public static GridCamEstimator m_instance;
    private AprilTagFieldLayout m_field;

    public static GridCamEstimator getInstance() {
        if (m_instance == null) {
            new GridCamEstimator();
        }
        return m_instance;
    }

    private GridCamEstimator() {
        m_instance = this;

        try {
            m_field = new AprilTagFieldLayout(AprilTagFields.k2023ChargedUp.m_resourceFile);
        } catch (IOException e) {
            Logger.getInstance().recordOutput("APRIL TAG FIELD FAILED TO LOAD", true);
        }
    }

    public AprilTagEstimate estimateRobotPose(ArrayList<AprilTagEstimate> estimates) {
        if(estimates == null) {
            return null;
        }
        
        AprilTagEstimate bestEstimate = findBestEstimate(estimates);
        return transformEstimateToField(bestEstimate);
    }

    public AprilTagEstimate findBestEstimate(ArrayList<AprilTagEstimate> estimates) {
        AprilTagEstimate bestEstimate = estimates.get(0);

        for (int i = 1; i < estimates.size(); i++) {
            AprilTagEstimate estimate = estimates.get(i);

            if(estimate.getAmbiguity() > bestEstimate.getAmbiguity()) {
                bestEstimate = estimate;
            }
        }
        return bestEstimate;
    }

    public AprilTagEstimate transformEstimateToField(AprilTagEstimate estimate) {

        Pose2d pose = estimate.getPose();

        Pose2d fieldPose = m_field.getTagPose(estimate.getId()).get().toPose2d();
        switch(estimate.getGridCam()) {
            case FRONT: 
                pose.relativeTo((Constants.GRIDCAM.FRONT_CAM_POSE)).relativeTo(fieldPose);
                break;
            case REAR:
                pose.relativeTo((Constants.GRIDCAM.REAR_CAM_POSE)).relativeTo(fieldPose);
                break;
            default:
                return null;
        }

        return new AprilTagEstimate(estimate.getId(), estimate.getTimeStamp(), pose, estimate.getAmbiguity(), GRID_CAM.NONE, true);
    }
}

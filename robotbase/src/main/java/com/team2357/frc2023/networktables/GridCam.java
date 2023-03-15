package com.team2357.frc2023.networktables;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.apriltag.AprilTagEstimate;
import com.team2357.frc2023.apriltag.GridCamEstimator;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringSubscriber;

public class GridCam {
    private static GridCam m_instance;

    private int m_poseListenerHandle;

    public static GridCam getInstance() {
        if (m_instance == null) {
            m_instance = new GridCam();
        }
        return m_instance;
    }

    public enum GRID_CAM {
        FRONT,
        REAR,
        NONE
    }

    private NetworkTable m_table = NetworkTableInstance.getDefault()
            .getTable(Constants.APRILTAG_POSE.APRILTAG_TABLE_NAME);

    private StringSubscriber m_poseSub;

    private JSONParser m_parser = new JSONParser();

    public final Consumer<NetworkTableEvent> m_onValueChange = (NetworkTableEvent event) -> {
        String jsonPoses = event.valueData.value.getString();

        ArrayList<AprilTagEstimate> estimates;
        try {
            estimates = toEstimates(jsonPoses);
        } catch (NullPointerException e) {
            return;
        }
        AprilTagEstimate robotEstimate = GridCamEstimator.getInstance().estimateRobotPose(estimates);
        // SwerveDriveSubsystem.getInstance().addVisionPoseEstimate(robotEstimate);
    };

    private GridCam() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();

        m_poseSub = m_table.getStringTopic(Constants.APRILTAG_POSE.POSE_TOPIC_NAME).subscribe("");

        m_instance = this;

        m_poseListenerHandle = inst.addListener(
                m_poseSub,
                EnumSet.of(NetworkTableEvent.Kind.kValueAll),
                m_onValueChange);
    }

    /**
     * 
     * @return List of estimates at a given time relative to the camera
     */
    public ArrayList<AprilTagEstimate> getCamRelativePoses() {
        try {
            // System.out.println(m_poseSub.get());
            return toEstimates(m_poseSub.get());
        } catch (NullPointerException e) {
            return null;
        }
    }

    public ArrayList<AprilTagEstimate> toEstimates(String jsonString) throws NullPointerException {
        JSONObject obj;
        try {
            obj = (JSONObject) m_parser.parse(jsonString);
        } catch (ParseException e) {
            return null;
        }

        System.out.println(obj);

        double timestamp = (double) obj.get("timestamp");
        JSONArray jsonTagPoses = (JSONArray) obj.get("tags");

        ArrayList<AprilTagEstimate> aprilTagEstimates = new ArrayList<AprilTagEstimate>();

        for (int i = 0; i < jsonTagPoses.size(); i++) {
            JSONObject jsonTagInfo = (JSONObject) jsonTagPoses.get(i);

            JSONObject jsonTagPose = (JSONObject) jsonTagInfo.get("pose");

            Translation2d poseTrans = getTranslation(jsonTagPose);
            Rotation2d poseRot = getRotation(jsonTagPose);

            Pose2d poseEstimate = new Pose2d(poseTrans, poseRot);

            int id = (int) jsonTagInfo.get("ID");
            double ambiguity = (double) jsonTagInfo.get("ambiguity");

            GRID_CAM gridCam = getGridCam((int) jsonTagInfo.get("camera"));

            if (gridCam != GRID_CAM.NONE) {
                AprilTagEstimate fullEstimate = new AprilTagEstimate(id, timestamp, poseEstimate, ambiguity, gridCam,
                        false);
                aprilTagEstimates.add(fullEstimate);
            }
        }
        return aprilTagEstimates;
    }

    public GRID_CAM getGridCam(int cam) {
        switch (cam) {
            case 0:
                return GRID_CAM.FRONT;
            case 1:
                return GRID_CAM.REAR;
            default:
                return GRID_CAM.NONE;
        }
    }

    public Translation2d getTranslation(JSONObject obj) {
        JSONObject translation;

        try {
            translation = (JSONObject) obj.get("translation");

            double translationX = (double) translation.get("x");
            double translationY = (double) translation.get("y");

            return new Translation2d(translationX, translationY);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Rotation2d getRotation(JSONObject obj) throws NullPointerException {
        JSONObject quaternion;

        try {
            quaternion = (JSONObject) ((JSONObject) obj.get("rotation")).get("quaternion");

            double w = (double) quaternion.get("W");
            double x = (double) quaternion.get("X");
            double y = (double) quaternion.get("Y");
            double z = (double) quaternion.get("Z");

            double rotation = Math.atan2(2 * (w * z + x * y), 1 - 2 * (Math.pow(y, 2) + Math.pow(z, 2)));
            rotation += rotation < 0 ? 2 * Math.PI : 0;

            return Rotation2d.fromRadians(rotation);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void close() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        inst.removeListener(m_poseListenerHandle);
        m_poseSub.close();
    }
}

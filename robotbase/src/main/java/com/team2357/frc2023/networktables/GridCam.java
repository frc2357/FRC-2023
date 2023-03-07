package com.team2357.frc2023.networktables;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.team2357.frc2023.Constants;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringSubscriber;

public class GridCam {
    private static GridCam m_instance;

    public static GridCam getInstance() {
        if (m_instance == null) {
            m_instance = new GridCam();
        }
        return m_instance;
    }

    private NetworkTable m_table = NetworkTableInstance.getDefault()
            .getTable(Constants.APRILTAG_POSE.APRILTAG_TABLE_NAME);

    private StringSubscriber m_poseSub;

    private JSONParser m_parser = new JSONParser();

    private GridCam() {
        m_poseSub = m_table.getStringTopic(Constants.APRILTAG_POSE.POSE_TOPIC_NAME).subscribe("");

        m_instance = this;
    }

    /**
     * 
     * @return List of poses at a given time relative to the camera
     */
    public Pose2d getCamRelativePose() {
        return toPose2d(m_poseSub.get());
    }

    /**
     * 
     * @return List of poses at a given time relative to the robot
     */
    public Pose2d getRobotRelativePose() {
        return getCamRelativePose();
    }

    public Pose2d getFieldRelativePoses() {
        return getRobotRelativePose();
    }

    public Pose2d toPose2d(String jsonString) {
        JSONObject obj;
        try {
            obj = (JSONObject) m_parser.parse(jsonString);
        } catch (ParseException e) {
            return null;
        }

        System.out.println(obj);
        Pose2d pose = new Pose2d(getTranslation(obj, 0), getRotation(obj, 0));

        return pose;
    }

    public Translation2d getTranslation(JSONObject obj, int tagIdx) {
        JSONObject translation;
        try {
            translation = (JSONObject) ((JSONObject) ((JSONObject) ((JSONArray) (obj.get("tags"))).get(tagIdx))
                    .get("pose")).get("translation");
        } catch (NullPointerException e) {
            return new Translation2d(Double.NaN, Double.NaN);
        }

        double translationX = (double) translation.get("x");
        double translationY = (double) translation.get("y");

        return new Translation2d(translationX, translationY);
    }

    public Rotation2d getRotation(JSONObject obj, int tagIdx) {
        JSONObject quaternion;
        try {
            quaternion = (JSONObject) ((JSONObject) ((JSONObject) ((JSONObject) ((JSONArray) (obj.get("tags"))).get(tagIdx)).get("pose")).get("rotation")).get("quaternion");
        } catch (NullPointerException e) {
            return Rotation2d.fromRadians(Double.NaN);
        }
        double w = (double) quaternion.get("W");
        double x = (double) quaternion.get("X");
        double y = (double) quaternion.get("Y");
        double z = (double) quaternion.get("Z");

        double rotation = Math.atan2(2 * (w*z + x*y), 1 - 2*(Math.pow(y, 2) + Math.pow(z, 2)));
        rotation += rotation < 0 ? 2 * Math.PI : 0;

        return Rotation2d.fromRadians(rotation);
    }

    public void close() {
        m_poseSub.close();
    }
}

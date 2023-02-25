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

public class AprilTagPose {
    private static AprilTagPose m_instance;

    public static AprilTagPose getInstance() {
        if (m_instance == null) {
            m_instance = new AprilTagPose();
        }
        return m_instance;
    }

    private NetworkTable m_table = NetworkTableInstance.getDefault().getTable(Constants.APRILTAG_POSE.APRILTAG_TABLE_NAME);

    private StringSubscriber m_poseSub;

    private JSONParser m_parser = new JSONParser();

    private AprilTagPose() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();

        m_poseSub = m_table.getStringTopic(Constants.APRILTAG_POSE.POSE_TOPIC_NAME).subscribe("");

        m_instance = this;
    }

    public Pose2d getPose() {
        return toPose2d(m_poseSub.get());
    }

    public Pose2d toPose2d(String jsonString) {
        JSONObject obj;
        try {
            obj = (JSONObject) m_parser.parse(jsonString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        Pose2d pose = new Pose2d(getTranslation(obj, 0), getRotation(obj, 0));

        return pose;
    }

    public Translation2d getTranslation(JSONObject obj, int tagIdx) {
        double translationX, translationY;
        JSONObject translation = (JSONObject) ((JSONObject) ((JSONObject) ((JSONArray) (obj.get("tags"))).get(tagIdx))
                .get("pose")).get("translation");
        translationX = (double) translation.get("x");
        translationY = (double) translation.get("y");

        return new Translation2d(translationX, translationY);
    }

    public Rotation2d getRotation(JSONObject obj, int tagIdx) {
        JSONObject quaternion = (JSONObject) ((JSONObject) ((JSONObject) ((JSONObject) ((JSONArray) (obj.get("tags"))).get(tagIdx)).get("pose")).get("rotation")).get("quaternion");
        double w = (double) quaternion.get("W"), x = (double) quaternion.get("X"), y = (double) quaternion.get("Y"), z = (double) quaternion.get("Z");

        double rotation = Math.atan2(2 * (w*z + x*y), 1 - 2*(Math.pow(y, 2) + Math.pow(z, 2)));
        rotation += rotation < 0 ? 2 * Math.PI : 0;

        return Rotation2d.fromRadians(rotation);
    }

    public void close() {
        m_poseSub.close();
    }
}

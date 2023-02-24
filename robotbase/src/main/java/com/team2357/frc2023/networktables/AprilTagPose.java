package com.team2357.frc2023.networktables;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.wpi.first.math.geometry.Pose2d;
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

    private NetworkTable m_table = NetworkTableInstance.getDefault().getTable("apriltag");

    private StringSubscriber m_poseSub;

    private JSONParser m_parser = new JSONParser();

    private AprilTagPose() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();

        m_poseSub = m_table.getStringTopic("pose").subscribe("");

        m_instance = this;
    }

    public Pose2d getPose() {
        return toPose2d(m_poseSub.get());
    }

    public Pose2d toPose2d(String jsonString) {
        try {
            JSONObject obj = (JSONObject) m_parser.parse(jsonString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Pose2d pose = new Pose2d();

        return pose;
    }

    public void close() {
        m_poseSub.close();
    }
}

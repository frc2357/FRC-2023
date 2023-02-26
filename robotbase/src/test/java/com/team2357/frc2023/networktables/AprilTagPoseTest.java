package com.team2357.frc2023.networktables;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.wpi.first.math.geometry.Translation2d;

public class AprilTagPoseTest {
    JSONParser parser = new JSONParser();

    String testString = "{\"timestamp\":0.0,\"tags\": [{\"ID\": 1,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 15.513558,\"y\": 1.071626,\"z\": 0.462788},\"rotation\": {\"quaternion\": {\"W\": 0.0,\"X\": 0.0,\"Y\": 0.0,\"Z\": 1.0}}}},{\"ID\": 2,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 15.513558,\"y\": 2.748026,\"z\": 0.462788},\"rotation\": {\"quaternion\": {\"W\": 0.7071081,\"X\": 0.0,\"Y\": 0.0,\"Z\": 0.7071081}}}},{\"ID\": 3,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 15.513558,\"y\": 4.424426,\"z\": 0.462788},\"rotation\": {\"quaternion\": {\"W\": 0.9659258,\"X\": 0.0,\"Y\": 0.0,\"Z\": 0.258819}}}},{\"ID\": 4,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 16.178784,\"y\": 6.749796,\"z\": 0.695452},\"rotation\": {\"quaternion\": {\"W\": -0.7071068,\"X\": 0.0,\"Y\": 0.0,\"Z\": 0.7071068}}}},{\"ID\": 5,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 0.36195,\"y\": 6.749796,\"z\": 0.695452},\"rotation\": {\"quaternion\": {\"W\": -0.7986355,\"X\": 0.0,\"Y\": 0.0,\"Z\": 0.601815}}}},{\"ID\": 6,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 1.02743,\"y\": 4.424426,\"z\": 0.462788},\"rotation\": {\"quaternion\": {\"W\": 0.9986295,\"X\": 0.0,\"Y\": 0.0,\"Z\": 0.052336}}}},{\"ID\": 7,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 1.02743,\"y\": 2.748026,\"z\": 0.462788},\"rotation\": {\"quaternion\": {\"W\": 0.4461978,\"X\": 0.0,\"Y\": 0.0,\"Z\": 0.8949344}}}},{\"ID\": 8,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 1.02743,\"y\": 1.071626,\"z\": 0.462788},\"rotation\": {\"quaternion\": {\"W\": -0.9832549,\"X\": 0.0,\"Y\": 0.0,\"Z\": 0.1822355}}}}]}";
    JSONObject testObj = parseObject(testString);
    String emptyString = "";
    JSONObject emptyObj = parseObject(emptyString);

    @Test
    public void testGetTranslation() {
        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 0).getX(), 15.513558, 0.0);
        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 0).getY(), 1.071626, 0.0);

        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 1).getX(), 15.513558, 0.0);
        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 1).getY(), 2.748026, 0.0);

        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 2).getX(), 15.513558, 0.0);
        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 2).getY(), 4.424426, 0.0);

        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 3).getX(), 16.178784, 0.0);
        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 3).getY(), 6.749796, 0.0);

        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 4).getX(), 0.36195, 0.0);
        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 4).getY(), 6.749796, 0.0);

        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 5).getX(), 1.02743, 0.0);
        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 5).getY(), 4.424426, 0.0);

        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 6).getX(), 1.02743, 0.0);
        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 6).getY(), 2.748026, 0.0);

        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 7).getX(), 1.02743, 0.0);
        assertEquals(AprilTagPose.getInstance().getTranslation(testObj, 7).getY(), 1.071626, 0.0);
    }

    @Test
    public void testGetRotation() {
        assertEquals(AprilTagPose.getInstance().getRotation(testObj, 0).getDegrees(), 180, 0.1);

        assertEquals(AprilTagPose.getInstance().getRotation(testObj, 1).getDegrees(), 90, 0.1);

        assertEquals(AprilTagPose.getInstance().getRotation(testObj, 2).getDegrees(), 30, 0.01);

        assertEquals(AprilTagPose.getInstance().getRotation(testObj, 3).getDegrees(), 270, 0.01);

        assertEquals(AprilTagPose.getInstance().getRotation(testObj, 4).getDegrees(), 286, 0.01);

        assertEquals(AprilTagPose.getInstance().getRotation(testObj, 5).getDegrees(), 6, 0.01);

        assertEquals(AprilTagPose.getInstance().getRotation(testObj, 6).getDegrees(), 127, 0.01);
        
        assertEquals(AprilTagPose.getInstance().getRotation(testObj, 7).getDegrees(), 339, 0.01);
    }

    @Test
    public void testEmptyJson() {
        assertEquals(AprilTagPose.getInstance().getTranslation(emptyObj, 0).getX(), Double.NaN);
        assertEquals(AprilTagPose.getInstance().getTranslation(emptyObj, 0).getY(), Double.NaN);
        assertEquals(AprilTagPose.getInstance().getRotation(emptyObj, 0).getDegrees(), Double.NaN);
    }

    private JSONObject parseObject(String str) {
        JSONObject obj;
        try {
            obj = (JSONObject) parser.parse(str);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            return new JSONObject();
        }
        return obj;
    }
}

package com.team2357.frc2023.networktables;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

public class AprilTagPoseTest {
    JSONParser parser = new JSONParser();

    String testString = "{\"timestamp\":0.0,\"tags\": [{\"ID\": 1,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 15.513558,\"y\": 1.071626,\"z\": 0.462788},\"rotation\": {\"quaternion\": {\"W\": 0.0,\"X\": 0.0,\"Y\": 0.0,\"Z\": 1.0}}}},{\"ID\": 2,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 15.513558,\"y\": 2.748026,\"z\": 0.462788},\"rotation\": {\"quaternion\": {\"W\": 0.7071081,\"X\": 0.0,\"Y\": 0.0,\"Z\": 0.7071081}}}},{\"ID\": 3,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 15.513558,\"y\": 4.424426,\"z\": 0.462788},\"rotation\": {\"quaternion\": {\"W\": 0.9659258,\"X\": 0.0,\"Y\": 0.0,\"Z\": 0.258819}}}},{\"ID\": 4,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 16.178784,\"y\": 6.749796,\"z\": 0.695452},\"rotation\": {\"quaternion\": {\"W\": -0.7071068,\"X\": 0.0,\"Y\": 0.0,\"Z\": 0.7071068}}}},{\"ID\": 5,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 0.36195,\"y\": 6.749796,\"z\": 0.695452},\"rotation\": {\"quaternion\": {\"W\": -0.7986355,\"X\": 0.0,\"Y\": 0.0,\"Z\": 0.601815}}}},{\"ID\": 6,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 1.02743,\"y\": 4.424426,\"z\": 0.462788},\"rotation\": {\"quaternion\": {\"W\": 0.9986295,\"X\": 0.0,\"Y\": 0.0,\"Z\": 0.052336}}}},{\"ID\": 7,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 1.02743,\"y\": 2.748026,\"z\": 0.462788},\"rotation\": {\"quaternion\": {\"W\": 0.4461978,\"X\": 0.0,\"Y\": 0.0,\"Z\": 0.8949344}}}},{\"ID\": 8,\"ambiguity\":1.0,\"pose\": {\"translation\": {\"x\": 1.02743,\"y\": 1.071626,\"z\": 0.462788},\"rotation\": {\"quaternion\": {\"W\": -0.9832549,\"X\": 0.0,\"Y\": 0.0,\"Z\": 0.1822355}}}}]}";
    JSONObject testObj = parseObject(testString);

    JSONArray testTags = (JSONArray) testObj.get("tags");
        
    JSONObject pose0 = (JSONObject) ((JSONObject) testTags.get(0)).get("pose");
    JSONObject pose1 = (JSONObject) ((JSONObject) testTags.get(1)).get("pose");
    JSONObject pose2 = (JSONObject) ((JSONObject) testTags.get(2)).get("pose");
    JSONObject pose3 = (JSONObject) ((JSONObject) testTags.get(3)).get("pose");
    JSONObject pose4 = (JSONObject) ((JSONObject) testTags.get(4)).get("pose");
    JSONObject pose5 = (JSONObject) ((JSONObject) testTags.get(5)).get("pose");
    JSONObject pose6 = (JSONObject) ((JSONObject) testTags.get(6)).get("pose");
    JSONObject pose7 = (JSONObject) ((JSONObject) testTags.get(7)).get("pose");

    JSONObject emptyPose = parseObject("");

    @Test
    public void testGetTranslation() {
        

        assertEquals(GridCam.getInstance().getTranslation(pose0).getX(), 15.513558, 0.0);
        assertEquals(GridCam.getInstance().getTranslation(pose0).getY(), 1.071626, 0.0);

        assertEquals(GridCam.getInstance().getTranslation(pose1).getX(), 15.513558, 0.0);
        assertEquals(GridCam.getInstance().getTranslation(pose1).getY(), 2.748026, 0.0);

        assertEquals(GridCam.getInstance().getTranslation(pose2).getX(), 15.513558, 0.0);
        assertEquals(GridCam.getInstance().getTranslation(pose2).getY(), 4.424426, 0.0);

        assertEquals(GridCam.getInstance().getTranslation(pose3).getX(), 16.178784, 0.0);
        assertEquals(GridCam.getInstance().getTranslation(pose3).getY(), 6.749796, 0.0);

        assertEquals(GridCam.getInstance().getTranslation(pose4).getX(), 0.36195, 0.0);
        assertEquals(GridCam.getInstance().getTranslation(pose4).getY(), 6.749796, 0.0);

        assertEquals(GridCam.getInstance().getTranslation(pose5).getX(), 1.02743, 0.0);
        assertEquals(GridCam.getInstance().getTranslation(pose5).getY(), 4.424426, 0.0);

        assertEquals(GridCam.getInstance().getTranslation(pose6).getX(), 1.02743, 0.0);
        assertEquals(GridCam.getInstance().getTranslation(pose6).getY(), 2.748026, 0.0);

        assertEquals(GridCam.getInstance().getTranslation(pose7).getX(), 1.02743, 0.0);
        assertEquals(GridCam.getInstance().getTranslation(pose7).getY(), 1.071626, 0.0);
    }

    @Test
    public void testGetRotation() {
        assertEquals(GridCam.getInstance().getRotation(pose0).getDegrees(), 180, 0.1);

        assertEquals(GridCam.getInstance().getRotation(pose1).getDegrees(), 90, 0.1);

        assertEquals(GridCam.getInstance().getRotation(pose2).getDegrees(), 30, 0.01);

        assertEquals(GridCam.getInstance().getRotation(pose3).getDegrees(), 270, 0.01);

        assertEquals(GridCam.getInstance().getRotation(pose4).getDegrees(), 286, 0.01);

        assertEquals(GridCam.getInstance().getRotation(pose5).getDegrees(), 6, 0.01);

        assertEquals(GridCam.getInstance().getRotation(pose6).getDegrees(), 127, 0.01);
        
        assertEquals(GridCam.getInstance().getRotation(pose7).getDegrees(), 339, 0.01);
    }

    @Test
    public void testEmptyJson() {
        assertEquals(GridCam.getInstance().getTranslation(emptyPose), null);
        assertEquals(GridCam.getInstance().getRotation(emptyPose), null);
    }

    private JSONObject parseObject(String str) {
        JSONObject obj;
        try {
            obj = (JSONObject) parser.parse(str);
        } catch (ParseException e) {
            return new JSONObject();
        }
        return obj;
    }
}

package com.team2357.frc2023.trajectoryutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

@TestInstance(Lifecycle.PER_CLASS)
public class AvailableTeleopTrajectoriesTest {

    @BeforeAll
    public void setup() {
        AvailableTeleopTrajectories.generateTrajectories();
    }

    @Test
    public void testAutoMapping() {

        Pose2d testPose = new Pose2d(3.5, 4.5, Rotation2d.fromDegrees(0));
        assertEquals(AvailableTeleopTrajectories.getStartTrajectoryKey(testPose), 4.5, 0);
    }
    
}

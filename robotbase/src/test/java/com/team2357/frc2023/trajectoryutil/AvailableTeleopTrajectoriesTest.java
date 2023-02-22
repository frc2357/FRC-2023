package com.team2357.frc2023.trajectoryutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.team2357.frc2023.subsystems.SubsystemFactory;
import com.team2357.frc2023.util.DriverStationAllianceGetter;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

@TestInstance(Lifecycle.PER_CLASS)
public class AvailableTeleopTrajectoriesTest {

    @BeforeAll
    public void setup() {
        SubsystemFactory factory = new SubsystemFactory();
        factory.CreateSwerveDriveSubsystem();
        try (MockedStatic<DriverStationAllianceGetter> allianceGetter = Mockito.mockStatic(DriverStationAllianceGetter.class)) {
            allianceGetter.when(DriverStationAllianceGetter::getAlliance).thenReturn(Alliance.Blue);
            AvailableTeleopTrajectories.generateTrajectories();
        }
    }

    @Test
    public void testAutoMapping() {


        // exact values
        Pose2d testPose = new Pose2d(3.5, 4.5, Rotation2d.fromDegrees(0));
        assertEquals(AvailableTeleopTrajectories.getTrajectoryKey(testPose), 4.5, 0.001);

        testPose = new Pose2d(3.5, 4.65, Rotation2d.fromDegrees(0));
        assertEquals(AvailableTeleopTrajectories.getTrajectoryKey(testPose), 4.65, 0.001);

        testPose = new Pose2d(3.5, 4.85, Rotation2d.fromDegrees(0));
        assertEquals(AvailableTeleopTrajectories.getTrajectoryKey(testPose), 4.85, 0.001);

        // Undershoot tests
        testPose = new Pose2d(3.5, 4.5-0.06, Rotation2d.fromDegrees(0));
        assertEquals(AvailableTeleopTrajectories.getTrajectoryKey(testPose), 4.5, 0.001);

        testPose = new Pose2d(3.5, 4.65-0.07, Rotation2d.fromDegrees(0));
        assertEquals(AvailableTeleopTrajectories.getTrajectoryKey(testPose), 4.65, 0.001);

        testPose = new Pose2d(3.5, 4.85-0.09, Rotation2d.fromDegrees(0));
        assertEquals(AvailableTeleopTrajectories.getTrajectoryKey(testPose), 4.85, 0.001);

        // Overshoot tests
        testPose = new Pose2d(3.5, 4.5+0.06, Rotation2d.fromDegrees(0));
        assertEquals(AvailableTeleopTrajectories.getTrajectoryKey(testPose), 4.5, 0.001);

        testPose = new Pose2d(3.5, 4.65+0.07, Rotation2d.fromDegrees(0));
        assertEquals(AvailableTeleopTrajectories.getTrajectoryKey(testPose), 4.65, 0.001);

        testPose = new Pose2d(3.5, 4.85+0.09, Rotation2d.fromDegrees(0));
        assertEquals(AvailableTeleopTrajectories.getTrajectoryKey(testPose), 4.85, 0.001);
}
    
}

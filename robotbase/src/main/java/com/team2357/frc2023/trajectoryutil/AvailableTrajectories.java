package com.team2357.frc2023.trajectoryutil;

import java.util.HashMap;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AvailableTrajectories {
    public static HashMap<PATH_POINTS, Command> trajectoryMap = new HashMap<>();

    // public static SequentialCommandGroup figure8Trajectory;
    // public static SequentialCommandGroup lineTrajectory;
    // public static SequentialCommandGroup exampleTwoPointTrajectory;
    public static SequentialCommandGroup node_1_to_stage_1;
    public static SequentialCommandGroup node_3_to_charge;
    public static SequentialCommandGroup node_4_to_stage_2;
    public static SequentialCommandGroup node_6_to_charge;
    public static SequentialCommandGroup node_7_to_charge;
    public static SequentialCommandGroup node_9_to_stage_4;
    public static SequentialCommandGroup stage_1_to_node_3;
    public static SequentialCommandGroup stage_2_to_node_6;
    public static SequentialCommandGroup stage_4_to_node_7;


    public enum PATH_POINTS {
        NONE,
        NODE_1,
        NODE_3,
        NODE_4,
        NODE_6,
        NODE_7,
        NODE_9,
        STAGE_1,
        STAGE_2,
        STAGE_3,
        STAGE_4,
        CHARGE_STATION
    }

    public static void generateTrajectories() {
        // figure8Trajectory = createFigure8Trajectory();
        // lineTrajectory = createLineTrajectory();
        // exampleTwoPointTrajectory = createExampleTwoPointTrajectory();

        node_1_to_stage_1 = createNode1ToStage1();
        node_3_to_charge = createNode3ToCharge();
        node_4_to_stage_2 = createNode4ToStage2();
        node_6_to_charge = createNode6ToCharge();
        node_7_to_charge = createNode7ToCharge();
        node_9_to_stage_4 = createNode9ToStage4();
        stage_1_to_node_3 = createStage1ToNode3();
        stage_2_to_node_6 = createStage2ToNode6();
        stage_4_to_node_7 = createStage4ToNode7();
    }

    public static void createTrajectoryMap() {

        //TODO: generate trajectories
        trajectoryMap.put(PATH_POINTS.NODE_1, node_1_to_stage_1);
        trajectoryMap.put(PATH_POINTS.NODE_3, node_3_to_charge);
        trajectoryMap.put(PATH_POINTS.NODE_4, node_4_to_stage_2);
        trajectoryMap.put(PATH_POINTS.NODE_6, node_6_to_charge);
        trajectoryMap.put(PATH_POINTS.NODE_7, node_7_to_charge);
        trajectoryMap.put(PATH_POINTS.NODE_9, node_9_to_stage_4);
        trajectoryMap.put(PATH_POINTS.STAGE_1, stage_1_to_node_3);
        trajectoryMap.put(PATH_POINTS.STAGE_2, stage_2_to_node_6);
        trajectoryMap.put(PATH_POINTS.STAGE_4, stage_4_to_node_7);
    }

    public static Command getTrajectory(PATH_POINTS start, PATH_POINTS end) {
        return trajectoryMap.getOrDefault(start, new InstantCommand(() -> {System.out.println("*****************************************************\ngetTrajectory couldn't find trajectory\n*****************************************************");}));
    }

    // public static SequentialCommandGroup createFigure8Trajectory() {
    //     return TrajectoryUtil.createTrajectoryPathCommand("figure8", true);
    // }

    // public static SequentialCommandGroup createLineTrajectory() {
    //     return TrajectoryUtil.createTrajectoryPathCommand("Normal line", true);
    // }

    // public static SequentialCommandGroup createExampleTwoPointTrajectory() {
    //     return TrajectoryUtil.CreateTwoPointTrajectoryPathCommand(new Pose2d(1, 1, Rotation2d.fromDegrees(0)),
    //             new Pose2d(2, 1, Rotation2d.fromDegrees(90)), true);
    // }

    public static SequentialCommandGroup createNode1ToStage1() {
        return TrajectoryUtil.createTrajectoryPathCommand("blue node1 to stage1", true);
    }

    public static SequentialCommandGroup createNode3ToCharge() {
        return TrajectoryUtil.createTrajectoryPathCommand("blue node3 to charge", true);
    }

    public static SequentialCommandGroup createNode4ToStage2() {
        return TrajectoryUtil.createTrajectoryPathCommand("blue node4 to stage2", true);
    }

    public static SequentialCommandGroup createNode6ToCharge() {
        return TrajectoryUtil.createTrajectoryPathCommand("blue node6 to charge", true);
    }

    public static SequentialCommandGroup createNode7ToCharge() {
        return TrajectoryUtil.createTrajectoryPathCommand("blue node7 to charge", true);
    }

    public static SequentialCommandGroup createNode9ToStage4() {
        return TrajectoryUtil.createTrajectoryPathCommand("blue node9 to stage4", true);
    }

    public static SequentialCommandGroup createStage1ToNode3() {
        return TrajectoryUtil.createTrajectoryPathCommand("blue stage1 to node3", true);
    }

    public static SequentialCommandGroup createStage2ToNode6() {
        return TrajectoryUtil.createTrajectoryPathCommand("blue stage2 to node6", true);
    }

    public static SequentialCommandGroup createStage4ToNode7() {
        return TrajectoryUtil.createTrajectoryPathCommand("blue stage4 to node7", true);
    }
    
}

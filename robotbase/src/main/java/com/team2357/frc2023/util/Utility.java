package com.team2357.frc2023.util;

import org.littletonrobotics.junction.Logger;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.scoring.cone.ConeHighPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeHighScoreCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeLowPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeLowScoreCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeMidPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeMidScoreCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeHighPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeHighScoreCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeLowPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeLowScoreCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeMidPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeMidScoreCommand;
import com.team2357.frc2023.networktables.Buttonboard;
import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.state.RobotState.State;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class Utility {
    /**
     * 
     * @param column The column from 0-8 with 0 being closest to opposing alliance
     * @return The id of the april tag on the grid
     */
    public static int gridColumnToAprilTagID(int column) {
        switch (column / 3) {
            case 0:
                return Buttonboard.getInstance().getAlliance() == Alliance.Blue ? 8 : 1;
            case 1:
                return Buttonboard.getInstance().getAlliance() == Alliance.Blue ? 7 : 2;
            case 2:
                return Buttonboard.getInstance().getAlliance() == Alliance.Blue ? 6 : 3;
            default:
                return -1;
        }
    }

    public static Pose2d gridColumnToTargetPose(int col) {
        if (col == -1) {
            return null;
        }

        if (Buttonboard.getInstance().getAlliance() == DriverStation.Alliance.Red) {
            col = 8 - col;
        }

        double y = Constants.FIELD.COLUMN_ZERO_SCORE_Y_METERS;
        y += (col * Constants.FIELD.GRID_DISTANCE_METERS_BETWEEN_COLUMN);
 
        double x = Constants.FIELD.GRID_SCORE_X_METERS;

        Rotation2d rot = Rotation2d.fromDegrees(Constants.FIELD.GRID_SCORE_ROTATION_DEGREES);

        Pose2d pose = flipPoseForCurrentAlliance(new Pose2d(x, y, rot));

        double trim = Constants.FIELD.GRID_SCORE_Y_TRIM * (Buttonboard.getInstance().getAlliance() == DriverStation.Alliance.Red ? -1 : 1);
        
        Pose2d newPose = new Pose2d(pose.getX(), pose.getY() + trim, pose.getRotation());
        
        return newPose;
    }

    public static Pose2d flipPoseForCurrentAlliance(Pose2d pose) {
        if (Buttonboard.getInstance().getAlliance() == Alliance.Red) {

            Translation2d flippedTranslation = new Translation2d(pose.getX(),
                    Constants.FIELD.FIELD_WIDTH_METERS - pose.getY());

            Rotation2d flippedRotation = pose.getRotation().times(-1);

            return new Pose2d(flippedTranslation, flippedRotation);
        }
        return pose;
    }

    /**
     * From:
     * https://github.com/Mechanical-Advantage/RobotCode2023/blob/main/src/main/java/org/littletonrobotics/frc2023/util/GeomUtil.java
     * Creates a pure translating transform
     *
     * @param x The x component of the translation
     * @param y The y component of the translation
     * @return The resulting transform
     */
    public static Transform2d translationToTransform(double x, double y) {
        return new Transform2d(new Translation2d(x, y), new Rotation2d());
    }
}

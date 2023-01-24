package com.team2357.frc2023.subsystems;

import com.ctre.phoenix.sensors.WPI_Pigeon2;
import com.swervedrivespecialties.swervelib.Mk4iSwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SwerveModule;
import com.team2357.frc2023.Constants;
import com.team2357.lib.subsystems.LimelightSubsystem;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class SubsystemFactory {
        public SwerveDriveSubsystem CreateSwerveDriveSubsystem() {
                ShuffleboardTab tab = Shuffleboard.getTab("Drivetrain");

                WPI_Pigeon2 pigeon = new WPI_Pigeon2(Constants.CAN_ID.PIGEON_ID, Constants.CAN_ID.DRIVE_CANBUS);

                SwerveModule frontLeft = Mk4iSwerveModuleHelper.createFalcon500(
                                tab.getLayout("Front Left Module", BuiltInLayouts.kList)
                                                .withSize(2, 4)
                                                .withPosition(0, 0),
                                Mk4iSwerveModuleHelper.GearRatio.L2,
                                Constants.CAN_ID.FRONT_LEFT_MODULE_DRIVE_MOTOR_ID,
                                Constants.CAN_ID.FRONT_LEFT_MODULE_STEER_MOTOR_ID,
                                Constants.CAN_ID.FRONT_LEFT_MODULE_STEER_ENCODER_ID,
                                // the canbus the module is on
                                Constants.CAN_ID.DRIVE_CANBUS,
                                Constants.DRIVE.FRONT_LEFT_MODULE_STEER_OFFSET);

                SwerveModule frontRight = Mk4iSwerveModuleHelper.createFalcon500(
                                tab.getLayout("Front Right Module", BuiltInLayouts.kList)
                                                .withSize(2, 4)
                                                .withPosition(2, 0),
                                Mk4iSwerveModuleHelper.GearRatio.L2,
                                Constants.CAN_ID.FRONT_RIGHT_MODULE_DRIVE_MOTOR_ID,
                                Constants.CAN_ID.FRONT_RIGHT_MODULE_STEER_MOTOR_ID,
                                Constants.CAN_ID.FRONT_RIGHT_MODULE_STEER_ENCODER_ID,
                                // the canbus the module is on
                                Constants.CAN_ID.DRIVE_CANBUS,
                                Constants.DRIVE.FRONT_RIGHT_MODULE_STEER_OFFSET);

                SwerveModule backLeft = Mk4iSwerveModuleHelper.createFalcon500(
                                tab.getLayout("Back Left Module", BuiltInLayouts.kList)
                                                .withSize(2, 4)
                                                .withPosition(4, 0),
                                Mk4iSwerveModuleHelper.GearRatio.L2,
                                Constants.CAN_ID.BACK_LEFT_MODULE_DRIVE_MOTOR_ID,
                                Constants.CAN_ID.BACK_LEFT_MODULE_STEER_MOTOR_ID,
                                Constants.CAN_ID.BACK_LEFT_MODULE_STEER_ENCODER_ID,
                                // the canbus the module is on
                                Constants.CAN_ID.DRIVE_CANBUS,
                                Constants.DRIVE.BACK_LEFT_MODULE_STEER_OFFSET);

                SwerveModule backRight = Mk4iSwerveModuleHelper.createFalcon500(
                                tab.getLayout("Back Right Module", BuiltInLayouts.kList)
                                                .withSize(2, 4)
                                                .withPosition(6, 0),
                                Mk4iSwerveModuleHelper.GearRatio.L2,
                                Constants.CAN_ID.BACK_RIGHT_MODULE_DRIVE_MOTOR_ID,
                                Constants.CAN_ID.BACK_RIGHT_MODULE_STEER_MOTOR_ID,
                                Constants.CAN_ID.BACK_RIGHT_MODULE_STEER_ENCODER_ID,
                                // the canbus the module is on
                                Constants.CAN_ID.DRIVE_CANBUS,
                                Constants.DRIVE.BACK_RIGHT_MODULE_STEER_OFFSET);

                SwerveDriveSubsystem subsystem = new SwerveDriveSubsystem(pigeon, frontLeft, frontRight, backLeft,
                                backRight);
                subsystem.configure(Constants.DRIVE.GET_SWERVE_DRIVE_CONFIG());

                return subsystem;
        }

    public IntakeSubsystem CreateIntakeSubsystem() {
        IntakeSubsystem subsystem = new IntakeSubsystem();
        subsystem.configure(Constants.INTAKE.GET_INTAKE_CONFIG());
        return subsystem;
    }

    public LimelightSubsystem CreateLimelightSubsystem() {
        LimelightSubsystem subsystem = new LimelightSubsystem();
        return subsystem;
    }
}

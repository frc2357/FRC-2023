// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.team2357.frc2023;

import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import com.team2357.frc2023.subsystems.IntakeSubsystem;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.math.controller.PIDController;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean
 * constants. This class should not be used for any other purpose. All constants
 * should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

    public static final class CAN_ID {
        /**
         * The canbus the swerve modules are on
         * 
         * Use "" or "rio" for rio can bus
         * 
         * Use name of Canivore device to use Canivore
         */
        public static final String DRIVE_CANBUS = "CANivore";

        public static final int PIGEON_ID = 5;

        public static final int FRONT_LEFT_MODULE_DRIVE_MOTOR_ID = 11;
        public static final int FRONT_LEFT_MODULE_STEER_MOTOR_ID = 12;
        public static final int FRONT_LEFT_MODULE_STEER_ENCODER_ID = 19;

        public static final int FRONT_RIGHT_MODULE_DRIVE_MOTOR_ID = 13;
        public static final int FRONT_RIGHT_MODULE_STEER_MOTOR_ID = 14;
        public static final int FRONT_RIGHT_MODULE_STEER_ENCODER_ID = 20;

        public static final int BACK_LEFT_MODULE_DRIVE_MOTOR_ID = 15;
        public static final int BACK_LEFT_MODULE_STEER_MOTOR_ID = 16;
        public static final int BACK_LEFT_MODULE_STEER_ENCODER_ID = 21;

        public static final int BACK_RIGHT_MODULE_DRIVE_MOTOR_ID = 17;
        public static final int BACK_RIGHT_MODULE_STEER_MOTOR_ID = 18;
        public static final int BACK_RIGHT_MODULE_STEER_ENCODER_ID = 22;

        public static final int LEFT_INTAKE_MOTOR = 23;
        public static final int RIGHT_INTAKE_MOTOR = 24;
    }

    public static final class DRIVE {
        public static SwerveDriveSubsystem.Configuration GET_SWERVE_DRIVE_CONFIG() {
            SwerveDriveSubsystem.Configuration config = new SwerveDriveSubsystem.Configuration();

            config.m_trackwidthMeters = .60325;
            config.m_wheelbaseMeters = .62865;

            config.m_maxVoltage = 10.0;
            config.m_maxVelocityMetersPerSecond = 6380.0 / 60.0 *
                    SdsModuleConfigurations.MK4I_L2.getDriveReduction() *
                    SdsModuleConfigurations.MK4I_L2.getWheelDiameter() * Math.PI;

            config.m_maxAngularVelocityRadiansPerSecond = config.m_maxVelocityMetersPerSecond /
                    Math.hypot(config.m_trackwidthMeters / 2.0, config.m_wheelbaseMeters / 2.0);
            config.m_maxAngularAccelerationRadiansPerSecondSquared = config.m_maxAngularVelocityRadiansPerSecond / 3.0;

            config.m_trajectoryMaxVelocityMetersPerSecond = 2;
            config.m_trajectoryMaxAccelerationMetersPerSecond = 3;
            
            config.m_xController = new PIDController(.56122, 0, 0);
            config.m_yController = new PIDController(.56122, 0, 0);
            config.m_thetaController = new PIDController(2.15, 0, 0);
            
            config.m_sensorPositionCoefficient = 2.0 * Math.PI / Constants.DRIVE.TICKS_PER_ROTATION
                    * SdsModuleConfigurations.MK4I_L2.getSteerReduction();

            config.m_translateXMaxSpeedMeters = 0.25;
            config.m_translateMaxSpeedMeters = 0.25;

            config.m_translateXToleranceMeters = 0.05;
            config.m_translateYToleranceMeters = 0.05;

            config.m_translateXController = new PIDController(0.5, 0, 0);
            config.m_translateYController = new PIDController(0.05, 0, 0);

            config.m_openLoopRampRateSeconds = 1;

            return config;
        }

        // CONFIGURE THE FOLLOWING ON EACH CANCODER
        // absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
        // magnetOffsetDegrees = Math.toDegrees(configuration.getOffset());
        // sensorDirection = false;
        // initializationStrategy = bootToAbsValue;

        public static final double FRONT_LEFT_MODULE_STEER_OFFSET = -Math.toRadians(162.7);
        public static final double FRONT_RIGHT_MODULE_STEER_OFFSET = -Math.toRadians(135.08);
        public static final double BACK_LEFT_MODULE_STEER_OFFSET = -Math.toRadians(125);
        public static final double BACK_RIGHT_MODULE_STEER_OFFSET = -Math.toRadians(9.45);
        
        public static final PIDController CHARGE_STATION_BALANCE_ANGLE_CONTROLLER = new PIDController(0.5, 0, 0);
        public static final PIDController CHARGE_STATION_DISTANCE_CONTROLLER = new PIDController(0.5, 0, 0);

        public static final double BALANCE_LEVEL_DEGREES = 2.5;
        public static final double BALANCE_FULL_TILT_DEGREES = 15;
        public static final double BACKWARDS_BALANCING_EXTRA_POWER_MULTIPLIER = 1.35;
        public static final double BALANCE_KP = 0.01;
        public static final double BALANCE_MAX_POWER = 0.4;

        public static final double TICKS_PER_ROTATION = 2048.0;

        public static final double WAIT_FOR_ZERO_TIME_MILLIS = 250;

        public static final double ENCODER_SYNC_ACCURACY_RADIANS = 0.05;

        public static final PIDController ROTATE_TO_TARGET_CONTROLLER = new PIDController(0.1, 0, 0);

        public static final double ROTATE_MAXSPEED_METERS_PER_SECOND = 0.91;
    }

    public static final class INTAKE {
        public static IntakeSubsystem.Configuration GET_INTAKE_CONFIG() {
            IntakeSubsystem.Configuration config = new IntakeSubsystem.Configuration();

            config.m_runPercentOutput = 0.4;
            config.m_reversePercentOutput = -0.7;

            // TODO: Make sure these are correct
            config.m_rightInverted = true;
            config.m_leftInverted = false;

            return config;
        }
    }

    public static final class CONTROLLER {
        public static final int DRIVE_CONTROLLER_PORT = 0;
        public static final int GUNNER_CONTROLLER_PORT = 1;

        public static final double DRIVE_CONTROLLER_DEADBAND = 0.1;
        public static final double GUNNER_CONTROLLER_DEADBAND = 0.1;
    }

    
}

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.team2357.frc2023;

import com.revrobotics.CANSparkMax.IdleMode;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import com.team2357.frc2023.subsystems.ArmExtensionSubsystem;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;
import com.team2357.frc2023.subsystems.ClawSubsystem;
import com.team2357.frc2023.subsystems.ElevatorSubsystem;
import com.team2357.frc2023.subsystems.IntakeArmSubsystem;
import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.frc2023.subsystems.WristSubsystem;

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

        public static final int PNEUMATICS_HUB_ID = 2;

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

        public static final int LEFT_ELEVATOR_MOTOR = 29;
        public static final int RIGHT_ELEVATOR_MOTOR = 30;

        public static final int MASTER_ROTATION_MOTOR = 25;
        public static final int FOLLOWER_ROTATION_MOTOR = 26;

        public static final int ARM_EXTENSION_MOTOR = 27;
    }

    public static final class PH_ID {

        public static final int WRIST_SOLENOID_CHANNEL = 0;
        
        public static final int CLAW_SOLENOID_CHANNEL = 2;

        public static final int INTAKE_SOLENOID_FORWARD_CHANNEL = 4;
        public static final int INTAKE_SOLENOID_REVERSE_CHANNEL = 5;
    }

    public static final class DRIVE {
        public static SwerveDriveSubsystem.Configuration GET_SWERVE_DRIVE_CONFIG() {
            SwerveDriveSubsystem.Configuration config = new SwerveDriveSubsystem.Configuration();

            config.m_trackwidthMeters = 0.50165;
            config.m_wheelbaseMeters = 0.55245;

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

            return config;
        }

        // CONFIGURE THE FOLLOWING ON EACH CANCODER
        // absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
        // magnetOffsetDegrees = Math.toDegrees(configuration.getOffset());
        // sensorDirection = false;
        // initializationStrategy = bootToAbsValue;

        public static final double FRONT_LEFT_MODULE_STEER_OFFSET = -Math.toRadians(0.0);
        public static final double FRONT_RIGHT_MODULE_STEER_OFFSET = -Math.toRadians(0.0); 
        public static final double BACK_LEFT_MODULE_STEER_OFFSET = -Math.toRadians(0.0); 
        public static final double BACK_RIGHT_MODULE_STEER_OFFSET = -Math.toRadians(0.0);
        
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

        public static final PIDController ROTATE_TO_TARGET_CONTROLLER = new PIDController(0.009, 0, 0);

        public static final double ROTATE_MAX_SPEED = .45;
    }

    public static final class INTAKE_ROLLER {
        public static IntakeRollerSubsystem.Configuration GET_INTAKE_CONFIG() {
            IntakeRollerSubsystem.Configuration config = new IntakeRollerSubsystem.Configuration();

            config.m_runPercentOutput = 0.5;
            config.m_reversePercentOutput = -0.3;

            config.m_rampRate = 1;
            config.m_currentLimit = 30;

            // TODO: Make sure these are correct
            config.m_rightInverted = false;
            config.m_leftInverted = true;

            return config;
        }

        public static final double AUTO_SCORE_LOW_REVERSE_TIME = 1;
    }

    public static final class INTAKE_ARM {
        public static IntakeArmSubsystem.Configuration GET_INTAKE_ARM_CONFIG() {
            IntakeArmSubsystem.Configuration config = new IntakeArmSubsystem.Configuration();

            config.m_deployMilliseconds = 1000;
            config.m_stowMilliseconds = 1000;

            return config;
        }
    }

    public static final class WRIST {
        public static WristSubsystem.Configuration GET_WRIST_CONFIG() {
            WristSubsystem.Configuration config = new WristSubsystem.Configuration();

            config.m_extendMilliseconds = 1000;
            config.m_retractMilliseconds = 1000;

            return config;
        }
    }

    public static final class ELEVATOR {
        public static ElevatorSubsystem.Configuration GET_ELEVATOR_CONFIG() {
            ElevatorSubsystem.Configuration config = new ElevatorSubsystem.Configuration();

            config.m_elevatorAxisMaxSpeed = 1;

            config.m_elevatorMotorIdleMode = IdleMode.kBrake;
            config.m_elevatorMotorStallLimitAmps = 30;
            config.m_elevatorMotorFreeLimitAmps = 30;

            //TODO: Figure out smart motion constants

            return config;
        }
    }

    public static final class CLAW {
        public static ClawSubsystem.Configuration GET_CLAW_CONFIG() {
            ClawSubsystem.Configuration config = new ClawSubsystem.Configuration();

            config.m_openMilliseconds = 500;
            config.m_closeMilliseconds = 500;

            return config;
        }
    }

    public static final class ARM_EXTENSION {
        public static ArmExtensionSubsystem.Configuration GET_EXTENSION_CONFIG() {
            ArmExtensionSubsystem.Configuration config = new ArmExtensionSubsystem.Configuration();
            config.m_extendAxisMaxSpeed = 0;

            config.m_extendMotorIdleMode = IdleMode.kBrake;

            config.m_extendMotorStallLimitAmps = 0;
            config.m_extendMotorFreeLimitAmps = 0;

            config.m_isInverted = false;

            config.m_extendGrippedAmps = 0;

            config.m_extendMotorRampRate = 0;

            // smart motion config
            config.m_extendMotorP = 0;
            config.m_extendMotorI = 0;
            config.m_extendMotorD = 0;
            config.m_extendMotorIZone = 0;
            config.m_extendMotorFF = 0;
            config.m_extendMotorMaxOutput = 0;
            config.m_extendMotorMinOutput = 0;
            config.m_extendMotorMaxRPM = 0;
            config.m_extendMotorMaxVel = 0;
            config.m_extendMotorMinVel = 0;
            config.m_extendMotorMaxAcc = 0;
            config.m_extendMotorAllowedError = 0;
            config.m_rotationMotorAllowedError = 0;
            config.m_maxSpeedPercent = 0.4;
            config.m_smartMotionSlot = 0;
            return config;
        }

        public static final double RETRACTED_ROTATIONS = 0;
        public static final double INTAKE_HANDOFF_ROTATIONS = 0;

        public static final double AUTO_SCORE_MID_ROTATIONS = 0;
        public static final double AUTO_SCORE_HIGH_ROTATIONS = 0;
    }

    public static final class ARM_ROTATION {
        public static ArmRotationSubsystem.Configuration GET_ROTATION_CONFIG() {
            ArmRotationSubsystem.Configuration config = new ArmRotationSubsystem.Configuration();
            config.m_rotationAxisMaxSpeed = 0;

            config.m_rotationMotorIdleMode = IdleMode.kBrake;

            config.m_rotationMotorStallLimitAmps = 0;
            config.m_rotationMotorFreeLimitAmps = 0;

            config.m_isFollowerInverted = false;

            // smart motion config
            config.m_rotationMotorP = 0;
            config.m_rotationMotorI = 0;
            config.m_rotationMotorD = 0;
            config.m_rotationMotorIZone = 0;
            config.m_rotationMotorFF = 0;
            config.m_rotationMotorMaxOutput = 0;
            config.m_rotationMotorMinOutput = 0;
            config.m_rotationMotorMaxRPM = 0;
            config.m_rotationMotorMaxVel = 0;
            config.m_rotationMotorMinVel = 0;
            config.m_rotationMotorMaxAcc = 0;
            config.m_rotationMotorAllowedError = 0;
            config.m_maxSpeedPercent = 0.4;
            config.m_rotationAxisMaxSpeed = 0;

            config.m_rotationMotorIdleMode = IdleMode.kBrake;

            config.m_rotationMotorStallLimitAmps = 0;
            config.m_rotationMotorFreeLimitAmps = 0;

            config.m_isFollowerInverted = false;

            // smart motion config
            config.m_rotationMotorP = 0;
            config.m_rotationMotorI = 0;
            config.m_rotationMotorD = 0;
            config.m_rotationMotorIZone = 0;
            config.m_rotationMotorFF = 0;
            config.m_rotationMotorMaxOutput = 0;
            config.m_rotationMotorMinOutput = 0;
            config.m_rotationMotorMaxRPM = 0;
            config.m_rotationMotorMaxVel = 0;
            config.m_rotationMotorMinVel = 0;
            config.m_rotationMotorMaxAcc = 0;
            config.m_rotationMotorAllowedError = 0;
            config.m_maxSpeedPercent = 0.4;
            config.m_smartMotionSlot = 0;

            return config;
        }

        public static final double RETRACTED_ROTATIONS = 0;
        public static final double INTAKE_HANDOFF_ROTATIONS = 0;

        public static final double AUTO_SCORE_MID_ROTATIONS = 0;
        public static final double AUTO_SCORE_HIGH_ROTATIONS = 0;
    }

    public static final class CONTROLLER {
        public static final int DRIVE_CONTROLLER_PORT = 0;
        public static final int GUNNER_CONTROLLER_PORT = 1;

        public static final double DRIVE_CONTROLLER_DEADBAND = 0.1;
        public static final double GUNNER_CONTROLLER_DEADBAND = 0.1;

        public static final int BUTTON_BOARD_NUM_ROWS = 3;
        public static final int BUTTON_BOARD_NUM_COLS = 9;
        public static final double BUTTON_BOARD_JOYSTICK_MAX_VALUE = 32767;
    }

    public static final class COMPRESSOR {
        public static final int MIN_PRESSURE_PSI = 90;
        public static final int MAX_PRESSURE_PSI = 120;
    }

}

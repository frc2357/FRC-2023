// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.team2357.frc2023;

import com.revrobotics.CANSparkMax.IdleMode;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import com.team2357.frc2023.subsystems.ArmExtensionSubsystem;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;
import com.team2357.frc2023.subsystems.ClawSubsystem;
import com.team2357.frc2023.subsystems.IntakeArmSubsystem;
import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.frc2023.subsystems.WristSubsystem;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.math.kinematics.ChassisSpeeds;

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

        public static final int PNEUMATICS_HUB_ID = 1;

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

        public static final int MASTER_INTAKE_MOTOR_ID = 23;
        public static final int FOLLOWER_INTAKE_MOTOR_ID = 24;
        public static final int INTAKE_WINCH_MOTOR_ID = 25;

        public static final int ARM_ROTATION_MOTOR_ID = 26;

        public static final int ARM_EXTENSION_MOTOR_ID = 27;
    }

    public static final class PH_ID {

        public static final int WRIST_FORWARD_SOLENOID_CHANNEL = 3; // Red
        public static final int WRIST_REVERSE_SOLENOID_CHANNEL = 0;
        
        public static final int CLAW_FORWARD_SOLENOID_CHANNEL = 4;
        public static final int CLAW_REVERSE_SOLENOID_CHANNEL = 1; // Black

        public static final int INTAKE_SOLENOID_FORWARD_CHANNEL = 5;
        public static final int INTAKE_SOLENOID_REVERSE_CHANNEL = 2;
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

            config.m_trajectoryMaxVelocityMetersPerSecond = 2.5; // 2 for grid0 2 cone
            config.m_trajectoryMaxAccelerationMetersPerSecond = 1.5; // 1 for grid0 two cone
            config.m_xController = new PIDController(2.2, 0, 0); //.56122   2.2
            config.m_yController = new PIDController(2.2, 0, 0); //.56122
            config.m_thetaController = new PIDController(2.15, 0, 0); //2.15

            config.m_sensorPositionCoefficient = 2.0 * Math.PI / Constants.DRIVE.TICKS_PER_ROTATION
                    * SdsModuleConfigurations.MK4I_L2.getSteerReduction();

            config.m_translateXMaxSpeedMeters = 3;
            config.m_translateYMaxSpeedMeters = 3;

            config.m_translateYAngleTolerance = 1;
            config.m_translateXAngleTolerance = 1;

            config.m_defaultXAngleSetpoint = DRIVE.DEFAULT_X_ANGLE_SETPOINT;
            config.m_defaultYAngleSetpoint = DRIVE.DEFAULT_Y_ANGLE_SETPOINT;

            config.m_leftColXAngleSetpoint = DRIVE.LEFT_COL_X_ANGLE_SETPOINT;
            config.m_midColXAngleSetpoint = DRIVE.MID_COL_X_ANGLE_SETPOINT;
            config.m_rightColXAngleSetpoint = DRIVE.RIGHT_COL_X_ANGLE_SETPOINT;

            config.m_translateXController = new PIDController(0.3, 0, 0.015);
            config.m_translateYController = new PIDController(0.1, 0, 0.025);

            config.m_translationXFeedForward = new SimpleMotorFeedforward(0.244, 0);
            config.m_translationYFeedForward = new SimpleMotorFeedforward(0.244, 0.00);

            config.m_openLoopRampRateSeconds = 1;

            return config;
        }

        // CONFIGURE THE FOLLOWING ON EACH CANCODER
        // absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
        // magnetOffsetDegrees = Math.toDegrees(configuration.getOffset());
        // sensorDirection = false;
        // initializationStrategy = bootToAbsValue;

        public static final double FRONT_LEFT_MODULE_STEER_OFFSET = -Math.toRadians(8.79+180);
        public static final double FRONT_RIGHT_MODULE_STEER_OFFSET = -Math.toRadians(314.38-180); 
        public static final double BACK_LEFT_MODULE_STEER_OFFSET = -Math.toRadians(306.3-180); 
        public static final double BACK_RIGHT_MODULE_STEER_OFFSET = -Math.toRadians(163.04+180);
        
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

        public static final double ROTATE_MAXSPEED_RADIANS_PER_SECOND = 0.91;
        public static final double SYNC_ENCODER_LIMIT_MS = 10000;


        public static final double DEFAULT_Y_ANGLE_SETPOINT = 20;
        public static final double DEFAULT_X_ANGLE_SETPOINT = -9;

        public static final double LEFT_COL_X_ANGLE_SETPOINT = -10;
        public static final double MID_COL_X_ANGLE_SETPOINT = -9;
        public static final double RIGHT_COL_X_ANGLE_SETPOINT = -8;

        public static final double DEAD_RECKONING_TRANSLATION_METERS_PER_SECOND = 1;
        public static final double DEAD_RECKONING_ROTATION_RADIANS_PER_SECOND = 0.1;

        public static final ChassisSpeeds DEAD_RECKONING_X_CHASSIS_SPEEDS = new ChassisSpeeds(DEAD_RECKONING_TRANSLATION_METERS_PER_SECOND, 0, 0);
        public static final ChassisSpeeds DEAD_RECKONING_Y_CHASSIS_SPEEDS = new ChassisSpeeds(0, DEAD_RECKONING_TRANSLATION_METERS_PER_SECOND, 0);
        public static final ChassisSpeeds DEAD_RECKONING_ROTATION_CHASSIS_SPEEDS = new ChassisSpeeds(0, 0, DEAD_RECKONING_ROTATION_RADIANS_PER_SECOND);

        public static final String SWERVE_MODULE_SHUFFLEBOARD_TAB_NAME = "Drivetrain";
    
        // Tolerance for out-of-range poses on auto-mapping
        public static final double TRAJECTORY_MAP_TOLERANCE_METERS = 0.1;
    }

    public static final class INTAKE_ROLLER {
        public static final double AUTO_SCORE_LOW_REVERSE_TIME = 1;

        //TODO: Tune these
        public static final double MID_SHOT_PERCENT_OUTPUT = 0;
        public static final double MID_SHOT_DELAY_SECONDS = .25;
        public static final double HIGH_SHOT_PERCENT_OUTPUT = 0;
        public static final double HIGH_SHOT_DELAY_SECONDS = .25;
        public static final int AUTO_INTAKE_CURRENT_LIMIT = 0;
        public static final double AUTO_INTAKE_WAIT_TIME = 0;

        public static IntakeRollerSubsystem.Configuration GET_INTAKE_CONFIG() {
            IntakeRollerSubsystem.Configuration config = new IntakeRollerSubsystem.Configuration();

            config.m_runPercentOutput = 0.6;
            config.m_reversePercentOutput = -0.5;

            config.m_rollerAxisMaxSpeed = 1;

            config.m_rampRate = 1;

            config.m_peakCurrentLimit = 40;
            config.m_peakCurrentDuration = 0;
            config.m_continuousCurrentLimit = 40;

            config.m_masterInverted = true;
            config.m_followerInverted = false;
            return config;
        }

        
    }

    public static final class INTAKE_ARM {
        public static final double SOLENOID_EXTEND_WAIT_MILLIS = 250;

        // Auto score low
        public static final double AUTO_SCORE_LOW_ROTATIONS = 70;

        // Cube shooting
        //TODO: Tune these
        public static final double MID_SHOT_SETPOINT_ROTATIONS = 0;
        public static final double HIGH_SHOT_SETPOINT_ROTATIONS = 0;

        public static final double INTAKE_HANDOFF_WINCH_ROTATIONS = 60;

        public static final int WINCH_DEPLOY_PID_SLOT = 0;
        public static final int WINCH_STOW_PID_SLOT = 1;

        public static final double WINCH_AMP_ZERO_PERCENT_OUTPUT = -0.4;
        public static final int WINCH_AMP_ZERO_MAX_AMPS = 15;

        public static IntakeArmSubsystem.Configuration GET_INTAKE_ARM_CONFIG() {
            IntakeArmSubsystem.Configuration config = new IntakeArmSubsystem.Configuration();

            config.m_deployMilliseconds = 1000;
            config.m_stowMilliseconds = 1000;

            config.m_isInverted = true;

            config.m_winchAxisMaxSpeed = 0.7;

            config.m_winchMotorIdleMode = IdleMode.kBrake;

            config.m_winchMotorStallLimitAmps = 15;
            config.m_winchMotorFreeLimitAmps = 30;

            // smart motion config
            // extend PID
            config.m_winchDeployP = 0;
            config.m_winchDeployI = 0;
            config.m_winchDeployD = 0;
            config.m_winchDeployIZone = 0;
            config.m_winchDeployFF = 0.0001;
            config.m_winchDeployPidSlot = WINCH_DEPLOY_PID_SLOT;

            // retract PID
            config.m_winchStowP = 0;
            config.m_winchStowI = 0;
            config.m_winchStowD = 0;
            config.m_winchStowIZone = 0;
            config.m_winchStowFF = 0.0005;
            config.m_winchStowPidSlot = WINCH_STOW_PID_SLOT;

            // Smart motion
            config.m_pidMaxOutput = 1;
            config.m_pidMinOutput = -1;
            config.m_smartMotionMaxVelRPM = 10000;
            config.m_smartMotionMinVelRPM = 0;
            config.m_smartMotionMaxAccRPM = 10000;
            config.m_smartMotionRotationAllowedError = 2;

            config.m_winchMotorAllowedError = 2;
            config.m_winchDeployRotations = 140;
            config.m_winchStowRotations = 0.0;

            return config;
        }

       
    }

    public static final class WRIST {
        public static WristSubsystem.Configuration GET_WRIST_CONFIG() {
            WristSubsystem.Configuration config = new WristSubsystem.Configuration();

            config.m_extendMilliseconds = 500;
            config.m_retractMilliseconds = 2000;

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
        public static final double RETRACTED_ROTATIONS = 0;

        public static final double AUTO_SCORE_MID_ROTATIONS = 0;
        public static final double AUTO_SCORE_HIGH_ROTATIONS = 260;

        public static final double ARM_EXTENSION_AMP_ZERO_PERCENT_OUTPUT = -0.2;
        public static final int ARM_EXTENSION_AMP_ZERO_MAX_AMPS = 25;

        public static ArmExtensionSubsystem.Configuration GET_EXTENSION_CONFIG() {
            ArmExtensionSubsystem.Configuration config = new ArmExtensionSubsystem.Configuration();
            config.m_extendAxisMaxSpeed = 0;

            config.m_extendMotorIdleMode = IdleMode.kBrake;

            config.m_extendMotorStallLimitAmps = 30;
            config.m_extendMotorFreeLimitAmps = 30;

            config.m_isInverted = true;

            config.m_extendMotorRampRate = 0;

            config.m_shuffleboardTunerPRange = 0.2;
            config.m_shuffleboardTunerIRange = 0.2;
            config.m_shuffleboardTunerDRange = 0.2;

            // smart motion config

            // extend PID
            config.m_extendP = 0.00001;
            config.m_extendI = 0;
            config.m_extendD = 0;
            config.m_extendIZone = 0;
            config.m_extendFF = 0.00011;
            config.m_extendPidSlot = 0;

            // Smart motion
            config.m_pidMaxOutput = 1;
            config.m_pidMinOutput = -1;
            config.m_smartMotionMaxVelRPM = 8700;
            config.m_smartMotionMinVelRPM = 0;
            config.m_smartMotionMaxAccRPM = 8700*2;
            config.m_smartMotionRotationAllowedError = 0.5;
            config.m_rotationAllowedError = 0.5;

            config.m_maxSpeedPercent = 0.7;
            return config;
        }

        
    }

    public static final class ARM_ROTATION {
        public static final double CHAIN_BACKLASH_ROTATIONS = 15;

        public static final double RETRACTED_ROTATIONS = 0;

        public static final double AUTO_SCORE_MID_ROTATIONS = 45;
        public static final double AUTO_SCORE_HIGH_ROTATIONS = 62;

        public static final double ARM_ROTATION_GEAR_RATIO  = 190.91;
        public static final double ARM_HANDOFF_ROTATIONS = ARM_ROTATION_GEAR_RATIO / 8;

        public static final double ARM_ROTATION_AMP_ZERO_PERCENT_OUTPUT = -0.1;
        public static final int ARM_ROTATION_AMP_ZERO_MAX_AMPS = 25;


        public static final double ARM_ROTATION_AMP_ZERO_TIME_MILLIS = 1000;

        public static ArmRotationSubsystem.Configuration GET_ROTATION_CONFIG() {
            ArmRotationSubsystem.Configuration config = new ArmRotationSubsystem.Configuration();

            config.m_rotationZeroTolerance = 2.5;
            
            config.m_rotationAxisMaxSpeed = 0.7;
            config.m_maxSpeedPercent = 0.4;

            config.m_rotationMotorIdleMode = IdleMode.kBrake;

            config.m_rotationMotorStallLimitAmps = 30;
            config.m_rotationMotorFreeLimitAmps = 30;

            config.m_isInverted = true;

            // smart motion config
            config.m_rotationMotorP = 0.00075;
            config.m_rotationMotorI = 0;
            config.m_rotationMotorD = 0;
            config.m_rotationMotorIZone = 0;
            config.m_rotationMotorFF = 0.001;

            config.m_rotationMotorMaxOutput = 1;
            config.m_rotationMotorMinOutput = -1;
            config.m_rotationMotorMaxRPM = 5676;
            config.m_rotationMotorMaxVel = 4600;
            config.m_rotationMotorMinVel = 0;
            config.m_rotationMotorMaxAcc = 4600;
            config.m_rotationMotorAllowedError = 0.5;

            config.m_smartMotionSlot = 0;

            // Static gain, will likely be zero
            config.m_feedforwardKs = 0;

            // Gravity gain, should be the gain required to keep the arm parallel with the floor
            config.m_feedforwardKg = 0.64;

            // Velocity gain, will be zero
            config.m_feedforwardKv = 0;

            // Acceleration gain, will be zero
            config.m_feedforwardKa = 0;

            // TODO: Calculate
            config.m_armHorizontalRotations = ARM_ROTATION_GEAR_RATIO / 4; // 90 degrees
            config.m_rotationsPerRadian = ARM_ROTATION_GEAR_RATIO / (2 * Math.PI);

            return config;
        }

    }

    public static final class LIMELIGHT {
        public static final String LEFT_LIMELIGHT_NAME = "limelight-left";
        public static final String RIGHT_LIMELIGHT_NAME = "limelight-right";
        
        public static final double LEFT_LIMELIGHT_TX_SETPOINT = Double.NaN;
        public static final double RIGHT_LIMELIGHT_TX_SETPOINT = Double.NaN;
    }

    public static final class BUTTONBOARD {
        public static final String BUTTONBOARD_TABLE_NAME = "buttonboard";
        public static final String ROW_TOPIC_NAME = "row";
        public static final String COLUMN_TOPIC_NAME = "col";
        public static final String ALLIANCE_TOPIC_NAME = "alliance";
    
    }

    public static final class APRILTAG_POSE {
        public static final String APRILTAG_TABLE_NAME = "apriltag";
        public static final String POSE_TOPIC_NAME = "pose";
    }

    public static final class CONTROLLER {
        public static final int DRIVE_CONTROLLER_PORT = 0;
        public static final int GUNNER_CONTROLLER_PORT = 1;

        public static final double DRIVE_CONTROLLER_DEADBAND = 0.05;
        public static final double GUNNER_CONTROLLER_DEADBAND = 0.1;

        public static final double RUMBLE_INTENSITY = 0.5;
        public static final double RUMBLE_TIMEOUT_SECONDS_ON_TELEOP_AUTO = 0.5;

        public static final int BUTTON_BOARD_NUM_ROWS = 3;
        public static final int BUTTON_BOARD_NUM_COLS = 9;
        public static final double BUTTON_BOARD_JOYSTICK_MAX_VALUE = 32767;
    }

    public static final class COMPRESSOR {
        public static final int MIN_PRESSURE_PSI = 90;
        public static final int MAX_PRESSURE_PSI = 100;
    }
    public static final class AMP_ZERO{
        public static final int AMP_ZERO_DEADLINE_SECONDS = 1;
    }

}

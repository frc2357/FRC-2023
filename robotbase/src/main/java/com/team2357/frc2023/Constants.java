// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.team2357.frc2023;

import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;

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

            config.m_xController = new PIDController(.56122, 0, 0);
            config.m_yController = new PIDController(.56122, 0, 0);
            config.m_thetaController = new ProfiledPIDController(2.15, 0, 0, new TrapezoidProfile.Constraints(config.m_maxAngularVelocityRadiansPerSecond, null));

            return config;
        }

        public static final double FRONT_LEFT_MODULE_STEER_OFFSET = -Math.toRadians(162.7);
        public static final double FRONT_RIGHT_MODULE_STEER_OFFSET = -Math.toRadians(135.08);
        public static final double BACK_LEFT_MODULE_STEER_OFFSET = -Math.toRadians(125);
        public static final double BACK_RIGHT_MODULE_STEER_OFFSET = -Math.toRadians(9.45);

        public static final PIDController CHARGE_STATION_BALANCE_CONTROLLER = new PIDController(0.5, 0, 0);
    }

    public static final class CONTROLLER {
        public static final int DRIVE_CONTROLLER_PORT = 0;
        public static final int GUNNER_CONTROLLER_PORT = 1;

        public static final double DRIVE_CONTROLLER_DEADBAND = 0.1;
        public static final double GUNNER_CONTROLLER_DEADBAND = 0.1;
    }

}

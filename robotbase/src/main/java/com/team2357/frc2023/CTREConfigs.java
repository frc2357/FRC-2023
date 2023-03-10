package com.team2357.frc2023;

import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.sensors.CANCoderConfiguration;

public final class CTREConfigs {
    public static TalonFXConfiguration swerveSteerFXConfig;
    public static TalonFXConfiguration swerveDriveFXConfig;
    public static CANCoderConfiguration swerveCANCoderConfig;

    public static void createConfigs() {
        swerveSteerFXConfig = new TalonFXConfiguration();
        swerveDriveFXConfig = new TalonFXConfiguration();
        swerveCANCoderConfig = new CANCoderConfiguration();

        SupplyCurrentLimitConfiguration steerSupplyLimit = new SupplyCurrentLimitConfiguration(
            Constants.TEAM_364_SWERVE.STEER_ENABLE_CURRENT_LIMIT,
            Constants.TEAM_364_SWERVE.STEER_CONTINUOUS_CURRENT_LIMIT,
            Constants.TEAM_364_SWERVE.STEER_PEAK_CURRENT_LIMIT,
            Constants.TEAM_364_SWERVE.STEER_PEAK_CURRENT_DURATION
        );

        swerveSteerFXConfig.slot0.kP = Constants.TEAM_364_SWERVE.STEER_KP;
        swerveSteerFXConfig.slot0.kI = Constants.TEAM_364_SWERVE.STEER_KI;
        swerveSteerFXConfig.slot0.kD = Constants.TEAM_364_SWERVE.STEER_KD;
        swerveSteerFXConfig.slot0.kF = Constants.TEAM_364_SWERVE.STEER_KF;
        swerveSteerFXConfig.slot0.supplyCurrLimit = steerSupplyLimit;

        SupplyCurrentLimitConfiguration driveSupplyLimit = new SupplyCurrentLimitConfiguration(
            Constants.TEAM_364_SWERVE.DRIVE_ENABLE_CURRENT_LIMIT,
            Constants.TEAM_364_SWERVE.DRIVE_CONTINUOUS_CURRENT_LIMIT,
            Constants.TEAM_364_SWERVE.DRIVE_PEAK_CURRENT_LIMIT,
            Constants.TEAM_364_SWERVE.DRIVE_PEAK_CURRENT_DURATION
        )

        swerveDriveFXConfig.slot0.kP = Constants.TEAM_364_SWERVE.DRIVE_KP;
        swerveDriveFXConfig.slot0.kI = Constants.TEAM_364_SWERVE.DRIVE_KI;
        swerveDriveFXConfig.slot0.kD = Constants.TEAM_364_SWERVE.DRIVE_KD;
        swerveDriveFXConfig.slot0.kF = Constants.TEAM_364_SWERVE.DRIVE_KF;
        swerveDriveFXConfig.slot0.supplyCurrLimit = driveSupplyLimit;

        swerveCANCoderConfig.absoluteSensorRange = absoluteSensorRange.Unsigned_0_to_360;
        swerveCanCoderConfig.initializationStrategy = SensorInitializationStrategy.BootToAbsolutePosition;
    }
}
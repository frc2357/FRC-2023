// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.team2357.frc2023.subsystems;

import org.littletonrobotics.junction.Logger;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.WPI_Pigeon2;
import com.pathplanner.lib.PathConstraints;
import com.swervedrivespecialties.swervelib.AbsoluteEncoder;
import com.swervedrivespecialties.swervelib.SwerveModule;
import com.team2357.frc2023.Constants;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.DoubleArraySubscriber;
import edu.wpi.first.networktables.DoubleArrayTopic;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PubSubOption;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SwerveDriveSubsystem extends SubsystemBase {
	private static SwerveDriveSubsystem instance = null;

	private boolean m_isZeroed;

	public static SwerveDriveSubsystem getInstance() {
		return instance;
	}

	private SwerveDriveKinematics m_kinematics;

	private WPI_Pigeon2 m_pigeon;

	private SwerveModule m_frontLeftModule;
	private SwerveModule m_frontRightModule;
	private SwerveModule m_backLeftModule;
	private SwerveModule m_backRightModule;

	private ChassisSpeeds m_chassisSpeeds = new ChassisSpeeds(0.0, 0.0, 0.0);

	private Configuration m_config;

	private SwerveDriveOdometry m_odometry;

	private PathConstraints m_pathConstraints;

	public NetworkTable m_limelightTable;
	public DoubleArrayTopic m_limelightInfo;
	private DoubleArraySubscriber m_limelightSubscriber;

	public static class Configuration {
		/**
		 * The left-to-right distance between the drivetrain wheels (measured from
		 * center to center)
		 */
		public double m_trackwidthMeters;

		/**
		 * The front-to-back distance between the drivetrain wheels (measured from
		 * center to center)
		 */
		public double m_wheelbaseMeters;

		/**
		 * The maximum voltage that will be delivered to the drive motors
		 */
		public double m_maxVoltage;

		/**
		 * The maximum velocity of the robot in meters per second
		 * (How fast the robot can drive in a straight line)
		 * 
		 * Formula: 6380 / 60 * <gear ratio> * <wheel diameter> * Math.PI
		 */
		public double m_maxVelocityMetersPerSecond;

		/**
		 * The maximum angular velocity of the robot in radians per second
		 * (how fast the robot can rotate in place)
		 * 
		 * Formula: m_maxVelocityMetersPerSecond / Math.hypot(m_trackwidthMeters / 2,
		 * m_wheelbaseMeters / 2)
		 */
		public double m_maxAngularVelocityRadiansPerSecond;

		public double m_maxAngularAccelerationRadiansPerSecondSquared;

		public double m_trajectoryMaxVelocityMetersPerSecond;

		public double m_trajectoryMaxAccelerationMetersPerSecond;

		public PIDController m_xController;
		public PIDController m_yController;
		public PIDController m_thetaController;

		/**
		 * Conversion coefficient to go from degrees to Falcon500 sensor units
		 * 
		 * Formula: 2.0 * Math.PI / TICKS_PER_ROTATION *
		 * moduleConfiguration.getSteerReduction()
		 */
		public double m_sensorPositionCoefficient;
	}

	public SwerveDriveSubsystem(WPI_Pigeon2 pigeon, SwerveModule frontLeft, SwerveModule frontRight,
			SwerveModule backLeft, SwerveModule backRight) {
		m_pigeon = pigeon;

		m_frontLeftModule = frontLeft;
		m_frontRightModule = frontRight;
		m_backLeftModule = backLeft;
		m_backRightModule = backRight;

		m_limelightTable = NetworkTableInstance.getDefault().getTable("limelight");
		m_limelightInfo = m_limelightTable.getDoubleArrayTopic("botpose");
		m_limelightSubscriber = m_limelightInfo.subscribe(null, PubSubOption.keepDuplicates(true));

		instance = this;
	}

	public void configure(Configuration config) {
		m_config = config;

		m_kinematics = new SwerveDriveKinematics(
				new Translation2d(m_config.m_trackwidthMeters / 2.0,
						m_config.m_wheelbaseMeters / 2.0),
				new Translation2d(m_config.m_trackwidthMeters / 2.0,
						-m_config.m_wheelbaseMeters / 2.0),
				new Translation2d(-m_config.m_trackwidthMeters / 2.0,
						m_config.m_wheelbaseMeters / 2.0),
				new Translation2d(-m_config.m_trackwidthMeters / 2.0,
						-m_config.m_wheelbaseMeters / 2.0));

		m_odometry = new SwerveDriveOdometry(m_kinematics, getGyroscopeRotation(),
				new SwerveModulePosition[] { m_frontLeftModule.getPosition(),
						m_frontRightModule.getPosition(),
						m_backLeftModule.getPosition(), m_backRightModule.getPosition() });

		m_pathConstraints = new PathConstraints(m_config.m_trajectoryMaxVelocityMetersPerSecond,
				m_config.m_trajectoryMaxAccelerationMetersPerSecond);
	}

	public PIDController getXController() {
		return m_config.m_xController;
	}

	public PIDController getYController() {
		return m_config.m_yController;
	}

	public PIDController getThetaController() {
		return m_config.m_thetaController;
	}

	public SwerveDriveKinematics getKinematics() {
		return m_kinematics;
	}

	public PathConstraints getPathConstraints() {
		return m_pathConstraints;
	}

	public boolean isReadyToZero() {
		if (isReadyToZero(m_frontLeftModule) && isReadyToZero(m_frontRightModule) && isReadyToZero(m_backLeftModule)
				&& isReadyToZero(m_backRightModule)) {
			return true;
		}
		return false;
	}

	private boolean isReadyToZero(SwerveModule module) {
		WPI_TalonFX steerMotor;
		steerMotor = (WPI_TalonFX) (module.getSteerMotor());

		double absoluteAngle = module.getSteerEncoder().getAbsoluteAngle();
		((WPI_TalonFX) (module.getSteerMotor()))
				.setSelectedSensorPosition(absoluteAngle / m_config.m_sensorPositionCoefficient);

		return isEncoderSynced(steerMotor, module.getSteerEncoder());
	}

	private boolean isEncoderSynced(WPI_TalonFX steerMotor, AbsoluteEncoder steerEncoder) {
		double difference = Math.abs(steerMotor.getSelectedSensorPosition() * m_config.m_sensorPositionCoefficient
				- steerEncoder.getAbsoluteAngle());
		difference %= Math.PI;
		System.out.println(difference);
		return difference < Constants.DRIVE.ENCODER_SYNC_ACCURACY_RADIANS
				|| Math.abs(difference - Math.PI) < Constants.DRIVE.ENCODER_SYNC_ACCURACY_RADIANS;
	}

	public void zero() {
		SwerveModuleState state = new SwerveModuleState(0.0, Rotation2d.fromDegrees(0.0));

		m_frontLeftModule.set(
				state.speedMetersPerSecond / m_config.m_maxVelocityMetersPerSecond * m_config.m_maxVoltage,
				state.angle.getRadians());
		m_frontRightModule.set(
				state.speedMetersPerSecond / m_config.m_maxVelocityMetersPerSecond * m_config.m_maxVoltage,
				state.angle.getRadians());
		m_backLeftModule.set(
				state.speedMetersPerSecond / m_config.m_maxVelocityMetersPerSecond * m_config.m_maxVoltage,
				state.angle.getRadians());
		m_backRightModule.set(
				state.speedMetersPerSecond / m_config.m_maxVelocityMetersPerSecond * m_config.m_maxVoltage,
				state.angle.getRadians());
	}

	public void checkEncodersSynced() {
		m_isZeroed = ((isEncoderSynced((WPI_TalonFX) m_frontLeftModule.getSteerMotor(),
				m_frontLeftModule.getSteerEncoder())) &&
				(isEncoderSynced((WPI_TalonFX) m_frontRightModule.getSteerMotor(),
						m_frontRightModule.getSteerEncoder()))
				&&
				(isEncoderSynced((WPI_TalonFX) m_backLeftModule.getSteerMotor(), m_backLeftModule.getSteerEncoder())) &&
				(isEncoderSynced((WPI_TalonFX) m_backRightModule.getSteerMotor(),
						m_backRightModule.getSteerEncoder())));
	}

	public void zeroGyroscope() {
		m_pigeon.reset();
	}

	public double getYaw() {
		return m_pigeon.getYaw();
	}

	public double getPitch() {
		return m_pigeon.getPitch();
	}

	public double getRoll() {
		return m_pigeon.getRoll();
	}

	public Rotation2d getGyroscopeRotation() {
		return Rotation2d.fromDegrees(m_pigeon.getYaw());
	}

	public Pose2d getPose() {
		return m_odometry.getPoseMeters();
	}

	public void resetOdometry(Pose2d pose) {
		m_odometry.resetPosition(
				getGyroscopeRotation(),
				new SwerveModulePosition[] { m_frontLeftModule.getPosition(),
						m_frontRightModule.getPosition(),
						m_backLeftModule.getPosition(), m_backRightModule.getPosition() },
				pose);
	}

	public void drive(double x, double y, double rotation) {
		ChassisSpeeds chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
				x * m_config.m_maxVelocityMetersPerSecond,
				y * m_config.m_maxVelocityMetersPerSecond,
				rotation * m_config.m_maxAngularVelocityRadiansPerSecond,
				getGyroscopeRotation());

		drive(chassisSpeeds);
	}

	public void drive(ChassisSpeeds chassisSpeeds) {
		if (!m_isZeroed) {
			DriverStation.reportError("Swerve is not zeroed", false);
			return;
		}
		m_chassisSpeeds = chassisSpeeds;
		SwerveModuleState[] states = m_kinematics.toSwerveModuleStates(m_chassisSpeeds);
		SwerveDriveKinematics.desaturateWheelSpeeds(states, m_config.m_maxVelocityMetersPerSecond);

		m_frontLeftModule.set(
				states[0].speedMetersPerSecond / m_config.m_maxVelocityMetersPerSecond * m_config.m_maxVoltage,
				states[0].angle.getRadians());
		m_frontRightModule.set(
				states[1].speedMetersPerSecond / m_config.m_maxVelocityMetersPerSecond * m_config.m_maxVoltage,
				states[1].angle.getRadians());
		m_backLeftModule.set(
				states[2].speedMetersPerSecond / m_config.m_maxVelocityMetersPerSecond * m_config.m_maxVoltage,
				states[2].angle.getRadians());
		m_backRightModule.set(
				states[3].speedMetersPerSecond / m_config.m_maxVelocityMetersPerSecond * m_config.m_maxVoltage,
				states[3].angle.getRadians());

		Logger.getInstance().recordOutput("Swerve States", states);
	}

	public void setOdemetryFromApriltag() {
		double[] vals = m_limelightSubscriber.get();
		if (vals.length >= 2) {
			Translation2d t2d = new Translation2d(vals[0], vals[1]);
			Rotation2d r2d = new Rotation2d(getYaw());
			Pose2d p2d = new Pose2d(t2d, r2d);
			resetOdometry(p2d);
		} else {
			DriverStation.reportError("No AprilTag target", false);
		}
	}

	public void balance() {
		double yaw, direction, angle, error, power;
		angle = 0;
		direction = 0;

		yaw = Math.abs(getYaw() % 360);

		if ((0 <= yaw && yaw < 45) || (315 <= yaw && yaw <= 360)) {
			direction = 1;
			angle = getRoll();
		} else if (45 <= yaw && yaw < 135) {
			direction = 1;
			angle = getPitch();
		} else if (135 <= yaw && yaw < 225) {
			direction = -1;
			angle = getRoll();
		} else if (225 <= yaw && yaw < 315) {
			direction = -1;
			angle = getPitch();
		}

		if (angle > Constants.DRIVE.BALANCE_FULL_TILT_DEGREES) {
			return;
		}

		error = Math.copySign(Constants.DRIVE.BALANCE_LEVEL_DEGREES + Math.abs(angle), angle);
		power = Math.min(Math.abs(Constants.DRIVE.BALANCE_KP * error), Constants.DRIVE.BALANCE_MAX_POWER);
		power = Math.copySign(power, error);

		power *= direction;

		drive(power, 0, 0);
	}

	@Override
	public void periodic() {
		// m_odometry.update(getGyroscopeRotation(),
		// new SwerveModulePosition[] { m_frontLeftModule.getPosition(),
		// m_frontRightModule.getPosition(),
		// m_backLeftModule.getPosition(), m_backRightModule.getPosition() });
		setOdemetryFromApriltag();

		SmartDashboard.putNumber("Angle", m_pigeon.getYaw());

		SmartDashboard.putNumber("Yaw", m_pigeon.getYaw());
		SmartDashboard.putNumber("Pose X", m_odometry.getPoseMeters().getX());
		SmartDashboard.putNumber("Pose Y", m_odometry.getPoseMeters().getY());
		SmartDashboard.putNumber("Pose Angle", m_odometry.getPoseMeters().getRotation().getDegrees());

		Logger.getInstance().recordOutput("Robot Pose", m_odometry.getPoseMeters());
	}
}

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.team2357.frc2023.subsystems;

import org.littletonrobotics.junction.Logger;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.sensors.WPI_Pigeon2;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.swervedrivespecialties.swervelib.AbsoluteEncoder;
import com.swervedrivespecialties.swervelib.Mk4iSwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SwerveModule;
import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem.LIMELIGHT;
import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import com.team2357.lib.subsystems.LimelightSubsystem;
import com.team2357.lib.util.Utility;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveDriveSubsystem extends ClosedLoopSubsystem {
	private static SwerveDriveSubsystem instance = null;

	private boolean m_areEncodersSynced;

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

	private SwerveDrivePoseEstimator m_poseEstimator;

	// The current path the robot is running
	private PathPlannerTrajectory m_currentTrajectory;
	// Time when trajectory starts
	private double m_trajectoryStartSeconds;

	private Twist2d m_fieldVelocity;

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
		public double m_robotCentricMaxVelocityPerSecond;

		/**
		 * The maximum angular velocity of the robot in radians per second
		 * (how fast the robot can rotate in place)
		 * 
		 * Formula: m_maxVelocityMetersPerSecond / Math.hypot(m_trackwidthMeters / 2,
		 * m_wheelbaseMeters / 2)
		 */
		public double m_maxAngularVelocityRadiansPerSecond;
		public double m_robotCentricMaxAngularVelocityRadiansPerSecond;

		public double m_maxAngularAccelerationRadiansPerSecondSquared;

		// Trajectory PID controllers
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

		// Standard deviations for pose estimation
		public Matrix<N3, N1> m_stateStdDevs;
		public Matrix<N3, N1> m_visionMeasurementStdDevs;

		/**
		 * Error tolerance in meters for vision estimate from encoder estimate in meters
		 */
		public double m_visionToleranceMeters;

		/**
		 * Profiled PID controller for translation for autoAlign
		 */
		public ProfiledPIDController m_autoAlignDriveController;

		/**
		 * Profiled PID controller for rotation for autoAlign
		 */
		public ProfiledPIDController m_autoAlignThetaController;
	}

	public SwerveDriveSubsystem(int pigeonId, int[] frontLeftIds, int[] frontRightIds,
			int[] backLeftIds, int[] backRightIds, String canbus, String shuffleboardTab) {
		ShuffleboardTab tab = Shuffleboard.getTab(shuffleboardTab);

		m_pigeon = new WPI_Pigeon2(pigeonId, canbus);

		m_frontLeftModule = Mk4iSwerveModuleHelper.createFalcon500(
				tab.getLayout("Front Left Module", BuiltInLayouts.kList)
						.withSize(2, 4)
						.withPosition(0, 0),
				Mk4iSwerveModuleHelper.GearRatio.L2,
				frontLeftIds[0],
				frontLeftIds[1],
				frontLeftIds[2],
				canbus,
				0 // Offsets are set manually so this parameter is unnecessary
		);

		m_frontRightModule = Mk4iSwerveModuleHelper.createFalcon500(
				tab.getLayout("Front Right Module", BuiltInLayouts.kList)
						.withSize(2, 4)
						.withPosition(2, 0),
				Mk4iSwerveModuleHelper.GearRatio.L2,
				frontRightIds[0],
				frontRightIds[1],
				frontRightIds[2],
				canbus,
				0 // Offsets are set manually so this parameter is unnecessary
		);

		m_backLeftModule = Mk4iSwerveModuleHelper.createFalcon500(
				tab.getLayout("Back Left Module", BuiltInLayouts.kList)
						.withSize(2, 4)
						.withPosition(4, 0),
				Mk4iSwerveModuleHelper.GearRatio.L2,
				backLeftIds[0],
				backLeftIds[1],
				backLeftIds[2],
				canbus,
				0 // Offsets are set manually so this parameter is unnecessary
		);

		m_backRightModule = Mk4iSwerveModuleHelper.createFalcon500(
				tab.getLayout("Back Right Module", BuiltInLayouts.kList)
						.withSize(2, 4)
						.withPosition(6, 0),
				Mk4iSwerveModuleHelper.GearRatio.L2,
				backRightIds[0],
				backRightIds[1],
				backRightIds[2],
				canbus,
				0 // Offsets are set manually so this parameter is unnecessary
		);

		setCoastMode();

		m_fieldVelocity = new Twist2d();
		instance = this;
	}

	public void configure(Configuration config) {
		m_config = config;

		m_pigeon.configFactoryDefault();

		m_kinematics = new SwerveDriveKinematics(
				new Translation2d(m_config.m_trackwidthMeters / 2.0,
						m_config.m_wheelbaseMeters / 2.0),
				new Translation2d(m_config.m_trackwidthMeters / 2.0,
						-m_config.m_wheelbaseMeters / 2.0),
				new Translation2d(-m_config.m_trackwidthMeters / 2.0,
						m_config.m_wheelbaseMeters / 2.0),
				new Translation2d(-m_config.m_trackwidthMeters / 2.0,
						-m_config.m_wheelbaseMeters / 2.0));

		m_poseEstimator = new SwerveDrivePoseEstimator(m_kinematics, getGyroscopeRotation(),
				new SwerveModulePosition[] { m_frontLeftModule.getPosition(),
						m_frontRightModule.getPosition(),
						m_backLeftModule.getPosition(), m_backRightModule.getPosition() },
				new Pose2d(0.0, 0.0, getGyroscopeRotation()), m_config.m_stateStdDevs,
				m_config.m_visionMeasurementStdDevs);
	}

	public void setBrakeMode() {
		((TalonFX) m_frontLeftModule.getDriveMotor()).setNeutralMode(NeutralMode.Brake);
		((TalonFX) m_frontRightModule.getDriveMotor()).setNeutralMode(NeutralMode.Brake);
		((TalonFX) m_backLeftModule.getDriveMotor()).setNeutralMode(NeutralMode.Brake);
		((TalonFX) m_backRightModule.getDriveMotor()).setNeutralMode(NeutralMode.Brake);
	}

	public void setCoastMode() {
		((TalonFX) m_frontLeftModule.getDriveMotor()).setNeutralMode(NeutralMode.Coast);
		((TalonFX) m_frontRightModule.getDriveMotor()).setNeutralMode(NeutralMode.Coast);
		((TalonFX) m_backLeftModule.getDriveMotor()).setNeutralMode(NeutralMode.Coast);
		((TalonFX) m_backRightModule.getDriveMotor()).setNeutralMode(NeutralMode.Coast);
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

	public ProfiledPIDController getAutoAlignDriveController() {
		return m_config.m_autoAlignDriveController;
	}

	public ProfiledPIDController getAutoAlignThetaController() {
		return m_config.m_autoAlignThetaController;
	}

	public Twist2d getFieldVelocity() {
		return m_fieldVelocity;
	}

	public SwerveDriveKinematics getKinematics() {
		return m_kinematics;
	}

	public void syncEncoders() {
		syncEncoder(m_frontLeftModule);
		syncEncoder(m_frontRightModule);
		syncEncoder(m_backLeftModule);
		syncEncoder(m_backRightModule);
	}

	private void syncEncoder(SwerveModule module) {
		TalonFX steerMotor = (TalonFX) (module.getSteerMotor());

		double absoluteAngle = module.getSteerEncoder().getAbsoluteAngle();
		ErrorCode error = steerMotor.setSelectedSensorPosition(absoluteAngle / m_config.m_sensorPositionCoefficient);
		if (error != ErrorCode.OK)
			System.out
					.println("Sensor position unsuccessfully set on motor " + steerMotor.getDeviceID() + ": " + error);
	}

	private boolean isEncoderSynced(SwerveModule module) {
		TalonFX steerMotor = (TalonFX) (module.getSteerMotor());
		AbsoluteEncoder steerEncoder = module.getSteerEncoder();

		double difference = Math.abs(steerMotor.getSelectedSensorPosition() * m_config.m_sensorPositionCoefficient
				- steerEncoder.getAbsoluteAngle());
		difference %= Math.PI;
		// System.out.println(difference);
		return difference < Constants.DRIVE.ENCODER_SYNC_ACCURACY_RADIANS
				|| Math.abs(difference - Math.PI) < Constants.DRIVE.ENCODER_SYNC_ACCURACY_RADIANS;
	}

	public boolean checkEncodersSynced() {
		m_areEncodersSynced = ((isEncoderSynced(m_frontLeftModule)) &&
				(isEncoderSynced(m_frontRightModule)) &&
				(isEncoderSynced(m_backLeftModule)) &&
				(isEncoderSynced(m_backRightModule)));

		return m_areEncodersSynced;
	}

	public boolean getIsEncodersSynced() {
		return m_areEncodersSynced;
	}

	public void zeroGyroscope() {
		m_pigeon.reset();
	}

	public void setGyroScope(double degrees) {
		m_pigeon.setYaw(degrees);
	}

	public double getYaw() {
		return m_pigeon.getYaw();
	}

	public double getYaw0To360() {
		double yaw = getYaw() % 360;
		while (yaw < 0) {
			yaw += 360;
		}
		return yaw;
	}

	public double getPitch() {
		return m_pigeon.getPitch();
	}

	public double getRoll() {
		return m_pigeon.getRoll();
	}

	public Rotation2d getGyroscopeRotation() {
		return Rotation2d.fromDegrees(getYaw());
	}

	public Pose2d getPose() {
		return m_poseEstimator.getEstimatedPosition();
	}

	public void resetPoseEstimator(Pose2d pose) {
		setGyroScope(pose.getRotation().getDegrees());
		m_poseEstimator.resetPosition(
				pose.getRotation(),
				new SwerveModulePosition[] { m_frontLeftModule.getPosition(),
						m_frontRightModule.getPosition(),
						m_backLeftModule.getPosition(), m_backRightModule.getPosition() },
				pose);
	}

	public void drive(double x, double y, double rotation) {
		drive(x, y, rotation, true);
	}

	public void drive(double x, double y, double rotation, boolean fieldOriented) {
		ChassisSpeeds chassisSpeeds;

		if (fieldOriented) {
			chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
					x * m_config.m_maxVelocityMetersPerSecond,
					y * m_config.m_maxVelocityMetersPerSecond,
					rotation * m_config.m_maxAngularVelocityRadiansPerSecond,
					getGyroscopeRotation());
		} else {
			chassisSpeeds = new ChassisSpeeds(
					x * m_config.m_robotCentricMaxVelocityPerSecond,
					y * m_config.m_robotCentricMaxVelocityPerSecond,
					rotation * m_config.m_robotCentricMaxAngularVelocityRadiansPerSecond);
		}
		drive(chassisSpeeds);
	}

	public void drive(ChassisSpeeds chassisSpeeds) {
		if (!m_areEncodersSynced) {
			DriverStation.reportError("Swerve is not synced", false);
			syncEncoders();
			checkEncodersSynced();
			return;
		}

		m_chassisSpeeds = chassisSpeeds;
	}

	public void setCurrentTrajectory(PathPlannerTrajectory trajectory) {
		m_currentTrajectory = trajectory;
		m_trajectoryStartSeconds = Timer.getFPGATimestamp();
	}

	public void endTrajectory() {
		m_currentTrajectory = null;
		m_trajectoryStartSeconds = 0;
	}

	public void updatePoseEstimator() {

		LimelightSubsystem leftLL = DualLimelightManagerSubsystem.getInstance().getLimelight(LIMELIGHT.LEFT);
		LimelightSubsystem rightLL = DualLimelightManagerSubsystem.getInstance().getLimelight(LIMELIGHT.RIGHT);

		Pose2d leftPose = leftLL.getCurrentAllianceLimelightPose();
		Pose2d rightPose = rightLL.getCurrentAllianceLimelightPose();

		double leftTime = leftLL.getCurrentAllianceBotposeTimestamp();
		double rightTime = rightLL.getCurrentAllianceBotposeTimestamp();

		if (leftPose != null) {
			addVisionPoseEstimate(leftPose, leftTime);
		}

		if (rightPose != null) {
			addVisionPoseEstimate(rightPose, rightTime);
		}

		m_poseEstimator.update(getGyroscopeRotation(),
				new SwerveModulePosition[] { m_frontLeftModule.getPosition(),
						m_frontRightModule.getPosition(),
						m_backLeftModule.getPosition(), m_backRightModule.getPosition() });
	}

	/**
	 * 
	 * @param pose      The estimated pose from vision
	 * @param timestamp Timestamp of the vision pose
	 */
	public void addVisionPoseEstimate(Pose2d pose, double timestamp) {

		if (pose == null) {
			return;
		}

		if (Utility.isWithinTolerance(pose.getRotation().getDegrees(), getYaw(), 15)) {

			if (m_currentTrajectory != null) {
				Pose2d trajPose = m_currentTrajectory.sample(timestamp - m_trajectoryStartSeconds).poseMeters;
				if (!Utility.isWithinTolerance(pose.getX(), trajPose.getX(),
						m_config.m_visionToleranceMeters) ||
						!Utility.isWithinTolerance(pose.getY(), trajPose.getY(),
								m_config.m_visionToleranceMeters)) {
					return;
				}
			}

			Logger.getInstance().recordOutput("filtered vision pose", pose);
			Pose2d currentPose = getPose();
			m_poseEstimator.addVisionMeasurement(pose, timestamp);
			Pose2d updatedPose = getPose();

			Logger.getInstance().recordOutput("Vision Correction (x, y, degrees)", new double[] {
					currentPose.getX() - updatedPose.getX(),
					currentPose.getY() - updatedPose.getY(),
					currentPose.getRotation().getDegrees() - updatedPose.getRotation().getDegrees() });
		}
	}

	public double getTilt(double yaw) {
		double angle = 0;
		if ((0 <= yaw && yaw < 45) || (315 <= yaw && yaw <= 360)) {
			angle = getRoll();
		} else if (45 <= yaw && yaw < 135) {
			angle = getPitch();
		} else if (135 <= yaw && yaw < 225) {
			angle = getRoll();
		} else if (225 <= yaw && yaw < 315) {
			angle = getPitch();
		}

		return angle;
	}

	public int getDirection(double yaw) {
		int direction = 0;
		if ((0 <= yaw && yaw < 45) || (315 <= yaw && yaw <= 360)) {
			direction = 1;
		} else if (45 <= yaw && yaw < 135) {
			direction = -1;
		} else if (135 <= yaw && yaw < 225) {
			direction = -1;
		} else if (225 <= yaw && yaw < 315) {
			direction = 1;
		}
		return direction;
	}

	private void updateFieldVelocity() {
		Translation2d linearFieldVelocity = new Translation2d(m_chassisSpeeds.vxMetersPerSecond,
				m_chassisSpeeds.vyMetersPerSecond)
				.rotateBy(getPose().getRotation());

		m_fieldVelocity = new Twist2d(
				linearFieldVelocity.getX(),
				linearFieldVelocity.getY(),
				Math.toRadians(m_pigeon.getRate()));
	}

	@Override
	public void periodic() {
		updatePoseEstimator();

		// SmartDashboard.putNumber("Angle", m_pigeon.getYaw());

		// SmartDashboard.putNumber("Yaw", m_pigeon.getYaw());
		// SmartDashboard.putNumber("Pose X",
		// m_poseEstimator.getEstimatedPosition().getX());
		// SmartDashboard.putNumber("Pose Y",
		// m_poseEstimator.getEstimatedPosition().getY());
		// SmartDashboard.putNumber("Pose Angle",
		// m_poseEstimator.getEstimatedPosition().getRotation().getDegrees());

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

		SwerveModuleState[] loggingSwerveStates = states;
		loggingSwerveStates[0] = m_frontLeftModule.getState();
		loggingSwerveStates[1] = m_frontRightModule.getState();
		loggingSwerveStates[2] = m_backLeftModule.getState();
		loggingSwerveStates[3] = m_backRightModule.getState();
		Logger.getInstance().recordOutput("Robot Pose", getPose());

		updateFieldVelocity();
	}

	public void printEncoderVals() {
		SmartDashboard.putNumber("front left encoder count",
				((TalonFX) (m_frontLeftModule.getDriveMotor())).getSelectedSensorPosition(0));

		SmartDashboard.putNumber("front right encoder count",
				((TalonFX) (m_frontRightModule.getDriveMotor())).getSelectedSensorPosition(0));
		SmartDashboard.putNumber("back left module encoder count",
				((TalonFX) (m_backLeftModule.getDriveMotor())).getSelectedSensorPosition(0));
		SmartDashboard.putNumber("back right module encoder count",
				((TalonFX) (m_backRightModule.getDriveMotor())).getSelectedSensorPosition(0));

	}
}

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.team2357.frc2023.subsystems;

import org.littletonrobotics.junction.Logger;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.sensors.WPI_Pigeon2;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.PathPlannerTrajectory.PathPlannerState;
import com.swervedrivespecialties.swervelib.AbsoluteEncoder;
import com.swervedrivespecialties.swervelib.Mk4iSwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SwerveModule;
import com.team2357.frc2023.Constants;
import com.team2357.frc2023.apriltag.AprilTagEstimate;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem.LIMELIGHT;
import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import com.team2357.lib.subsystems.LimelightSubsystem;
import com.team2357.lib.util.Utility;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.trajectory.Trajectory;
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

	// View of 3x3 section of the grid from robot POV
	public static enum COLUMN_TARGET {
		LEFT(Constants.DRIVE.LEFT_COL_X_ANGLE_SETPOINT, DualLimelightManagerSubsystem.LIMELIGHT.RIGHT, 0),
		MIDDLE(Constants.DRIVE.MID_COL_X_ANGLE_SETPOINT, DualLimelightManagerSubsystem.LIMELIGHT.LEFT, 1),
		RIGHT(Constants.DRIVE.RIGHT_COL_X_ANGLE_SETPOINT, DualLimelightManagerSubsystem.LIMELIGHT.LEFT, 2),
		NONE(Double.NaN, DualLimelightManagerSubsystem.LIMELIGHT.LEFT, -1);

		public final double setpoint;
		public final int index;
		public final DualLimelightManagerSubsystem.LIMELIGHT primaryLimelight;

		private COLUMN_TARGET(double setpoint, DualLimelightManagerSubsystem.LIMELIGHT primaryLimelight, int index) {
			this.setpoint = setpoint;
			this.primaryLimelight = primaryLimelight;
			this.index = index;
		}
	}

	public static COLUMN_TARGET getSetpoint(int val) {
		for (COLUMN_TARGET setpoint : COLUMN_TARGET.values()) {
			if (val == setpoint.index) {
				return setpoint;
			}
		}
		return COLUMN_TARGET.NONE;
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

	// Controller for robot movement along the y-axis
	private PIDController m_translateXController;

	// Controller for robot movement along the x-axis
	private PIDController m_translateYController;

	private COLUMN_TARGET m_targetColumn;
	// Whether or not the robot is seeking to get the primary limelight camera in
	// view
	private boolean m_isSeeking;

	// The current path the robot is running
	private PathPlannerTrajectory m_currentTrajectory;
	// Time when trajectory starts
	private double m_trajectoryStartSeconds;

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

		/**
		 * These are the maximum speeds that the targeting methods should achieve in
		 * meters per second
		 */
		public double m_translateXMaxSpeedMeters;

		public double m_translateYMaxSpeedMeters;

		/**
		 * These are the tolerances for the targeting methods in angle on the limelight
		 */
		public double m_translateYAngleTolerance;

		public double m_translateXAngleTolerance;

		/**
		 * These are the setpoints for the PID's that the translate commands use
		 */
		public double m_defaultXAngleSetpoint;
		public double m_defaultYAngleSetpoint;

		public double m_leftColXAngleSetpoint;
		public double m_midColXAngleSetpoint;
		public double m_rightColXAngleSetpoint;

		/*
		 * Open loop ramp rate for auto targeting
		 * In seconds from neutral to full throttle
		 */
		public double m_openLoopRampRateSeconds;

		// Trajectory PID controllers
		public PIDController m_xController;
		public PIDController m_yController;
		public PIDController m_thetaController;

		// Auto targeting PID controllers
		public PIDController m_rotateTargetController;
		public PIDController m_translateXController;
		public PIDController m_translateYController;

		public SimpleMotorFeedforward m_translationXFeedForward;
		public SimpleMotorFeedforward m_translationYFeedForward;

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

		m_targetColumn = COLUMN_TARGET.NONE;
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

		m_translateXController = m_config.m_translateXController;
		m_translateYController = m_config.m_translateYController;
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
		yaw += yaw < 0 ? 360 : 0;
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
		ChassisSpeeds chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
				x * m_config.m_maxVelocityMetersPerSecond,
				y * m_config.m_maxVelocityMetersPerSecond,
				rotation * m_config.m_maxAngularVelocityRadiansPerSecond,
				getGyroscopeRotation());

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

	public void addVisionPoseEstimate(AprilTagEstimate estimate) {

		if (estimate == null) {
			return;
		}

		Pose2d pose = estimate.getPose();
		addVisionPoseEstimate(pose, estimate.getTimeStamp());
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

		// System.out.println(
		// "Vision x: " + pose.getX() + ", Y: " + pose.getY() + ", Rot: " +
		// pose.getRotation().getDegrees());

		Pose2d robotPose = getPose();
		// System.out.println(
		// "robot x: " + robotPose.getX() + ", Y: " + robotPose.getY() + ", Rot: "
		// + robotPose.getRotation().getDegrees());

		double xError = robotPose.getX() - pose.getX();
		double yError = robotPose.getY() - pose.getY();

		// System.out.println("Error X: " + xError + " Y: " + yError);

		Logger.getInstance().recordOutput("Vision timestamp error", Timer.getFPGATimestamp() - timestamp);
		if (Utility.isWithinTolerance(pose.getRotation().getDegrees(), getYaw(), 15)) {

			if (m_currentTrajectory != null) {
				Pose2d trajPose = m_currentTrajectory.sample(timestamp - m_trajectoryStartSeconds).poseMeters;
				if (!Utility.isWithinTolerance(pose.getX(), trajPose.getX(),
						m_config.m_visionToleranceMeters) ||
						!Utility.isWithinTolerance(pose.getY(), trajPose.getY(),
								m_config.m_visionToleranceMeters)) {
					Logger.getInstance().recordOutput("Vision Pose", "Thrown");
					return;
				}
			}

			Logger.getInstance().recordOutput("Left limelight botpose filtered", pose);
			m_poseEstimator.addVisionMeasurement(pose, timestamp);
			Logger.getInstance().recordOutput("Vision Pose", "Added");

		} else {
			Logger.getInstance().recordOutput("Vision Pose", "Thrown");
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

	public void enableOpenLoopRamp() {
		TalonFX motor = (TalonFX) m_backRightModule.getDriveMotor();
		motor.configOpenloopRamp(m_config.m_openLoopRampRateSeconds);
		motor = (TalonFX) m_backLeftModule.getDriveMotor();
		motor.configOpenloopRamp(m_config.m_openLoopRampRateSeconds);
		motor = (TalonFX) m_frontLeftModule.getDriveMotor();
		motor.configOpenloopRamp(m_config.m_openLoopRampRateSeconds);
		motor = (TalonFX) m_frontRightModule.getDriveMotor();
		motor.configOpenloopRamp(m_config.m_openLoopRampRateSeconds);
	}

	public void disableOpenLoopRamp() {
		TalonFX motor = (TalonFX) m_backRightModule.getDriveMotor();
		motor.configOpenloopRamp(0);
		motor = (TalonFX) m_backLeftModule.getDriveMotor();
		motor.configOpenloopRamp(0);
		motor = (TalonFX) m_frontLeftModule.getDriveMotor();
		motor.configOpenloopRamp(0);
		motor = (TalonFX) m_frontRightModule.getDriveMotor();
		motor.configOpenloopRamp(0);
	}

	public boolean isTracking() {
		return m_targetColumn != COLUMN_TARGET.NONE;
	}

	public boolean isAtXTarget() {
		if (isTracking()) {
			return m_translateXController.atSetpoint();
		}
		return true;
	}

	public boolean isAtYTarget() {
		if (isTracking()) {
			return m_translateYController.atSetpoint();
		}
		return true;
	}

	public boolean hasTarget() {
		return DualLimelightManagerSubsystem.getInstance().validTargetExists();
	}

	public boolean isAtTarget() {
		// System.out.println(isAtXTarget() && isAtYTarget());
		// return isAtXTarget();
		// return isAtYTarget();
		return isAtXTarget() && isAtYTarget() && !m_isSeeking;
	}

	/**
	 * 
	 * @param column         The target column relative to the selected april tag
	 *                       (RIGHT, MIDDLE, LEFT)
	 * @param targetAprilTag The target AprilTag for the limelight to track,
	 *                       -1 will cause the limelight to track the primary in
	 *                       view AprilTag
	 */
	public void trackTarget(SwerveDriveSubsystem.COLUMN_TARGET column, int targetAprilTag) {
		// System.out.println("track target");
		setClosedLoopEnabled(true);

		DualLimelightManagerSubsystem limelightManager = DualLimelightManagerSubsystem.getInstance();
		m_targetColumn = column;

		limelightManager.setPrimary(m_targetColumn.primaryLimelight);
		limelightManager.setTargetAprilTag(targetAprilTag);
		limelightManager.setAprilTagPipelineActive();

		trackXTarget();
		trackYTarget(column.setpoint);
		enableOpenLoopRamp();
	}

	public void trackXTarget() {
		m_translateXController.reset();
		m_translateXController.setSetpoint(m_config.m_defaultYAngleSetpoint);
		m_translateXController.setTolerance(m_config.m_translateYAngleTolerance);
	}

	public void trackYTarget(double setpoint) {
		m_translateYController.reset();
		m_translateYController.setSetpoint(setpoint);
		m_translateYController.setTolerance(m_config.m_translateXAngleTolerance);
	}

	private void configureSeeking() {
		m_isSeeking = true;
		DualLimelightManagerSubsystem limelightManager = DualLimelightManagerSubsystem.getInstance();
		trackYTarget(limelightManager.getPrimaryTXSetpoint());
	}

	private void stopSeeking() {
		m_isSeeking = false;
		trackYTarget(m_targetColumn.setpoint);
	}

	public double calculateXMetersPerSecond() {
		double tY = DualLimelightManagerSubsystem.getInstance().getTY();

		double outputMetersPerSecond = m_translateXController
				.calculate(tY);
		outputMetersPerSecond *= -1; // Invert output
		outputMetersPerSecond += m_config.m_translationYFeedForward.calculate(tY);

		outputMetersPerSecond = MathUtil.clamp(outputMetersPerSecond, -m_config.m_translateXMaxSpeedMeters,
				m_config.m_translateXMaxSpeedMeters);
		return outputMetersPerSecond;
	}

	public double calculateYMetersPerSecond() {
		DualLimelightManagerSubsystem limelightManager = DualLimelightManagerSubsystem.getInstance();
		double errorAngle = m_isSeeking ? limelightManager.getSecondaryTX() : limelightManager.getPrimaryTX();

		double outputMetersPerSecond = m_translateYController.calculate(errorAngle);
		outputMetersPerSecond += -m_config.m_translationYFeedForward.calculate(errorAngle);
		outputMetersPerSecond = MathUtil.clamp(outputMetersPerSecond, -m_config.m_translateYMaxSpeedMeters,
				m_config.m_translateYMaxSpeedMeters);
		return outputMetersPerSecond;
	}

	public void trackingPeriodic() {
		DualLimelightManagerSubsystem limelightManager = DualLimelightManagerSubsystem.getInstance();
		// System.out.println(m_translateXController.getSetpoint());
		// System.out.println("TY "+limelight.getTY());
		if (!limelightManager.validTargetExists()) {
			DriverStation.reportWarning("NOT IN RANGE OF APRILTAG", null);
			return;
		}

		// System.out.println(isTracking());

		if (!limelightManager.validTargetExistsOnPrimary() && !m_isSeeking) { // If the primary limelight has no target,
																				// and has not been set to seek
			configureSeeking();
		} else if (limelightManager.validTargetExistsOnPrimary() && m_isSeeking) {
			stopSeeking();
		}

		double speed = calculateXMetersPerSecond();
		// System.out.println("speed: " + speed);
		// System.out.println("error: " +
		// (DualLimelightManagerSubsystem.getInstance().getTY() -
		// m_translateXController.getSetpoint()));
		// drive(new ChassisSpeeds(0, speed, 0));
		// drive(new ChassisSpeeds(speed, 0, 0));
		drive(new ChassisSpeeds(calculateXMetersPerSecond(), calculateYMetersPerSecond(), 0));
	}

	public void stopTracking() {
		setClosedLoopEnabled(false);
		DualLimelightManagerSubsystem.getInstance().setTargetAprilTag(-1);
		m_targetColumn = COLUMN_TARGET.NONE;
		m_isSeeking = false;

		// System.out.println("Ending Tracking -----------------");
		disableOpenLoopRamp();
		drive(0, 0, 0);
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

		Logger.getInstance().recordOutput("Swerve Setpoints", states);
		SwerveModuleState[] loggingSwerveStates = states;
		loggingSwerveStates[0] = m_frontLeftModule.getState();
		loggingSwerveStates[1] = m_frontRightModule.getState();
		loggingSwerveStates[2] = m_backLeftModule.getState();
		loggingSwerveStates[3] = m_backRightModule.getState();
		Logger.getInstance().recordOutput("Swerve Speed", loggingSwerveStates);
		Logger.getInstance().recordOutput("Robot Pose", getPose());

		// System.out.println("Is closed loop: "+isClosedLoopEnabled() + ", tracking: "
		// + isTracking());
		if (isClosedLoopEnabled() && isTracking()) {
			trackingPeriodic();
		}
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

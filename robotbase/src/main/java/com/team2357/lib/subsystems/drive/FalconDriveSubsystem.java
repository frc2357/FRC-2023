package com.team2357.lib.subsystems.drive;

import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;

public class FalconDriveSubsystem extends ClosedLoopSubsystem {

  private static FalconDriveSubsystem instance = null;

  public static FalconDriveSubsystem getInstance() {
    return instance;
  }

  // The gyro sensor
  private PigeonIMU m_gyro;
  private boolean m_isGyroReversed;

  // Odometry class for tracking robot pose
  private final DifferentialDriveOdometry m_odometry;

  private WPI_TalonFX m_leftFalconMaster;
  private WPI_TalonFX m_rightFalconMaster;

  private WPI_TalonFX[] m_rightFalconSlaves;
  private WPI_TalonFX[] m_leftFalconSlaves;

  private Configuration m_config;

  public static class Configuration {

    /**
     * Whether or not the left talon group needs to be inverted Value: boolean
     */
    public boolean m_isLeftInverted = false;

    /**
     * Whether or not the right talon group needs to be inverted Value: boolean
     */
    public boolean m_isRightInverted = false;

    /**
     * Whether or not the gyro is reversed Value: boolean
     */
    public boolean m_isGyroReversed = false;

    // The deadband of output percentage on the motor controller
    public double m_falconOutputDeadband = 0.001;

    /**
     *  Wheel circumference in meters
     */
    public double m_wheelCircumferenceMeters;

    /**
     * Encoder Clicks Per Rotation
     */
    public double m_encoderClicksPerRotation;

    // Turn sensitivity multiplier for velocity control
    public double m_turnSensitivity = 0.0;

    // Velocity PID constants
    public int m_gainsSlot = 0;
    public double m_velF = 0.0;
    public double m_velP = 0.0;
    public double m_velI = 0.0;
    public double m_velD = 0.0;

    public double m_nominalOutput = 0;
    public double m_peakOutput = 1;

    /**
     * This represents the native max velocity for the drive sensor over 100 ms
     * It should follow the following formula
     * maxRpm * encoderCpr / 600
     *
     */
    public double m_sensorUnitsMaxVelocity = 0;

    public int m_timeoutMs = 0;

    /**
     * Current config to handle drive motors
     * Current values a good baseline to use for a six-falcon drivebase
     */
    public SupplyCurrentLimitConfiguration m_currentConfig = new SupplyCurrentLimitConfiguration(
      true,
      0,
      0,
      0
    );

    public double m_openLoopRampRateSeconds = 0;
    public double m_closedLoopRampRateSeconds = 0;
  }

  /**
   *
   * @param leftTalonMaster
   * @param leftTalonSlaves
   * @param rightTalonMaster
   * @param rightTalonSlaves
   * @param leftEncoder
   * @param rightEncoder
   * @param gyro
   * @param encoderDistancePerPulse
   */
  public FalconDriveSubsystem(
    WPI_TalonFX leftFalconMaster,
    WPI_TalonFX[] leftFalconSlaves,
    WPI_TalonFX rightFalconMaster,
    WPI_TalonFX[] rightFalconSlaves,
    PigeonIMU gyro
  ) {
    m_leftFalconMaster = leftFalconMaster;
    m_rightFalconMaster = rightFalconMaster;
    m_leftFalconSlaves = leftFalconSlaves;
    m_rightFalconSlaves = rightFalconSlaves;

    instance = this;

    resetEncoders();
    m_gyro = gyro;
    m_gyro.configFactoryDefault();
    m_odometry =
      new DifferentialDriveOdometry(Rotation2d.fromDegrees(getHeading()), getLeftDistance(), getRightDistance());
    zeroHeading();
    resetOdometry(new Pose2d(0, 0, new Rotation2d(0)));
  }

  @Override
  public void periodic() {
    // Update the odometry in the periodic block
    m_odometry.update(
      Rotation2d.fromDegrees(getHeading()),
      getLeftDistance(),
      getRightDistance()
    );
    // System.out.print("Left encoder clicks: " + m_leftFalconMaster.getSelectedSensorPosition());
    // System.out.println(" Right encoder clicks: " + m_rightFalconMaster.getSelectedSensorPosition());
    //System.out.print("Left encoder distance: " + getLeftDistance());
    //System.out.println(" Right encoder distance: " + getRightDistance());
    //System.out.println(getHeading());
    //System.out.println("Pose: " + getPose().toString());
  }

  public void configure(Configuration config) {
    m_config = config;

    configureFalcons();

    m_isGyroReversed = m_config.m_isGyroReversed;

    // Velocity PID setup

    /* Config neutral deadband to be the smallest possible */
    m_leftFalconMaster.configNeutralDeadband(m_config.m_falconOutputDeadband);

    m_rightFalconMaster.configNeutralDeadband(m_config.m_falconOutputDeadband);

    /* Config sensor used for Primary PID [Velocity] */
    m_leftFalconMaster.configSelectedFeedbackSensor(
      TalonFXFeedbackDevice.IntegratedSensor,
      m_config.m_gainsSlot,
      m_config.m_timeoutMs
    );

    m_rightFalconMaster.configSelectedFeedbackSensor(
      TalonFXFeedbackDevice.IntegratedSensor,
      m_config.m_gainsSlot,
      m_config.m_timeoutMs
    );

    configFalconPID(m_leftFalconMaster);
    configFalconPID(m_rightFalconMaster);
  }

  private void configureFalcons() {
    m_leftFalconMaster.configSupplyCurrentLimit(m_config.m_currentConfig);
    m_leftFalconMaster.setInverted(m_config.m_isLeftInverted);
    m_rightFalconMaster.configSupplyCurrentLimit(m_config.m_currentConfig);
    m_rightFalconMaster.setInverted(m_config.m_isRightInverted);

    for (WPI_TalonFX slave : m_leftFalconSlaves) {
      slave.follow(m_leftFalconMaster, FollowerType.AuxOutput1);
      slave.follow(m_leftFalconMaster, FollowerType.PercentOutput);
      slave.configOpenloopRamp(m_config.m_openLoopRampRateSeconds);
      slave.configClosedloopRamp(m_config.m_closedLoopRampRateSeconds);
      slave.configSupplyCurrentLimit(m_config.m_currentConfig);
      slave.setInverted(m_config.m_isLeftInverted);
    }
    for (WPI_TalonFX slave : m_rightFalconSlaves) {
      slave.follow(m_rightFalconMaster, FollowerType.AuxOutput1);
      slave.follow(m_rightFalconMaster, FollowerType.PercentOutput);
      slave.configOpenloopRamp(m_config.m_openLoopRampRateSeconds);
      slave.configClosedloopRamp(m_config.m_closedLoopRampRateSeconds);
      slave.configSupplyCurrentLimit(m_config.m_currentConfig);
      slave.setInverted(m_config.m_isRightInverted);
    }
  }

  private void configFalconPID(WPI_TalonFX falcon) {
    falcon.configNominalOutputForward(
      m_config.m_nominalOutput,
      m_config.m_timeoutMs
    );
    falcon.configNominalOutputReverse(
      -m_config.m_nominalOutput,
      m_config.m_timeoutMs
    );
    falcon.configPeakOutputForward(m_config.m_peakOutput, m_config.m_timeoutMs);
    falcon.configPeakOutputReverse(
      -m_config.m_peakOutput,
      m_config.m_timeoutMs
    );

    falcon.config_kF(
      m_config.m_gainsSlot,
      m_config.m_velF,
      m_config.m_timeoutMs
    );
    falcon.config_kP(
      m_config.m_gainsSlot,
      m_config.m_velP,
      m_config.m_timeoutMs
    );
    falcon.config_kI(
      m_config.m_gainsSlot,
      m_config.m_velI,
      m_config.m_timeoutMs
    );
    falcon.config_kD(
      m_config.m_gainsSlot,
      m_config.m_velD,
      m_config.m_timeoutMs
    );
  }

  public void setBrake() {
    m_leftFalconMaster.setNeutralMode(NeutralMode.Brake);
    m_rightFalconMaster.setNeutralMode(NeutralMode.Brake);

    for (WPI_TalonFX slave : m_leftFalconSlaves) {
      slave.setNeutralMode(NeutralMode.Brake);
    }

    for (WPI_TalonFX slave : m_rightFalconSlaves) {
      slave.setNeutralMode(NeutralMode.Brake);
    }
  }

  public void setCoast() {
    m_leftFalconMaster.setNeutralMode(NeutralMode.Coast);
    m_rightFalconMaster.setNeutralMode(NeutralMode.Coast);

    for (WPI_TalonFX slave : m_leftFalconSlaves) {
      slave.setNeutralMode(NeutralMode.Coast);
    }

    for (WPI_TalonFX slave : m_rightFalconSlaves) {
      slave.setNeutralMode(NeutralMode.Coast);
    }
  }

  public void driveVelocity(double speed, double turn) {
    double speedSensorUnits = speed * m_config.m_sensorUnitsMaxVelocity;
    double turnSensorUnits = turn * m_config.m_sensorUnitsMaxVelocity;
    double leftSensorUnitsPer100Ms =
      speedSensorUnits - (turnSensorUnits * m_config.m_turnSensitivity);
    double rightSensorUnitsPer100Ms =
      speedSensorUnits + (turnSensorUnits * m_config.m_turnSensitivity);
    this.setVelocity(leftSensorUnitsPer100Ms, rightSensorUnitsPer100Ms);
  }

  protected void setVelocity(
    double leftSensorUnitsPer100Ms,
    double rightSensorUnitsPer100Ms
  ) {
    m_leftFalconMaster.set(
      TalonFXControlMode.Velocity,
      leftSensorUnitsPer100Ms
    );
    m_rightFalconMaster.set(
      TalonFXControlMode.Velocity,
      rightSensorUnitsPer100Ms
    );
  }

  public final void driveProportional(
    double speedProportion,
    double turnProportion
  ) {
    double leftProportion = speedProportion - turnProportion;
    double rightProportion = speedProportion + turnProportion;
    setProportional(leftProportion, rightProportion);
  }

  /**
   * Set the proportional speed of the drive base.
   *
   * @param leftProportion  Speed of left drive (-1.0 to 1.0, negative =
   *                        backwards)
   * @param rightProportion Speed of right drive (-1.0 to 1.0, negative =
   *                        backwards)
   */
  public void setProportional(double leftProportion, double rightProportion) {
    m_leftFalconMaster.set(leftProportion);
    m_rightFalconMaster.set(rightProportion);
  }

  public final void stop() {
    setProportional(0, 0);
  }

  public double getLeftDistance() {
    return clicksToMeters(m_leftFalconMaster.getSelectedSensorPosition());
  }

  public double getRightDistance() {
    return clicksToMeters(m_rightFalconMaster.getSelectedSensorPosition());
  }

  public double clicksToMeters(double clicks) {
    return (
      (clicks / m_config.m_encoderClicksPerRotation) *
      m_config.m_wheelCircumferenceMeters
    );
  }

  /**
   * Returns the currently-estimated pose of the robot.
   *
   * @return The pose.
   */
  public Pose2d getPose() {
    return m_odometry.getPoseMeters();
  }

  /**
   * Resets the odometry to the specified pose.
   *
   * @param pose The pose to which to set the odometry.
   */
  public void resetOdometry(Pose2d pose) {
    resetEncoders();
    m_odometry.resetPosition(Rotation2d.fromDegrees(getHeading()), getLeftDistance(), getRightDistance(), pose);
  }

  /**
   * Returns the current wheel speeds of the robot.
   *
   * @return The current wheel speeds.
   */
  public DifferentialDriveWheelSpeeds getWheelSpeeds() {
    return new DifferentialDriveWheelSpeeds(
      getVelocityLeftEncoder(),
      getVelocityRightEncoder()
    );
  }

  /**
   * Controls the left and right sides of the drive directly with voltages.
   *
   * @param leftVolts  the commanded left output
   * @param rightVolts the commanded right output
   */
  public void setTankDriveVolts(double leftVolts, double rightVolts) {
    // negative if motors are inverted.
    m_leftFalconMaster.setVoltage(leftVolts);
    m_rightFalconMaster.setVoltage(rightVolts);
  }

  /** Resets the drive encoders to currently read a position of 0. */
  public void resetEncoders() {
    m_leftFalconMaster.setSelectedSensorPosition(0);
    m_rightFalconMaster.setSelectedSensorPosition(0);
  }

  /**
   * Gets the average distance of the two encoders.
   *
   * @return the average of the two encoder readings
   */
  public double getAverageEncoderDistance() {
    return (getLeftDistance() + getRightDistance()) / 2.0;
  }

  public double getVelocityLeftEncoder() {
    double nativeSpeed = m_leftFalconMaster.getSelectedSensorVelocity();
    return clicksToMeters(nativeSpeed * 10);
  }

  /**
   *
   * @return The speed of the drive in meters per second
   */
  public double getVelocityRightEncoder() {
    double nativeSpeed = m_rightFalconMaster.getSelectedSensorVelocity();
    return clicksToMeters(nativeSpeed * 10);
  }

  /**
   * Zeroes the heading of the robot.
   */
  public void zeroHeading() {
    m_gyro.setYaw(0);
    m_gyro.setAccumZAngle(0);
  }

  /**
   * Returns the heading of the robot.
   *
   * @return the robot's heading in degrees, from -180 to 180
   */
  public double getHeading() {
    double[] ypr = getYawPitchAndRoll();
    return Math.IEEEremainder(ypr[0], 360) * (m_isGyroReversed ? -1.0 : 1.0);
  }

  /**
   * Returns the turn rate of the robot.
   *
   * @return The turn rate of the robot, in degrees per second
   */
  public double getTurnRate() {
    double[] ypr = getYawPitchAndRoll();
    return ypr[1] * (m_isGyroReversed ? -1.0 : 1.0);
  }

  public double[] getYawPitchAndRoll() {
    double[] ypr = new double[3];

    m_gyro.getYawPitchRoll(ypr);

    return ypr;
  }
}

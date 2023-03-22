/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team2357.lib.subsystems;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.DoubleArraySubscriber;
import edu.wpi.first.networktables.DoubleArrayTopic;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.IntegerPublisher;
import edu.wpi.first.networktables.IntegerSubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PubSubOption;

/**
 * Controls the limelight camera options.
 */
public class LimelightSubsystem extends ClosedLoopSubsystem {

  public static class Configuration {

    public int m_humanPipelineIndex = 2;

    public int m_targetingPipelineIndex = 0;

    public int m_aprilTagPipelineIndex = 1;
    public boolean m_isLimelightPrimaryStream = true;

    /** Angle of the Limelight axis from horizontal (degrees) */
    public double m_LimelightMountingAngle = 0;

    /** Height of the Limelight lens center from the floor (inches) */
    public double m_LimelightMountingHeightInches = 0;

    /** Default value to return if the camera can't be polled */
    public double m_DefaultReturnValue = 0;

    /** Tolerance in degrees for skew to be considered head on */
    public double m_HeadOnTolerance = 1e-4;

    /** Target width in inches */
    public double m_TargetWidth = 1;

    /** Target height in inches */
    public double m_TargetHeight = 1;

    public double m_targetHeightFromFloor = 0;
  }

  private static LimelightSubsystem instance = null;

  public static LimelightSubsystem getInstance() {
    return instance;
  }

  private Configuration m_Configuration = new Configuration();

  protected NetworkTable m_table;

  private DoublePublisher m_streamPub; 
  private DoublePublisher m_pipelinePub; 
  private DoubleSubscriber m_pipelineSub;
  private DoubleSubscriber m_TvSub;
  private DoubleSubscriber m_TxSub;
  private DoubleSubscriber m_TySub;
  private DoubleSubscriber m_TaSub;
  private DoubleSubscriber m_TsSub;
  private DoubleSubscriber m_ThorSub;
  private DoubleSubscriber m_TvertSub;
  private IntegerSubscriber m_Tid;
  private DoubleArraySubscriber m_limelightPoseInfoSub;

  /**
   * Sets the camera stream.
   * 
   * @param limelightName Name of the desired limelight's shuffleboard table
   */
  public LimelightSubsystem(String limelightName) {
    m_table = NetworkTableInstance
        .getDefault()
        .getTable(limelightName);

    m_streamPub = m_table.getDoubleTopic("stream").publish();
    m_pipelinePub = m_table.getDoubleTopic("pipeline").publish();
    m_pipelineSub = m_table.getDoubleTopic("pipeline").subscribe(Double.NaN);
    m_TvSub = m_table.getDoubleTopic("tv").subscribe(m_Configuration.m_DefaultReturnValue);
    m_TxSub = m_table.getDoubleTopic("tx").subscribe(m_Configuration.m_DefaultReturnValue);
    m_TySub = m_table.getDoubleTopic("ty").subscribe(m_Configuration.m_DefaultReturnValue);
    m_TaSub = m_table.getDoubleTopic("ta").subscribe(m_Configuration.m_DefaultReturnValue);
    m_TsSub = m_table.getDoubleTopic("ts").subscribe(m_Configuration.m_DefaultReturnValue);
    m_ThorSub = m_table.getDoubleTopic("thor").subscribe(m_Configuration.m_DefaultReturnValue);
    m_TvertSub = m_table.getDoubleTopic("tvert").subscribe(m_Configuration.m_DefaultReturnValue);
    m_Tid = m_table.getIntegerTopic("tid").subscribe(-1);

    DoubleArrayTopic limelightPoseInfo = m_table.getDoubleArrayTopic("botpose_targetspace");
    m_limelightPoseInfoSub = limelightPoseInfo.subscribe(null,
    PubSubOption.keepDuplicates(true));

    instance = this;
  }

  public void setConfiguration(Configuration configuration) {
    m_Configuration = configuration;

    setHumanPipelineActive();
    // setTargetingPipelineActive();
    setStream(configuration.m_isLimelightPrimaryStream);
  }

  public boolean validTargetExists() {
    return 0 < getTV();
  }

  /**
   * 
   * @param id Id of the desired april tag
   * @return If the limelight sees the april tag
   */
  public boolean validAprilTagTargetExists(int id) {
    return id == m_Tid.get();
  }

  public boolean isHumanPipelineActive() {
    return getPipeline() == m_Configuration.m_humanPipelineIndex;
  }

  protected void setPipeline(int index) {
    m_pipelinePub.set(index);
  }

  public void setHumanPipelineActive() {
    m_pipelinePub.set(m_Configuration.m_humanPipelineIndex);
  }

  public boolean isTargetingPipelineActive() {
    return getPipeline() == m_Configuration.m_targetingPipelineIndex;
  }

  public void setTargetingPipelineActive() {
    m_pipelinePub.set(m_Configuration.m_targetingPipelineIndex);
  }

  public boolean isAprilTagPipelineActive() {
    return getPipeline() == m_Configuration.m_aprilTagPipelineIndex;
  }

  public void setAprilTagPipelineActive() {
    m_pipelinePub.set(m_Configuration.m_aprilTagPipelineIndex);
  }

  private int getPipeline() {
    double value = m_pipelineSub.get();
    return (int) Math.round(value);
  }

  public void setStream(boolean isLimelightPrimary) {
    m_streamPub.set(isLimelightPrimary ? 1 : 2);
  }

  /**
   * Whether the camera has a valid target
   * 
   * @return 1 for true, 0 for false
   */
  public double getTV() {
    return m_TvSub.get();
  }

  /** Horizontal offset from crosshair to target (degrees) */
  public double getTX() {
    return m_TxSub.get();
  }

  /** Vertical offset from crosshair to target (degrees) */
  public double getTY() {
    return m_TySub.get();
  }

  /** Percent of image covered by target [0, 100] */
  public double getTA() {
    return m_TaSub.get();
  }

  /** Skew or rotation (degrees, [-90, 0]) */
  public double getTS() {
    return m_TsSub.get();
  }

  /** Horizontal sidelength of rough bounding box (0 - 320 pixels) */
  public double getTHOR() {
    return m_ThorSub.get();
  }

  /** Vertical sidelength of rough bounding box (0 - 320 pixels) */
  public double getTVERT() {
    return m_TvertSub.get();
  }

  /**
   * Skew of target in degrees. Positive values are to the left, negative to the
   * right
   */
  public double getSkew() {
    if (!validTargetExists()) {
      return Double.NaN;
    }

    double ts = getTS();
    if (ts < -45) {
      return ts + 90;
    } else {
      return ts;
    }
  }

  public boolean isHeadOn() {
    if (!validTargetExists()) {
      return false;
    }

    double skew = getSkew();
    return (-m_Configuration.m_HeadOnTolerance <= skew &&
        skew <= m_Configuration.m_HeadOnTolerance);
  }

  public boolean isToLeft() {
    if (!validTargetExists()) {
      return false;
    }

    return getSkew() > m_Configuration.m_HeadOnTolerance;
  }

  public boolean isToRight() {
    if (!validTargetExists()) {
      return false;
    }

    return getSkew() < m_Configuration.m_HeadOnTolerance;
  }

  public double getTargetRotationDegrees() {
    if (!validTargetExists()) {
      return Double.NaN;
    }

    if (isHeadOn()) {
      return 0.0;
    } else if (isToLeft()) {
      return -getRotationAngle();
    } else {
      return getRotationAngle();
    }
  }

  private double getRotationAngle() {
    if (!validTargetExists()) {
      return Double.NaN;
    }

    double proportion = getTHOR() / getTVERT();
    double factor = proportion *
        m_Configuration.m_TargetHeight /
        m_Configuration.m_TargetWidth;
    return 90.0 * (1 - factor);
  }

  public double getInchesFromTarget() {
    if (!validTargetExists()) {
      return Double.NaN;
    }

    double angleDegrees = Math.abs(getTY()) + m_Configuration.m_LimelightMountingAngle;

    double heightDifference = m_Configuration.m_LimelightMountingHeightInches -
        m_Configuration.m_targetHeightFromFloor;
    double distance = heightDifference / Math.tan(Math.toRadians(angleDegrees));

    return distance;
  }

  public Pose2d getLimelightPose2d() {
    double[] values = m_limelightPoseInfoSub.get();
    Translation2d t2d = new Translation2d(values[0], values[1]);
    Rotation2d r2d = new Rotation2d(SwerveDriveSubsystem.getInstance().getYaw());
    return new Pose2d(t2d, r2d);
  }
  /*
   * @Override
   * public void periodic() {
   * SmartDashboard.putNumber("Y", getTY());
   * }
   */
}

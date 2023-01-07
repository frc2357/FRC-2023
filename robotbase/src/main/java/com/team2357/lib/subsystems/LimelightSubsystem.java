/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team2357.lib.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Controls the limelight camera options.
 */
public class LimelightSubsystem extends ClosedLoopSubsystem {

  public static class Configuration {

    public int m_humanPipelineIndex = 0;

    public int m_targetingPipelineIndex = 0;

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

  protected NetworkTable m_Table = NetworkTableInstance
    .getDefault()
    .getTable("limelight");
  private NetworkTableEntry m_stream = m_Table.getEntry("stream");
  private NetworkTableEntry m_pipeline = m_Table.getEntry("pipeline");
  private NetworkTableEntry m_Tv = m_Table.getEntry("tv");
  private NetworkTableEntry m_Tx = m_Table.getEntry("tx");
  private NetworkTableEntry m_Ty = m_Table.getEntry("ty");
  private NetworkTableEntry m_Ta = m_Table.getEntry("ta");
  private NetworkTableEntry m_Ts = m_Table.getEntry("ts");
  private NetworkTableEntry m_Thor = m_Table.getEntry("thor");
  private NetworkTableEntry m_Tvert = m_Table.getEntry("tvert");

  private Configuration m_Configuration = new Configuration();

  /**
   * Sets the camera stream.
   *
   * @param isLimelightPrimary True if the limelight is primary, false if not.
   */
  public LimelightSubsystem() {
    instance = this;
  }

  public void setConfiguration(Configuration configuration) {
    m_Configuration = configuration;

    //  setHumanPipelineActive();
    setTargetingPipelineActive();
    setStream(configuration.m_isLimelightPrimaryStream);
  }

  public boolean validTargetExists() {
    return 0 < getTV();
  }

  public boolean isHumanPipelineActive() {
    return getPipeline() == m_Configuration.m_humanPipelineIndex;
  }

  public void setHumanPipelineActive() {
    m_pipeline.setDouble(m_Configuration.m_humanPipelineIndex);
  }

  public boolean isTargetingPipelineActive() {
    return getPipeline() == m_Configuration.m_targetingPipelineIndex;
  }

  public void setTargetingPipelineActive() {
    m_pipeline.setDouble(m_Configuration.m_targetingPipelineIndex);
  }

  private int getPipeline() {
    double value = m_pipeline.getDouble(Double.NaN);
    return (int) Math.round(value);
  }

  public void setStream(boolean isLimelightPrimary) {
    m_stream.setValue(isLimelightPrimary ? 1 : 2);
  }

  /**
   * Whether the camera has a valid target
   * @return 1 for true, 0 for false
   */
  public double getTV() {
    return m_Tv.getDouble(m_Configuration.m_DefaultReturnValue);
  }

  /** Horizontal offset from crosshair to target (degrees) */
  public double getTX() {
    return m_Tx.getDouble(m_Configuration.m_DefaultReturnValue);
  }

  /** Vertical offset from crosshair to target (degrees) */
  public double getTY() {
    return m_Ty.getDouble(m_Configuration.m_DefaultReturnValue);
  }

  /** Percent of image covered by target [0, 100] */
  public double getTA() {
    return m_Ta.getDouble(m_Configuration.m_DefaultReturnValue);
  }

  /** Skew or rotation (degrees, [-90, 0]) */
  public double getTS() {
    return m_Ts.getDouble(m_Configuration.m_DefaultReturnValue);
  }

  /** Horizontal sidelength of rough bounding box (0 - 320 pixels) */
  public double getTHOR() {
    return m_Thor.getDouble(m_Configuration.m_DefaultReturnValue);
  }

  /** Vertical sidelength of rough bounding box (0 - 320 pixels) */
  public double getTVERT() {
    return m_Tvert.getDouble(m_Configuration.m_DefaultReturnValue);
  }

  /** Skew of target in degrees. Positive values are to the left, negative to the right */
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
    return (
      -m_Configuration.m_HeadOnTolerance <= skew &&
      skew <= m_Configuration.m_HeadOnTolerance
    );
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
    double factor =
      proportion *
      m_Configuration.m_TargetHeight /
      m_Configuration.m_TargetWidth;
    return 90.0 * (1 - factor);
  }

  public double getInchesFromTarget() {
    if (!validTargetExists()) {
      return Double.NaN;
    }

    double angleDegrees =
      Math.abs(getTY()) + m_Configuration.m_LimelightMountingAngle;

    double heightDifference =
      m_Configuration.m_LimelightMountingHeightInches -
      m_Configuration.m_targetHeightFromFloor;
    double distance = heightDifference / Math.tan(Math.toRadians(angleDegrees));

    return distance;
  }
  /*
  @Override
  public void periodic() {
    SmartDashboard.putNumber("Y", getTY());
  }
  */
}

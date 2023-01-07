package com.team2357.lib.subsystems.drive;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

public class SingleSpeedFalconDriveSubsystem extends SkidSteerDriveSubsystem {

  // Left out of the abstract to use Falcon Specific
  protected WPI_TalonFX m_leftFalconMaster;
  protected WPI_TalonFX m_rightFalconMaster;

  public SingleSpeedFalconDriveSubsystem(
    WPI_TalonFX leftFalconMaster,
    WPI_TalonFX[] leftFalconSlaves,
    WPI_TalonFX rightFalconMaster,
    WPI_TalonFX[] rightFalconSlaves
  ) {
    super(
      new MotorControllerGroup(leftFalconMaster, leftFalconSlaves),
      new MotorControllerGroup(rightFalconMaster, rightFalconSlaves)
    );
    m_leftFalconMaster = leftFalconMaster;
    m_rightFalconMaster = rightFalconMaster;
  }

  @Override
  protected double getCurrentSpeedLeftClicksPerSecond() {
    return m_leftFalconMaster.getSelectedSensorVelocity();
  }

  @Override
  protected double getCurrentSpeedRightClicksPerSecond() {
    return m_rightFalconMaster.getSelectedSensorVelocity();
  }

  @Override
  protected void setVelocity(
    int leftClicksPerSecond,
    double rightClicksPerSecond
  ) {
    // TODO implement PID Loop
  }

  public WPI_TalonFX getLeftFalconMaster() {
    return m_leftFalconMaster;
  }

  public WPI_TalonFX getRightFalconMaster() {
    return m_rightFalconMaster;
  }
}

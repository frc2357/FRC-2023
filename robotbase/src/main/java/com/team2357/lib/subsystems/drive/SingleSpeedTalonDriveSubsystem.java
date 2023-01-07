package com.team2357.lib.subsystems.drive;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

public class SingleSpeedTalonDriveSubsystem extends SkidSteerDriveSubsystem {

  private static SingleSpeedTalonDriveSubsystem instance = null;

  public static SingleSpeedTalonDriveSubsystem getInstance() {
    return instance;
  }

  // Left out of the abstract to use Talon Specific methods
  protected WPI_TalonSRX m_leftTalonMaster;
  protected WPI_TalonSRX m_rightTalonMaster;

  public SingleSpeedTalonDriveSubsystem(
    WPI_TalonSRX[] leftTalons,
    WPI_TalonSRX[] rightTalons
  ) {
    super(
      new MotorControllerGroup(leftTalons),
      new MotorControllerGroup(rightTalons)
    );
    instance = this;
    m_leftTalonMaster = leftTalons[0];
    m_rightTalonMaster = rightTalons[0];
  }

  @Override
  protected double getCurrentSpeedLeftClicksPerSecond() {
    double rawSensorUnitsPer100ms = m_leftTalonMaster.getSelectedSensorVelocity(); // returns selected sensor (in
    // raw sensor units) per 100ms
    double rawSensorUnitsPerSec = rawSensorUnitsPer100ms / 10;
    // TODO: further changes?
    return rawSensorUnitsPerSec;
  }

  @Override
  protected double getCurrentSpeedRightClicksPerSecond() {
    double rawSensorUnitsPer100ms = m_rightTalonMaster.getSelectedSensorVelocity(); // returns selected sensor (in
    // raw sensor units) per 100ms
    double rawSensorUnitsPerSec = rawSensorUnitsPer100ms / 10;
    // TODO: further changes?
    return rawSensorUnitsPerSec;
  }

  @Override
  protected void setVelocity(
    int leftClicksPerSecond,
    double rightClicksPerSecond
  ) {
    // TODO implement PID Loop
  }
}

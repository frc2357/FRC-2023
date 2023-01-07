package com.team2357.lib.subsystems.drive;

import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import com.team2357.lib.util.RobotMath;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

/**
 * Base class for any kind of "Skid Steer" drive base. This makes assumptions
 * that we will use encoders and velocity drive. However, this makes zero
 * assumptions about hardware or implementation of such.
 */
public abstract class SkidSteerDriveSubsystem extends ClosedLoopSubsystem {

  private double m_wheelbaseWidthInches = 0;
  private int m_clicksPerInch = 0;
  private int m_maxSpeedClicksPerSecond = 0;
  protected boolean m_isLeftInverted = false;
  protected boolean m_isRightInverted = false;

  public static class Configuration {

    /**
     * The distance between the drive wheels. Measure from the center of the left
     * wheels to the center of the right. Value: double (positive)
     */
    public double m_wheelbaseWidthInches = 6;

    /**
     * The number of encoder clicks per inch of drive base travel. Calculated with
     * gear ratios and wheel diameter. Verify with measurement of working robot
     * travel for best accuracy. Value: int (positive)
     */
    public int m_clicksPerInch = 5;

    /**
     * The number of encoder clicks per minute when running at max speed. Measure
     * top running speed with no load (up on blocks) Value: int (positive)
     */
    public int m_maxSpeedClicksPerSecond = 1;

    /**
     * Whether or not the left talon group needs to be inverted Value: boolean
     */
    public boolean m_isLeftInverted = false;

    /**
     * Whether or not the right talon group needs to be inverted Value: boolean
     */
    public boolean m_isRightInverted = false;
  }

  protected MotorControllerGroup m_leftControllers;
  protected MotorControllerGroup m_rightControllers;

  public SkidSteerDriveSubsystem(
    MotorControllerGroup leftControllers,
    MotorControllerGroup rightcontrollers
  ) {
    m_leftControllers = leftControllers;
    m_rightControllers = rightcontrollers;
  }

  public void configure(Configuration config) {
    m_wheelbaseWidthInches = config.m_wheelbaseWidthInches;

    m_clicksPerInch = config.m_clicksPerInch;

    m_maxSpeedClicksPerSecond = config.m_maxSpeedClicksPerSecond;
    m_isLeftInverted = config.m_isLeftInverted;
    m_leftControllers.setInverted(config.m_isLeftInverted);
    m_isRightInverted = config.m_isRightInverted;
    m_rightControllers.setInverted(config.m_isRightInverted);
  }

  public final double getMaxSpeedInchesPerSecond() {
    return m_maxSpeedClicksPerSecond / m_clicksPerInch;
  }

  public final double getCurrentSpeedInchesPerSecond() {
    double leftSpeedInches =
      getCurrentSpeedLeftClicksPerSecond() / m_clicksPerInch;
    double rightSpeedInches =
      getCurrentSpeedRightClicksPerSecond() / m_clicksPerInch;
    return (rightSpeedInches + leftSpeedInches) / 2;
  }

  public final double getCurrentTurnDegreesPerSecond() {
    double leftSpeedInches =
      getCurrentSpeedLeftClicksPerSecond() / m_clicksPerInch;
    double rightSpeedInches =
      getCurrentSpeedRightClicksPerSecond() / m_clicksPerInch;
    double rotationInches = (leftSpeedInches - rightSpeedInches) / 2.0;
    return RobotMath.turnInchesToDegrees(
      rotationInches,
      m_wheelbaseWidthInches
    );
  }

  public final void stop() {
    setProportional(0, 0);
  }

  public final void driveProportional(
    double speedProportion,
    double turnProportion
  ) {
    double leftProportion = speedProportion - turnProportion;
    double rightProportion = speedProportion + turnProportion;
    setProportional(leftProportion, rightProportion);
  }

  public void driveVelocity(
    double speedInchesPerSecond,
    double turnDegreesPerSecond
  ) {
    if (this.isClosedLoopEnabled()) {
      System.err.println(
        "Drive: Cannot driveVelocity while failsafe is active!"
      );
      return;
    }

    double turnInchesPerSecond = RobotMath.turnDegreesToInches(
      turnDegreesPerSecond,
      m_wheelbaseWidthInches
    );
    int turnClicksPerSecond = (int) Math.round(
      turnInchesPerSecond * m_clicksPerInch
    );
    int speedClicksPerSecond = (int) Math.round(
      speedInchesPerSecond * m_clicksPerInch
    );

    int leftClicksPerSecond = speedClicksPerSecond + turnClicksPerSecond;
    int rightClicksPerSecond = speedClicksPerSecond - turnClicksPerSecond;

    setVelocity(leftClicksPerSecond, rightClicksPerSecond);
  }

  /**
   * Gets the current speed of the left drive system
   *
   * @return Speed in clicks per second (negative = backwards)
   */
  protected abstract double getCurrentSpeedLeftClicksPerSecond();

  /**
   * Gets the current speed of the right drive system
   *
   * @return Speed in clicks per second (negative = backwards)
   */
  protected abstract double getCurrentSpeedRightClicksPerSecond();

  /**
   * Set the proportional speed of the drive base.
   *
   * @param leftProportion  Speed of left drive (-1.0 to 1.0, negative =
   *                        backwards)
   * @param rightProportion Speed of right drive (-1.0 to 1.0, negative =
   *                        backwards)
   */
  public void setProportional(double leftProportion, double rightProportion) {
    m_leftControllers.set(leftProportion);
    m_rightControllers.set(rightProportion);
  }

  /**
   * Set the velocity speed of the drive base.
   *
   * @param leftClicksPerSecond  Speed of left drive in clicks per second
   *                             (negative = backwards)
   * @param rightClicksPerSecond Speed of right drive in clicks per second
   *                             (negative = backwards)
   */
  protected abstract void setVelocity(
    int leftClicksPerSecond,
    double rightClicksPerSecond
  );
}

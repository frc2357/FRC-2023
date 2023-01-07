package com.team2357.lib.controllers;

import com.team2357.lib.commands.InvertDriveCommand;
import com.team2357.lib.util.Utility;
import com.team2357.lib.util.XboxRaw;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * These extend {@link DriverControls} so these are the Driver's controls,
 * adapted to support the
 * {@link InvertDriveCommand}.
 *
 * @category Drive
 */
public class InvertDriveControls extends DriverControls {

  public final JoystickButton m_invertButton;
  private boolean m_isInverted = false;

  public InvertDriveControls(XboxController controller, double deadband) {
    super(controller, deadband);
    m_invertButton = new JoystickButton(controller, XboxRaw.A.value);
  }

  /**
   * Changes the value of m_isToggled from true to false or vice versa
   */
  public void invert() {
    m_isInverted = !m_isInverted;
  }

  @Override
  public double getSpeed() {
    double speed = super.getSpeed();
    speed = inputCurve(speed, 2);
    return m_isInverted ? speed : -speed;
  }

  @Override
  public double getTurn() {
    double turn = super.getTurn();
    turn = -inputCurve(super.getTurn(), 4);
    return Utility.clamp(turn, -.7, .7);
  }

  /**
   * Calculates the curve of a turn
   *
   * @param input       The turn value from a joystick
   * @param curveFactor The factor to determine how aggressive the turn is
   * @return The turn value to be used
   */
  public double inputCurve(double input, int curveFactor) {
    return Math.signum(input) * Math.abs(Math.pow(input, curveFactor));
  }
}

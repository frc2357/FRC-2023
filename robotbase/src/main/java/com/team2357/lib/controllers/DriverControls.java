package com.team2357.lib.controllers;

import com.team2357.lib.util.Utility;
import edu.wpi.first.wpilibj.XboxController;

/**
 * Set of control inputs for the Driver position.
 */
public class DriverControls implements ArcadeAxisInput {

  protected XboxController m_controller;
  private double m_deadband;

  /**
   * Create driver controls
   * @param controller The Xbox Controller used for the driver
   * @param deadband The deadband used for all drive axis output (typically 0.1 or less)
   */
  public DriverControls(XboxController controller, double deadband) {
    m_controller = controller;
    m_deadband = deadband;
  }

  @Override
  public double getSpeed() {
    double value = m_controller.getLeftY();

    return Utility.deadband(value, m_deadband);
  }

  @Override
  public double getTurn() {
    double value = m_controller.getRightX();
    return Utility.deadband(value, m_deadband);
  }
}

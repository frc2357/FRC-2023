package com.team2357.lib.sensors;

import edu.wpi.first.util.sendable.SendableBuilder;

/**
 * Base class for a Limit Sensor
 * This doesn't assume a hardware implementation, but handles the rest.
 */
public abstract class LimitSensor extends Sensor {

  /**
   * Checks if this limit sensor is active.
   * @return boolean True if sensor is indicating mechanism is at limit
   */
  public abstract boolean isAtLimit();

  @Override
  public void initSendable(SendableBuilder sendableBuilder) {
    sendableBuilder.addBooleanProperty("At Limit", this::isAtLimit, null);
  }
}

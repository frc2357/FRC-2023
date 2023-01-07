package com.team2357.lib.sensors;

import edu.wpi.first.wpilibj.DigitalInput;

public class DigitalInputLimitSwitch extends LimitSensor {

  private DigitalInput digitalInput;
  private boolean inverted;

  public DigitalInputLimitSwitch(DigitalInput digitalInput) {
    this(digitalInput, false);
  }

  public DigitalInputLimitSwitch(DigitalInput digitalInput, boolean inverted) {
    this.digitalInput = digitalInput;
    this.inverted = inverted;
  }

  @Override
  public boolean isAtLimit() {
    boolean value = digitalInput.get();
    return (inverted ? !value : value);
  }
}

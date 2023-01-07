package com.team2357.lib.triggers;

import com.team2357.lib.util.DPadValue;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class DPadTrigger extends Trigger {

  XboxController controller;
  DPadValue triggerValue;
  DPadValue lastValue;

  public DPadTrigger(XboxController controller, DPadValue triggerValue) {
    this.triggerValue = triggerValue;
    this.controller = controller;
  }

  @Override
  public boolean getAsBoolean() {
    DPadValue dPadValue = DPadValue.fromPOV(controller.getPOV(0));

    if (dPadValue != lastValue) {
      lastValue = dPadValue;
      return (dPadValue == triggerValue);
    }
    return false;
  }
}

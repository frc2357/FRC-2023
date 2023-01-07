package com.team2357.lib.triggers;

import com.team2357.lib.util.ControllerAxis;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Axis;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class AxisThresholdTrigger extends Trigger {

  private double triggerThreshold;
  private ControllerAxis controllerAxis;

  public AxisThresholdTrigger(
      XboxController controller,
      Axis axis,
      double triggerThreshold) {
    this.triggerThreshold = triggerThreshold;
    this.controllerAxis = () -> {
      switch (axis) {
        case kLeftX:
          return controller.getLeftX();
        case kLeftY:
          return controller.getLeftY();
        case kLeftTrigger:
          return controller.getLeftTriggerAxis();
        case kRightTrigger:
          return controller.getRightTriggerAxis();
        case kRightX:
          return controller.getRightX();
        case kRightY:
          return controller.getRightY();
      }
      return 0;
    };
  }

  @Override
  public boolean getAsBoolean() {
    return (controllerAxis.getAxisValue() > triggerThreshold);
  }
}

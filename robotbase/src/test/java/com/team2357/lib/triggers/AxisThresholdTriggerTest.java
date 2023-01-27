package com.team2357.lib.triggers;

import static org.junit.jupiter.api.Assertions.assertEquals;import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Axis;

public class AxisThresholdTriggerTest {

  @Test
  public void testGetReturnsTrueWhenHeld() {
    XboxController controller = mock(XboxController.class);

    when(controller.getRawAxis(Axis.kRightTrigger.value)).thenReturn(1.0);

    AxisThresholdTrigger trigger = new AxisThresholdTrigger(
      controller,
      Axis.kRightTrigger,
      .25
    );

    assertEquals(trigger.getAsBoolean(), true);
  }

  @Test
  public void testGetReturnsFalseWhenOpen() {
    XboxController controller = mock(XboxController.class);

    when(controller.getRightTriggerAxis()).thenReturn(0.0);

    AxisThresholdTrigger trigger = new AxisThresholdTrigger(
      controller,
      Axis.kRightTrigger,
      .25
    );

    assertEquals(trigger.getAsBoolean(), false);
  }

  @Test
  public void testGetReturnsFalseWhenJustUnderThreshold() {
    XboxController controller = mock(XboxController.class);

    when(controller.getRightTriggerAxis()).thenReturn(0.249);

    AxisThresholdTrigger trigger = new AxisThresholdTrigger(
      controller,
      Axis.kRightTrigger,
      .25
    );

    assertEquals(trigger.getAsBoolean(), false);
  }

  @Test
  public void testGetReturnsTrueWhenJustAboveThreshold() {
    XboxController controller = mock(XboxController.class);

    when(controller.getRawAxis(Axis.kRightTrigger.value)).thenReturn(0.251);

    AxisThresholdTrigger trigger = new AxisThresholdTrigger(
      controller,
      Axis.kRightTrigger,
      .25
    );

    assertEquals(trigger.getAsBoolean(), true);
  }

  @Test
  public void testGetReturnsFalseWhenAtThreshold() {
    XboxController controller = mock(XboxController.class);

    when(controller.getRightTriggerAxis()).thenReturn(0.25);

    AxisThresholdTrigger trigger = new AxisThresholdTrigger(
      controller,
      Axis.kRightTrigger,
      .25
    );

    assertEquals(trigger.getAsBoolean(), false);
  }
}

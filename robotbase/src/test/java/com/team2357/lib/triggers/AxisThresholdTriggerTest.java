package com.team2357.lib.triggers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Axis;
import org.junit.Test;

public class AxisThresholdTriggerTest {

  @Test
  public void testGetReturnsTrueWhenHeld() {
    XboxController controller = mock(XboxController.class);

    when(controller.getRightTriggerAxis()).thenReturn(1.0);

    AxisThresholdTrigger trigger = new AxisThresholdTrigger(
      controller,
      Axis.kRightTrigger,
      .25
    );

    assertEquals(trigger.get(), true);
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

    assertEquals(trigger.get(), false);
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

    assertEquals(trigger.get(), false);
  }

  @Test
  public void testGetReturnsTrueWhenJustAboveThreshold() {
    XboxController controller = mock(XboxController.class);

    when(controller.getRightTriggerAxis()).thenReturn(0.251);

    AxisThresholdTrigger trigger = new AxisThresholdTrigger(
      controller,
      Axis.kRightTrigger,
      .25
    );

    assertEquals(trigger.get(), true);
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

    assertEquals(trigger.get(), false);
  }
}

package com.team2357.lib.triggers;

import static org.junit.jupiter.api.Assertions.assertEquals;import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.team2357.lib.util.DPadValue;

import edu.wpi.first.wpilibj.XboxController;

public class DPadTriggerTest {

  @Test
  public void testDPadValueNotEqualToLastValue() {
    XboxController controller = mock(XboxController.class);
    DPadValue triggerValue = DPadValue.fromPOV(1);

    when(controller.getPOV()).thenReturn(1);

    DPadTrigger Dpad = new DPadTrigger(controller, triggerValue);

    assertEquals(Dpad.getAsBoolean(), true);
  }

  @Test
  public void testDpadValueEqualToLastValue() {
    XboxController controller = mock(XboxController.class);
    DPadValue triggerValue = DPadValue.fromPOV(-1);

    when(controller.getPOV()).thenReturn(-1);

    DPadTrigger Dpad = new DPadTrigger(controller, triggerValue);

    assertEquals(Dpad.getAsBoolean(), false);
  }
}

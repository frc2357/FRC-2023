package com.team2357.lib.triggers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.team2357.lib.util.DPadValue;
import edu.wpi.first.wpilibj.XboxController;
import org.junit.Test;

public class DPadTriggerTest {

  @Test
  public void testDPadValueNotEqualToLastValue() {
    XboxController controller = mock(XboxController.class);
    DPadValue triggerValue = DPadValue.fromPOV(1);

    when(controller.getPOV()).thenReturn(1);

    DPadTrigger Dpad = new DPadTrigger(controller, triggerValue);

    assertEquals(Dpad.get(), true);
  }

  @Test
  public void testDpadValueEqualToLastValue() {
    XboxController controller = mock(XboxController.class);
    DPadValue triggerValue = DPadValue.fromPOV(-1);

    when(controller.getPOV()).thenReturn(-1);

    DPadTrigger Dpad = new DPadTrigger(controller, triggerValue);

    assertEquals(Dpad.get(), false);
  }
}

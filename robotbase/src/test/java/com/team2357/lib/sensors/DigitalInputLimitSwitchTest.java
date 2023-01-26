package com.team2357.lib.sensors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.wpi.first.wpilibj.DigitalInput;
import org.junit.Test;

public class DigitalInputLimitSwitchTest {

  @Test
  public void testDigitalInputGet() {
    DigitalInput input = mock(DigitalInput.class);

    DigitalInputLimitSwitch limit = new DigitalInputLimitSwitch(input);

    when(input.get()).thenReturn(true);
    assertTrue(limit.isAtLimit());

    when(input.get()).thenReturn(false);
    assertFalse(limit.isAtLimit());
  }

  @Test
  public void testDigitalInputInverted() {
    DigitalInput input = mock(DigitalInput.class);

    DigitalInputLimitSwitch limit = new DigitalInputLimitSwitch(input, true);

    when(input.get()).thenReturn(true);
    assertFalse(limit.isAtLimit());

    when(input.get()).thenReturn(false);
    assertTrue(limit.isAtLimit());
  }
}

package com.team2357.lib.sensors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import edu.wpi.first.wpilibj.DigitalInput;

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

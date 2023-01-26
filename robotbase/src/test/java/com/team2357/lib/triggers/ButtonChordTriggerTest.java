package com.team2357.lib.triggers;

import static org.junit.jupiter.api.Assertions.assertEquals;import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class ButtonChordTriggerTest {

  @Test
  public void testGetReturnsTrue() {
    JoystickButton button1 = mock(JoystickButton.class);
    JoystickButton button2 = mock(JoystickButton.class);
    JoystickButton button3 = mock(JoystickButton.class);

    when(button1.getAsBoolean()).thenReturn(true);
    when(button2.getAsBoolean()).thenReturn(true);
    when(button3.getAsBoolean()).thenReturn(true);

    JoystickButton[] buttons = { button1, button2, button3 };

    ButtonChordTrigger trigger = new ButtonChordTrigger(buttons);

    assertEquals(trigger.getAsBoolean(), true);
  }

  @Test
  public void testGetReturnsFalseWithOne() {
    JoystickButton button1 = mock(JoystickButton.class);
    JoystickButton button2 = mock(JoystickButton.class);
    JoystickButton button3 = mock(JoystickButton.class);

    when(button1.getAsBoolean()).thenReturn(false);
    when(button2.getAsBoolean()).thenReturn(true);
    when(button3.getAsBoolean()).thenReturn(true);

    JoystickButton[] buttons = { button1, button2, button3 };

    ButtonChordTrigger trigger = new ButtonChordTrigger(buttons);

    assertEquals(trigger.getAsBoolean(), false);
  }

  @Test
  public void testGetReturnsFalseWithAll() {
    JoystickButton button1 = mock(JoystickButton.class);
    JoystickButton button2 = mock(JoystickButton.class);
    JoystickButton button3 = mock(JoystickButton.class);

    when(button1.getAsBoolean()).thenReturn(false);
    when(button2.getAsBoolean()).thenReturn(false);
    when(button3.getAsBoolean()).thenReturn(false);

    JoystickButton[] buttons = { button1, button2, button3 };

    ButtonChordTrigger trigger = new ButtonChordTrigger(buttons);

    assertEquals(trigger.getAsBoolean(), false);
  }
}

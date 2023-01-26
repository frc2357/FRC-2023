package com.team2357.lib.triggers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import org.junit.Test;

public class ButtonChordTriggerTest {

  @Test
  public void testGetReturnsTrue() {
    JoystickButton button1 = mock(JoystickButton.class);
    JoystickButton button2 = mock(JoystickButton.class);
    JoystickButton button3 = mock(JoystickButton.class);

    when(button1.get()).thenReturn(true);
    when(button2.get()).thenReturn(true);
    when(button3.get()).thenReturn(true);

    JoystickButton[] buttons = { button1, button2, button3 };

    ButtonChordTrigger trigger = new ButtonChordTrigger(buttons);

    assertEquals(trigger.get(), true);
  }

  @Test
  public void testGetReturnsFalseWithOne() {
    JoystickButton button1 = mock(JoystickButton.class);
    JoystickButton button2 = mock(JoystickButton.class);
    JoystickButton button3 = mock(JoystickButton.class);

    when(button1.get()).thenReturn(false);
    when(button2.get()).thenReturn(true);
    when(button3.get()).thenReturn(true);

    JoystickButton[] buttons = { button1, button2, button3 };

    ButtonChordTrigger trigger = new ButtonChordTrigger(buttons);

    assertEquals(trigger.get(), false);
  }

  @Test
  public void testGetReturnsFalseWithAll() {
    JoystickButton button1 = mock(JoystickButton.class);
    JoystickButton button2 = mock(JoystickButton.class);
    JoystickButton button3 = mock(JoystickButton.class);

    when(button1.get()).thenReturn(false);
    when(button2.get()).thenReturn(false);
    when(button3.get()).thenReturn(false);

    JoystickButton[] buttons = { button1, button2, button3 };

    ButtonChordTrigger trigger = new ButtonChordTrigger(buttons);

    assertEquals(trigger.get(), false);
  }
}

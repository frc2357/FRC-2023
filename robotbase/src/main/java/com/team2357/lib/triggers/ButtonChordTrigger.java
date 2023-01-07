package com.team2357.lib.triggers;

import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class ButtonChordTrigger extends Trigger {

  private JoystickButton[] buttons;

  public ButtonChordTrigger(JoystickButton[] buttons) {
    this.buttons = buttons;
  }

  @Override
  public boolean getAsBoolean() {
    for (JoystickButton b : buttons) {
      if (!b.getAsBoolean()) {
        return false;
      }
    }
    // All buttons in chord are pressed!
    return true;
  }
}

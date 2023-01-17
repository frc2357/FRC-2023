package com.team2357.lib.triggers;

import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class ButtonChordTrigger extends Trigger {

  public ButtonChordTrigger(JoystickButton[] buttons) {
    super(() -> {
      for (JoystickButton b : buttons) {
        if (!b.getAsBoolean()) {
          return false;
        }
      }
      // All buttons in chord are pressed!
      return true;
    });
  }
}

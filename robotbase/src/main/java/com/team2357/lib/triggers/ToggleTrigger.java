package com.team2357.lib.triggers;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * A Shuffleboard toggle button trigger.
 *
 * This keeps track of when the button state changes for
 * events that should trigger on an edge.
 */
public class ToggleTrigger extends Trigger {

  public ToggleTrigger(GenericEntry entry) {
    super(() -> entry.getBoolean(false));
  }
}

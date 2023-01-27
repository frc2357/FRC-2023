package com.team2357.lib.triggers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class ToggleTriggerTest {

  @Test
  public void testGetReturnsTrue() {
    ShuffleboardTab tab = Shuffleboard.getTab("Test");
    GenericEntry entry = tab
      .add("Get Returns True", true)
      .withWidget(BuiltInWidgets.kToggleButton)
      .getEntry();

    ToggleTrigger trigger = new ToggleTrigger(entry);

    assertEquals(trigger.getAsBoolean(), true);
  }

  @Test
  public void testGetReturnsFalseAfterClick() {
    ShuffleboardTab tab = Shuffleboard.getTab("Test");
    GenericEntry entry = tab
      .add("Get Returns False After Click", true)
      .withWidget(BuiltInWidgets.kToggleButton)
      .getEntry();

    ToggleTrigger trigger = new ToggleTrigger(entry);

    entry.setBoolean(false);

    assertEquals(trigger.getAsBoolean(), false);
  }
}

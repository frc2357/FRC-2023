package com.team2357.lib.triggers;

import static org.junit.Assert.*;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import org.junit.Test;

public class ToggleTriggerTest {

  @Test
  public void testGetReturnsTrue() {
    ShuffleboardTab tab = Shuffleboard.getTab("Test");
    NetworkTableEntry entry = tab
      .add("Get Returns True", true)
      .withWidget(BuiltInWidgets.kToggleButton)
      .getEntry();

    ToggleTrigger trigger = new ToggleTrigger(entry);

    assertEquals(trigger.get(), true);
  }

  @Test
  public void testGetReturnsFalseAfterClick() {
    ShuffleboardTab tab = Shuffleboard.getTab("Test");
    NetworkTableEntry entry = tab
      .add("Get Returns False After Click", true)
      .withWidget(BuiltInWidgets.kToggleButton)
      .getEntry();

    ToggleTrigger trigger = new ToggleTrigger(entry);

    entry.setBoolean(false);

    assertEquals(trigger.get(), false);
  }
}

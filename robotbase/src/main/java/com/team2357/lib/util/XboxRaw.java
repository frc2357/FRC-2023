package com.team2357.lib.util;

// Constants for the raw button and axis values of the XBox controller
public enum XboxRaw {
  A(1),
  B(2),
  X(3),
  Y(4),

  BumperLeft(5),
  BumperRight(6),

  Back(7),
  Start(8),

  StickPressLeft(9),
  StickPressRight(10),

  StickLeftX(0),
  StickLeftY(1),

  StickRightX(4),
  StickRightY(5),

  TriggerLeft(2),
  TriggerRight(3);

  public final int value;

  XboxRaw(int aValue) {
    value = aValue;
  }
}

package com.team2357.lib.util;

public enum DPadValue {
  Up(0),
  Right(1),
  Down(2),
  Left(3),
  Unpressed(4);

  public final int value;

  // Convert a POV value from GenericHID to the enum.
  // Snaps to a quadrant in case a diagonal is pressed.
  public static DPadValue fromPOV(int povVal) {
    if (povVal < 0) {
      return Unpressed;
    }
    if (povVal < 45 || povVal >= 315) {
      return Up;
    }
    if (povVal < 135) {
      return Right;
    }
    if (povVal < 225) {
      return Down;
    }
    return Left;
  }

  private DPadValue(int aValue) {
    value = aValue;
  }
}

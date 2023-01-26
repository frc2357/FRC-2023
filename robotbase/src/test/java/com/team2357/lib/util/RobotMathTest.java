package com.team2357.lib.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class RobotMathTest {

  @Test
  public void testTurnDegreesToInches() {
    assertEquals(RobotMath.turnDegreesToInches(45.0, 6.0), 2.35, 0.1);
  }

  @Test
  public void testTurnInchesToDegrees() {
    assertEquals(RobotMath.turnInchesToDegrees(2.356, 6.0), 45.0, 0.1);
  }
}

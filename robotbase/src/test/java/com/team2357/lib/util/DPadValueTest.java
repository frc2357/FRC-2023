package com.team2357.lib.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class DPadValueTest {

  @Test
  public void testPOVIsNotPressed() {
    assertEquals(DPadValue.fromPOV(-1), DPadValue.Unpressed);
  }

  @Test
  public void testPOVIsUp() {
    assertEquals(DPadValue.fromPOV(316), DPadValue.Up);
    assertEquals(DPadValue.fromPOV(35), DPadValue.Up);
  }

  @Test
  public void testPOVIsRight() {
    assertEquals(DPadValue.fromPOV(130), DPadValue.Right);
  }

  @Test
  public void testPOVIsDown() {
    assertEquals(DPadValue.fromPOV(220), DPadValue.Down);
  }

  @Test
  public void testPOVIsLeft() {
    assertEquals(DPadValue.fromPOV(300), DPadValue.Left);
  }
}

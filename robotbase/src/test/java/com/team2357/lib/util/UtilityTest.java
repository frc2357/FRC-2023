package com.team2357.lib.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UtilityTest {

  //Double Clamp Tests

  @Test
  public void testDoubleClampReturnsInputWhenInputIsBetweenMinAndMax() {
    assertEquals(Utility.clamp(3.2, -5.5, 5.5), 3.2, 0.0);
  }

  @Test
  public void testDoubleClampReturnsMinWhenImputIsLessThanMin() {
    assertEquals(Utility.clamp(-7.5, -5.5, 5.5), -5.5, 0.0);
  }

  @Test
  public void testDoubleClampReturnsMaxWhenInputIsGreaterThanMax() {
    assertEquals(Utility.clamp(8.2, -5.5, 5.5), 5.5, 0.0);
  }

  //int clamp tests

  @Test
  public void testIntClampReturnsInputWhenInputIsBetweenMinAndMax() {
    assertEquals(Utility.clamp(3, -5, 5), 3, 0);
  }

  @Test
  public void testIntClampReturnsMinWhenInputIsLessThanMin() {
    assertEquals(Utility.clamp(-7, -5, 5), -5, 0);
  }

  @Test
  public void testIntClampReturnsMaxWhenInputIsGreaterThanMax() {
    assertEquals(Utility.clamp(8, -5, 5), 5, 0);
  }

  //getAverage tests
  @Test
  public void testGetAverageReturnsFiveWithTenElementArrayFromOneToTen() {
    int[] samples = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

    assertEquals(Utility.getAverage(samples), 5, 0);
  }

  //Deadband Tests

  @Test
  public void testDeadbandReturnsInputWhenInputIsLessThanDeadband() {
    assertEquals(Utility.deadband(1.0, 0.5), 0.9, 1.0);
  }

  @Test
  public void testDeadbandReturnsZeroWhenInputIsGreaterThanDeadband() {
    assertEquals(Utility.deadband(0.5, 1.0), 0.0, 0.0);
  }

  @Test
  public void testDeadbandReturnsInputWhenABSOfNegativeInputIsLessThanDeadband() {
    assertEquals(Utility.deadband(-1.0, 0.5), -1.0, 0.9);
  }
}

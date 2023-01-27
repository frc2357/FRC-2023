package com.team2357.lib.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.wpi.first.wpilibj.XboxController;

public class DriverControlsTest {

  private XboxController controller;

  @BeforeEach
  public void setup() {
    controller = mock(XboxController.class);
  }

  @Test
  public void testGetSpeed() {
    DriverControls controls = new DriverControls(controller, 0.1);

    when(controller.getLeftY()).thenReturn(1.0);
    assertEquals(controls.getSpeed(), 1.0, 0.0);

    when(controller.getLeftY()).thenReturn(0.11);
    assertEquals(controls.getSpeed(), 0.11, 0.0);

    when(controller.getLeftY()).thenReturn(0.09);
    assertEquals(controls.getSpeed(), 0.0, 0.0);

    when(controller.getLeftY()).thenReturn(-0.09);
    assertEquals(controls.getSpeed(), 0.0, 0.0);

    when(controller.getLeftY()).thenReturn(-0.4);
    assertEquals(controls.getSpeed(), -0.4, 0.0);

    when(controller.getLeftY()).thenReturn(-1.0);
    assertEquals(controls.getSpeed(), -1.0, 0.0);
  }

  @Test
  public void testGetTurn() {
    DriverControls controls = new DriverControls(controller, 0.1);

    when(controller.getRightX()).thenReturn(1.0);
    assertEquals(controls.getTurn(), 1.0, 0.0);

    when(controller.getRightX()).thenReturn(0.11);
    assertEquals(controls.getTurn(), 0.11, 0.0);

    when(controller.getRightX()).thenReturn(0.09);
    assertEquals(controls.getTurn(), 0.0, 0.0);

    when(controller.getRightX()).thenReturn(-0.09);
    assertEquals(controls.getTurn(), 0.0, 0.0);

    when(controller.getRightX()).thenReturn(-0.4);
    assertEquals(controls.getTurn(), -0.4, 0.0);

    when(controller.getRightX()).thenReturn(-1.0);
    assertEquals(controls.getTurn(), -1.0, 0.0);
  }
}

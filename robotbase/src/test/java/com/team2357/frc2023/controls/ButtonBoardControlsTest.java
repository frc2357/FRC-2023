package com.team2357.frc2023.controls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.wpi.first.wpilibj.XboxController;

public class ButtonBoardControlsTest {
    
    private XboxController controller;

    @BeforeEach
    public void setup() {
        controller = mock(XboxController.class);
    }

    @Test
    public void testGetKey() {
        ButtonBoardControls controls = new ButtonBoardControls(controller);

        when(controller.getRightX()).thenReturn(-1.0);
        when(controller.getRightY()).thenReturn(-1.0);
        assertEquals(controls.getKey(), 0, 0.0);

        when(controller.getRightY()).thenReturn(0.0);
        assertEquals(controls.getKey(), 9, 0.0);

        when(controller.getRightY()).thenReturn(1.0);
        assertEquals(controls.getKey(), 18, 0.0);

        when(controller.getRightX()).thenReturn(0.0);
        when(controller.getRightY()).thenReturn(-1.0);
        assertEquals(controls.getKey(), 4, 0.0);

        when(controller.getRightY()).thenReturn(0.0);
        assertEquals(controls.getKey(), 13, 0.0);

        when(controller.getRightY()).thenReturn(1.0);
        assertEquals(controls.getKey(), 22, 0.0);

        when(controller.getRightX()).thenReturn(1.0);
        when(controller.getRightY()).thenReturn(-1.0);
        assertEquals(controls.getKey(), 8, 0.0);

        when(controller.getRightY()).thenReturn(0.0);
        assertEquals(controls.getKey(), 17, 0.0);

        when(controller.getRightY()).thenReturn(1.0);
        assertEquals(controls.getKey(), 26, 0.0);
    }

}

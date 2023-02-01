package com.team2357.frc2023.controls;

import com.team2357.frc2023.Constants;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class ButtonBoardControls {
    XboxController m_controller;

    private Trigger m_rightJoystick;

    private int m_xValue, m_yValue;

    public boolean m_keys[][] = new boolean[Constants.CONTROLLER.BUTTON_BOARD_NUM_ROWS][Constants.CONTROLLER.BUTTON_BOARD_NUM_COLS];

    public ButtonBoardControls(XboxController controller) {
        m_controller = controller;

        m_rightJoystick = new Trigger(() -> {
            return (1 >= m_controller.getRightX() && m_controller.getRightX() >= -1) &&
                    (1 >= m_controller.getRightY() && m_controller.getRightY() >= -1);
        });

        m_rightJoystick.onTrue(new InstantCommand(() -> {
            updateKeys();
        }));
    }

    private void updateKeys() {
        m_xValue = analogToIndex(m_controller.getRightX());
        m_yValue = analogToIndex(m_controller.getRightY());

        clearKeys();
        m_keys[m_yValue][m_xValue] = true;
    }

    private int analogToIndex(double value) {
        value /= Constants.CONTROLLER.BUTTON_BOARD_JOYSTICK_MAX_VALUE;
        value *= 10;
        return (int) value;
    }

    private void clearKeys() {
        m_keys = new boolean[Constants.CONTROLLER.BUTTON_BOARD_NUM_ROWS][Constants.CONTROLLER.BUTTON_BOARD_NUM_COLS];
    }

}

package com.team2357.frc2023.controls;

import com.team2357.frc2023.Constants;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class ButtonBoardControls {
    XboxController m_controller;

    private Trigger m_rightJoystick;

    private int m_xValue, m_yValue;

    private int m_key;

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
        m_yValue = m_xValue = 0;
        for (int i = -1;i < 1;i += 1) {
            if (m_controller.getRightY() > i) {
                m_yValue++;
            }
        }
        for (double i = -1;i < 1;i += .25) {
            if (m_controller.getRightX() > i) {
                m_xValue++;
            }
        }

        m_key = m_yValue * 9 + m_xValue;
    }

    public int getKey() {
        updateKeys();
        return m_key;
    }

}

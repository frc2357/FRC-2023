package com.team2357.frc2023.state;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

public class LEDState {
    private static LEDState s_instance;

    public static LEDState getInstance() {
        return s_instance;
    }

    private Color m_color;
    private Spark m_blinkIn;

    private static enum Color {
        RAINBOW_RAINBOW(-0.99),
        BEATS_PER_MINUTE_OCEAN(-0.75),
        BEATS_PER_MINUTE_LAVA(-0.73),
        HEARTBEAT_GRAY(-0.19),
        RED_BREATH(-0.17),
        BLUE_BREATH(-0.15),
        GRAY_BREATH(-0.13),
        YELLOW_LARSON_SCANNER(-0.01),
        YELLOW_HEARTBEAT_FAST(0.07),
        YELLOW_BREATH_SLOW(0.09),
        YELLOW_BREATH_FAST(0.11),
        PURPLE_LARSON_SCANNER(0.19),
        PURPLE_HEARTBEAT_FAST(0.27),
        PURPLE_BREATH_SLOW(0.29),
        PURPLE_BREATH_FAST(0.31),
        WHITE(0.93),
        DARK_GRAY(0.97);

        protected double value;
        Color(double value) {
            this.value = value;
        }
    };

    public LEDState(int pwmPort) {
        s_instance = this;
        m_color = Color.DARK_GRAY;
        m_blinkIn = new Spark(pwmPort);
    }

    public void updateLEDs(RobotState.State robotState, Alliance alliance) {
        Color newColor = getColor(robotState, alliance);
        if (newColor != m_color) {
            m_color = newColor;
            m_blinkIn.set(m_color.value);
        }
    }

    private Color getColor(RobotState.State robotState, Alliance alliance) {
        switch (robotState) {
            case ROBOT_INIT:
                return Color.WHITE;
            case ROBOT_DISABLED:
                if (alliance == Alliance.Red) {
                    return Color.RED_BREATH;
                } else if (alliance == Alliance.Blue) {
                    return Color.BLUE_BREATH;
                } else {
                    return Color.GRAY_BREATH;
                }
            case ROBOT_AUTONOMOUS:
                if (alliance == Alliance.Red) {
                    return Color.BEATS_PER_MINUTE_LAVA;
                } else if (alliance == Alliance.Blue) {
                    return Color.BEATS_PER_MINUTE_OCEAN;
                } else {
                    return Color.HEARTBEAT_GRAY;
                }
            case ROBOT_STOWED_EMPTY:
                return Color.RAINBOW_RAINBOW;
            case ROBOT_STOWED_CONE:
                return Color.YELLOW_BREATH_SLOW;
            case ROBOT_STOWED_CUBE:
                return Color.PURPLE_BREATH_SLOW;
            case ROBOT_PRE_INTAKING_CONE:
                return Color.YELLOW_HEARTBEAT_FAST;
            case ROBOT_PRE_INTAKING_CUBE:
                return Color.PURPLE_HEARTBEAT_FAST;
            case ROBOT_INTAKING_CONE:
                return Color.YELLOW_BREATH_FAST;
            case ROBOT_INTAKING_CUBE:
                return Color.PURPLE_BREATH_FAST;
            case ROBOT_PRE_SCORE_CONE_HIGH:
            case ROBOT_PRE_SCORE_CONE_MID:
            case ROBOT_PRE_SCORE_CONE_LOW:
                return Color.YELLOW_LARSON_SCANNER;
            case ROBOT_PRE_SCORE_CUBE_HIGH:
            case ROBOT_PRE_SCORE_CUBE_MID:
            case ROBOT_PRE_SCORE_CUBE_LOW:
                return Color.PURPLE_LARSON_SCANNER;
            default:
                return Color.DARK_GRAY;
        }
    }
}

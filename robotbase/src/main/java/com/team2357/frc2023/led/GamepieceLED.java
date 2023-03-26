package com.team2357.frc2023.led;

import edu.wpi.first.wpilibj.motorcontrol.Spark;

public class GamepieceLED {

    public enum SIGNAL_COLOR {
        PURPLE(0.27),
        YELLOW(0.07),
        BLANK(0.99);

        double value;

        SIGNAL_COLOR(double value) {
            this.value = value;
        }
    }

    public enum SIGNAL_PATTERN {
        BLEND_C1_C2(0.47),
        COLOR_WAVES_C1_C2(0.53),
        BEATS_PER_MINUTE_C1_C2(0.43),

        LARSON_SCANNER_C1(-0.01),
        LIGHT_CHASE_C1(0.01),
        HEARTBEAT_SLOW_C1(0.03),
        HEARTBEAT_MEDIUM_C1(0.05),
        HERTBEAT_FAST_C1(0.07),
        BREATH_SLOW_C1(0.09),
        BREATH_FAST_C1(0.11),
        SHOT_C1(0.13),
        STROBE_C1(0.15),

        LARSON_SCANNER_C2(0.19),
        LIGHT_CHASE_C2(0.21),
        HEARTBEAT_SLOW_C2(0.23),
        HEARTBEAT_MEDIUM_C2(0.25),
        HERTBEAT_FAST_C2(0.27),
        BREATH_SLOW_C2(0.29),
        BREATH_FAST_C2(0.31),
        SHOT_C2(0.33),
        STROBE_C2(0.35);

        double value;

        SIGNAL_PATTERN(double value) {
            this.value = value;
        }
    }

    private static GamepieceLED m_instance;

    public static GamepieceLED getInstance() {
        return m_instance;
    }

    private SIGNAL_COLOR m_signalColor;
    private SIGNAL_PATTERN m_signalPattern;
    private Spark m_blinkIn;

    public GamepieceLED(int pwmPort) {
        m_signalColor = SIGNAL_COLOR.PURPLE;
        m_instance = this;
        m_blinkIn = new Spark(pwmPort);
    }

    public void setSignalColor(SIGNAL_COLOR signalColor) {
        m_signalColor = signalColor;
        m_blinkIn.set(m_signalColor.value);
    }

    public void setSignalPattern(SIGNAL_PATTERN signalPattern) {
        m_signalPattern = signalPattern;
        m_blinkIn.set(m_signalPattern.value);
    }

    public SIGNAL_COLOR getSignalColor() {
        return m_signalColor;
    }

    public void blankLED() {
        setSignalColor(SIGNAL_COLOR.BLANK);
    }
}

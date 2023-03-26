package com.team2357.frc2023.led;

import edu.wpi.first.wpilibj.motorcontrol.Spark;

public class GamepieceLED {

    public enum SIGNAL_COLOR{
        PURPLE(0.27),
        YELLOW(0.07);

        double value;
        SIGNAL_COLOR(double value) {
            this.value = value;
        }
    }
    
    private static GamepieceLED m_instance;

    public static GamepieceLED getInstance() {
        return m_instance;
    }

    private SIGNAL_COLOR m_signalColor;
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

    public SIGNAL_COLOR getSignalColor() {
        return m_signalColor;
    }   
    
    public void blankLED(){
        m_blinkIn.stopMotor();
    }
}

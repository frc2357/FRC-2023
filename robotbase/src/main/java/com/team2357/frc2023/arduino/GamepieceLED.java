package com.team2357.frc2023.arduino;

public class GamepieceLED {

    public enum SIGNAL_COLOR{
        NONE,
        PURPLE,
        YELLOW
    }
    
    private static GamepieceLED m_instance;

    private SIGNAL_COLOR m_signalColor;

    public static GamepieceLED getInstance() {
        if(m_instance == null) {
            new GamepieceLED();
        }
        return m_instance;
    }

    private GamepieceLED() {
        m_signalColor = SIGNAL_COLOR.PURPLE;
        m_instance = this;
    }

    public void setSignalColor(SIGNAL_COLOR signalColor) {
        m_signalColor = signalColor;
    }

    public SIGNAL_COLOR getSignalColor() {
        return m_signalColor;
    }    
}

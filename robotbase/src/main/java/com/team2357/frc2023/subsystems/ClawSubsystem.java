package com.team2357.frc2023.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ClawSubsystem extends SubsystemBase {
    private static ClawSubsystem instance = null;

    public static ClawSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public int m_openMilliseconds = 0;
        public int m_closeMilliseconds = 0;
    }

    public Configuration m_config;

    private enum ClawState { Unknown, Open, Closed };

    private DoubleSolenoid m_clawSolenoid;
    private ClawState m_currentState;
    private ClawState m_desiredState;
    private long m_lastActionMillis;
    
    public ClawSubsystem(DoubleSolenoid clawSolenoid) {
        m_clawSolenoid = clawSolenoid;
        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;
    }

    public boolean isOpening() {
        return (m_desiredState == ClawState.Open && m_currentState != ClawState.Open);
    }

    public boolean isOpen() {
        return (m_desiredState == ClawState.Open && m_currentState == ClawState.Open);
    }

    public boolean isClosing() {
        return (m_desiredState == ClawState.Closed && m_currentState != ClawState.Closed);
    }

    public boolean isClosed() {
        return (m_desiredState == ClawState.Closed && m_currentState == ClawState.Closed);
    }

    public void open() {
        m_currentState = ClawState.Unknown;
        m_desiredState = ClawState.Open;
        m_lastActionMillis = 0; 
    }

    public void close() {
        m_currentState = ClawState.Unknown;
        m_desiredState = ClawState.Closed;
        m_lastActionMillis = 0;
    }

    @Override
    public void periodic() {
        if (m_currentState != m_desiredState) {
            if (m_desiredState == ClawState.Open) {
                openPeriodic();
            } else if (m_desiredState == ClawState.Closed) {
                closedPeriodic();
            }
        }
    }

    private void openPeriodic() {
        long now = System.currentTimeMillis();
        if (m_lastActionMillis == 0) {
            m_clawSolenoid.set(Value.kReverse);
            m_lastActionMillis = now;
        } else if (now > m_lastActionMillis + m_config.m_openMilliseconds) {
            m_currentState = ClawState.Open;
            m_lastActionMillis = 0;
        }
    }

    private void closedPeriodic() {
        long now = System.currentTimeMillis();

        if (m_lastActionMillis == 0) {
            m_clawSolenoid.set(Value.kForward);
            m_lastActionMillis = now;
        } else if (now > m_lastActionMillis + m_config.m_openMilliseconds) {
            m_currentState = ClawState.Closed;
            m_lastActionMillis = 0;
        }
    }
}

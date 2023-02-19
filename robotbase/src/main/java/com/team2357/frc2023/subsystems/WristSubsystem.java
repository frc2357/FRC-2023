package com.team2357.frc2023.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class WristSubsystem extends SubsystemBase {
    private static WristSubsystem instance = null;

    public static WristSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public int m_extendMilliseconds = 0;
        public int m_retractMilliseconds = 0;
    }

    private Configuration m_config;

    private enum WristState { Unknown, Extended, Retracted };

    private DoubleSolenoid m_wristeSolenoid;
    private WristState m_currentState;
    private WristState m_desiredState;
    private long m_lastActionMillis;

    public WristSubsystem(DoubleSolenoid wristSolenoid) {
        m_wristeSolenoid = wristSolenoid;

        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;
    }

    public boolean isExtending() {
        return (m_desiredState == WristState.Extended && m_currentState != WristState.Extended);
    }

    public boolean isExtended() {
        return (m_desiredState == WristState.Extended && m_currentState == WristState.Extended);
    }

    public boolean isRetracting() {
        return (m_desiredState == WristState.Retracted && m_currentState != WristState.Retracted);
    }

    public boolean isRetracted() {
        return (m_desiredState == WristState.Retracted && m_currentState == WristState.Retracted);
    }

    public void extend() {
        m_currentState = WristState.Unknown;
        m_desiredState = WristState.Extended;
        m_lastActionMillis = 0;
    }

    public void retract() {
        m_currentState = WristState.Unknown;
        m_desiredState = WristState.Retracted;
        m_lastActionMillis = 0;
    }

    @Override
    public void periodic() {
        if (m_currentState != m_desiredState) {
            if (m_desiredState == WristState.Extended) {
                extendedPeriodic();
            } else if (m_desiredState == WristState.Retracted) {
                retractedPeriodic();
            }
        }
    }

    private void extendedPeriodic() {
        long now = System.currentTimeMillis();

        if (m_lastActionMillis == 0) {
            m_wristeSolenoid.set(Value.kForward);
            m_lastActionMillis = now;
        } else if (now > m_lastActionMillis + m_config.m_extendMilliseconds) {
            m_currentState = WristState.Extended;
            m_lastActionMillis = 0;
        }
    }

    private void retractedPeriodic() {
        long now = System.currentTimeMillis();

        if (m_lastActionMillis == 0) {
            m_wristeSolenoid.set(Value.kReverse);
            m_lastActionMillis = now;
        } else if (now > m_lastActionMillis + m_config.m_extendMilliseconds) {
            m_currentState = WristState.Retracted;
            m_lastActionMillis = 0;
        }
    }
}
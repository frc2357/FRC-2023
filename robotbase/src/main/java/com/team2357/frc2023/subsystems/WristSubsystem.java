package com.team2357.frc2023.subsystems;

import javax.management.DescriptorRead;
import javax.print.attribute.standard.MediaSize;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class WristSubsystem extends SubsystemBase {
    private static WristSubsystem instance = null;

    public static WristSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public int m_extendMilliseconds = 0;
        public int m_contractMilliseconds = 0;
    }

    private Configuration m_config;

    private enum WristState { Unknown, Extended, Contracted };

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

    public boolean isContracting() {
        return (m_desiredState == WristState.Contracted && m_currentState != WristState.Contracted);
    }

    public boolean isContracted() {
        return (m_desiredState == WristState.Contracted && m_currentState == WristState.Contracted);
    }

    public void extend() {
        m_currentState = WristState.Unknown;
        m_desiredState = WristState.Extended;
        m_lastActionMillis = 0;
    }

    public void contract() {
        m_currentState = WristState.Unknown;
        m_desiredState = WristState.Contracted;
        m_lastActionMillis = 0;
    }

    @Override
    public void periodic() {
        if (m_currentState != m_desiredState) {
            if (m_desiredState == WristState.Extended) {
                extendedPeriodic();
            } else if (m_desiredState == WristState.Contracted) {
                contractedPeriodic();
            }
        }
    }

    private void extendedPeriodic() {
        long now = System.currentTimeMillis();

        if (m_lastActionMillis == 0) {
            m_wristeSolenoid.set(DoubleSolenoid.Value.kForward);
            m_lastActionMillis = now;
        } else if (now > m_lastActionMillis + m_config.m_extendMilliseconds) {
            m_wristeSolenoid.set(DoubleSolenoid.Value.kOff);
            m_currentState = WristState.Extended;
            m_lastActionMillis = 0;
        }
    }

    private void contractedPeriodic() {
        long now = System.currentTimeMillis();

        if (m_lastActionMillis == 0) {
            m_wristeSolenoid.set(DoubleSolenoid.Value.kReverse);
            m_lastActionMillis = now;
        } else if (now > m_lastActionMillis + m_config.m_extendMilliseconds) {
            m_wristeSolenoid.set(DoubleSolenoid.Value.kOff);
            m_currentState = WristState.Contracted;
            m_lastActionMillis = 0;
        }
    }
}
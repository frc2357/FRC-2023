package com.team2357.frc2023.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeArmSubsystem extends SubsystemBase {
    private static IntakeArmSubsystem instance = null;

    public static IntakeArmSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public int m_deployMilliseconds = 0;
        public int m_stowMilliseconds = 0;
    }

    public Configuration m_config;

    private enum ArmState { Unknown, Deployed, Stowed };

    private DoubleSolenoid m_intakeSolenoid;
    private ArmState m_currentState;
    private ArmState m_desiredState;
    private long m_lastActionMillis;

    public IntakeArmSubsystem(DoubleSolenoid intakeSolenoid) {
        m_intakeSolenoid = intakeSolenoid;

        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;
    }

    public boolean isDeploying() {
        return (m_desiredState == ArmState.Deployed && m_currentState != ArmState.Deployed);
    }

    public boolean isDeployed() {
        return (m_desiredState == ArmState.Deployed && m_currentState == ArmState.Deployed);
    }

    public boolean isStowing() {
        return (m_desiredState == ArmState.Stowed && m_currentState != ArmState.Stowed);
    }

    public boolean isStowed() {
        return (m_desiredState == ArmState.Stowed && m_currentState == ArmState.Stowed);
    }

    public void deploy() {
        m_currentState = ArmState.Unknown;
        m_desiredState = ArmState.Deployed;
        m_lastActionMillis = 0;
    }

    public void stow() {
        m_currentState = ArmState.Unknown;
        m_desiredState = ArmState.Stowed;
        m_lastActionMillis = 0;
    }

    @Override
    public void periodic() {
        if (m_currentState != m_desiredState) {
            if (m_desiredState == ArmState.Deployed) {
                deployPeriodic();
            } else if (m_desiredState == ArmState.Stowed) {
                stowPeriodic();
            }
        }
    }

    private void deployPeriodic() {
        long now = System.currentTimeMillis();

        if (m_lastActionMillis == 0) {
            m_intakeSolenoid.set(DoubleSolenoid.Value.kForward);
            m_lastActionMillis = now;
        } else if (now > m_lastActionMillis + m_config.m_deployMilliseconds) {
            m_intakeSolenoid.set(DoubleSolenoid.Value.kOff);
            m_currentState = ArmState.Deployed;
            m_lastActionMillis = 0;
        }
    }
    
    private void stowPeriodic() {
        long now = System.currentTimeMillis();

        if (m_lastActionMillis == 0) {
            m_intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
            m_lastActionMillis = now;
        } else if (now > m_lastActionMillis + m_config.m_stowMilliseconds) {
            m_intakeSolenoid.set(DoubleSolenoid.Value.kOff);
            m_currentState = ArmState.Stowed;
            m_lastActionMillis = 0;
        }
    }
}

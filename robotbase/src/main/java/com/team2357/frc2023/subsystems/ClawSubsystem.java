package com.team2357.frc2023.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ClawSubsystem extends SubsystemBase {
    private static ClawSubsystem instance = null;

    public static ClawSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public double m_forwardPercentOutput;
        public double m_reversePercentOutput;

        public double m_rollerAxisMaxSpeed;

        public boolean m_isInverted;
    }

    public Configuration m_config;

    private CANSparkMax m_rollerMotor;

    public ClawSubsystem(int clawMotorID) {
        m_rollerMotor = new CANSparkMax(clawMotorID, MotorType.kBrushless);

        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;

        m_rollerMotor.setInverted(m_config.m_isInverted);

        m_rollerMotor.setIdleMode(IdleMode.kBrake);
    }

    public void runRollers(boolean reverse) {
        if (reverse) {
            m_rollerMotor.set(m_config.m_reversePercentOutput);
        } else {
            m_rollerMotor.set(m_config.m_forwardPercentOutput);
        }
    }

    public void setAxisRollerSpeed(double axisSpeed) {
        double motorSpeed = (-axisSpeed) * m_config.m_rollerAxisMaxSpeed;
        m_rollerMotor.set(motorSpeed);
    }

    public void manualRunRollers(double percentOutput) {
        m_rollerMotor.set(percentOutput);
    }

    public void stopRollers() {
        m_rollerMotor.set(0.0);
    }
}

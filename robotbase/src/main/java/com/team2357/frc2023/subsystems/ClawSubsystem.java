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
        public double m_conePercentOutput;
        public double m_cubePercentOutput;

        public int m_clawMotorFreeLimitAmps;
        public int m_clawMotorStallLimitAmps;

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

        m_rollerMotor.setSmartCurrentLimit(m_config.m_clawMotorStallLimitAmps, m_config.m_clawMotorFreeLimitAmps);

        m_rollerMotor.setInverted(m_config.m_isInverted);

        m_rollerMotor.setIdleMode(IdleMode.kBrake);
    }

    public void intakeCone() {
        m_rollerMotor.set(m_config.m_conePercentOutput);
    }

    public void intakeCube() {
        m_rollerMotor.set(m_config.m_cubePercentOutput);
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

    public double getAmps() {
        return m_rollerMotor.getOutputCurrent();
    }
}

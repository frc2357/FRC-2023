package com.team2357.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team2357.frc2023.Constants;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeRollerSubsystem extends SubsystemBase {
    public static IntakeRollerSubsystem instance = null;

    private CANSparkMax m_rightMotor;
    private CANSparkMax m_leftMotor;

    public static IntakeRollerSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public double m_runPercentOutput;
        public double m_reversePercentOutput;

        public double m_rampRate;
        public int m_currentLimit;

        public boolean m_rightInverted;
        public boolean m_leftInverted;
    }

    public Configuration m_config;

    public IntakeRollerSubsystem() {
        m_rightMotor = new CANSparkMax(Constants.CAN_ID.RIGHT_INTAKE_MOTOR, MotorType.kBrushless);
        m_leftMotor = new CANSparkMax(Constants.CAN_ID.LEFT_INTAKE_MOTOR, MotorType.kBrushless);

        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;

        m_rightMotor.setIdleMode(IdleMode.kBrake);
        m_leftMotor.setIdleMode(IdleMode.kBrake);

        m_rightMotor.setOpenLoopRampRate(m_config.m_rampRate);
        m_leftMotor.setOpenLoopRampRate(m_config.m_rampRate);

        m_rightMotor.setSmartCurrentLimit(m_config.m_currentLimit);
        m_leftMotor.setSmartCurrentLimit(m_config.m_currentLimit);

        m_rightMotor.setInverted(m_config.m_rightInverted);
        m_leftMotor.setInverted(m_config.m_leftInverted);
    }

    public void runIntake(boolean reverse) {
        if (reverse) {
            m_rightMotor.set(m_config.m_reversePercentOutput);
            m_leftMotor.set(m_config.m_reversePercentOutput);
        } else {
            m_rightMotor.set(m_config.m_runPercentOutput);
            m_leftMotor.set(m_config.m_runPercentOutput);
        }
    }

    public void setAxisRollerSpeed(double axisSpeed) {
        double motorSpeed = (-axisSpeed) * m_config.m_rollerAxisMaxSpeed;
        m_masterIntakeTalon.set(ControlMode.PercentOutput, motorSpeed);
        m_startupTime = System.currentTimeMillis() + m_config.m_rollerSpeedUpMillis;
    }

    public void stopIntake() {
        m_rightMotor.set(0.0);
        m_leftMotor.set(0.0);
    }
}
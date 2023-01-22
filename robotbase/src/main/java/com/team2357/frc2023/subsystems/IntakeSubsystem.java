package com.team2357.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team2357.frc2023.Constants;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeSubsystem extends SubsystemBase {
    public static IntakeSubsystem instance = null;

    private CANSparkMax m_rightMotor;
    private CANSparkMax m_leftMotor;

    public static IntakeSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public double m_runVolts;

        public double m_rampRate;

        public boolean m_rightInverted;
        public boolean m_leftInverted;
    }

    public Configuration m_config;

    public IntakeSubsystem() {
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

        m_rightMotor.setInverted(m_config.m_rightInverted);
        m_leftMotor.setInverted(m_config.m_leftInverted);
    }

    public void runIntake(boolean reverse) {
        if (reverse) {
            m_rightMotor.set(-m_config.m_runVolts);
            m_leftMotor.set(-m_config.m_runVolts);
        } else {
            m_rightMotor.set(m_config.m_runVolts);
            m_leftMotor.set(m_config.m_runVolts);
        }
    }

    public void stopIntake() {
        m_rightMotor.set(0.0);
        m_leftMotor.set(0.0);
    }
}
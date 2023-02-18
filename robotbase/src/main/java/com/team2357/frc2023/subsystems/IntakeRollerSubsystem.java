package com.team2357.frc2023.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team2357.frc2023.Constants;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeRollerSubsystem extends SubsystemBase {
    public static IntakeRollerSubsystem instance = null;

    private TalonSRX m_rightMotor;
    private TalonSRX m_leftMotor;

    public static IntakeRollerSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public double m_runPercentOutput;
        public double m_reversePercentOutput;

        public double m_rollerAxisMaxSpeed;

        public double m_rampRate;
        public int m_currentLimit;

        public boolean m_rightInverted;
        public boolean m_leftInverted;
    }

    public Configuration m_config;

    public IntakeRollerSubsystem() {
        m_rightMotor = new TalonSRX(Constants.CAN_ID.RIGHT_INTAKE_MOTOR);
        m_leftMotor = new TalonSRX(Constants.CAN_ID.LEFT_INTAKE_MOTOR);

        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;

        m_rightMotor.setInverted(m_config.m_rightInverted);
        m_leftMotor.setInverted(m_config.m_leftInverted);

        m_rightMotor.follow(m_leftMotor);

        m_leftMotor.setNeutralMode(NeutralMode.Brake);
        m_leftMotor.enableCurrentLimit(true);
        m_leftMotor.configPeakCurrentDuration(0);
        m_leftMotor.configPeakCurrentLimit(5);
        m_leftMotor.configContinuousCurrentLimit(5);
        // m_rightMotor.setIdleMode(IdleMode.kBrake);
        // m_leftMotor.setIdleMode(IdleMode.kBrake);

        // m_rightMotor.setOpenLoopRampRate(m_config.m_rampRate);
        // m_leftMotor.setOpenLoopRampRate(m_config.m_rampRate);

        // m_rightMotor.setSmartCurrentLimit(m_config.m_currentLimit);
        // m_leftMotor.setSmartCurrentLimit(m_config.m_currentLimit);
    }

    public void runIntake(boolean reverse) {
        if (reverse) {
            m_rightMotor.set(ControlMode.PercentOutput, m_config.m_reversePercentOutput);
            m_leftMotor.set(ControlMode.PercentOutput, m_config.m_reversePercentOutput);
        } else {
            m_rightMotor.set(ControlMode.PercentOutput, m_config.m_runPercentOutput);
            m_leftMotor.set(ControlMode.PercentOutput, m_config.m_runPercentOutput);
        }
    }

    public void setAxisRollerSpeed(double axisSpeed) {
        double motorSpeed = (-axisSpeed) * m_config.m_rollerAxisMaxSpeed;
        m_leftMotor.set(ControlMode.PercentOutput, motorSpeed);
        m_rightMotor.set(ControlMode.PercentOutput, motorSpeed);
    }

    public void stopIntake() {
        m_rightMotor.set(ControlMode.PercentOutput, 0.0);
        m_leftMotor.set(ControlMode.PercentOutput, 0.0);
    }
}
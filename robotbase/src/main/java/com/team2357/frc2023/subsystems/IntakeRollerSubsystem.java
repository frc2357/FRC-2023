package com.team2357.frc2023.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team2357.frc2023.Constants;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeRollerSubsystem extends SubsystemBase {
    public static IntakeRollerSubsystem instance = null;

    private WPI_TalonSRX m_followerMotor;
    private WPI_TalonSRX m_masterMotor;

    public static IntakeRollerSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public double m_runPercentOutput;
        public double m_reversePercentOutput;

        public double m_rollerAxisMaxSpeed;

        public double m_rampRate;

        public int m_peakCurrentLimit;
        public int m_peakCurrentDuration;
        public int m_continuousCurrentLimit;
        
        public boolean m_rightInverted;
        public boolean m_leftInverted;
    }

    public Configuration m_config;

    public IntakeRollerSubsystem() {
        m_followerMotor = new WPI_TalonSRX(Constants.CAN_ID.RIGHT_INTAKE_MOTOR);
        m_masterMotor = new WPI_TalonSRX(Constants.CAN_ID.LEFT_INTAKE_MOTOR);

        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;

        // m_rightMotor.setIdleMode(IdleMode.kBrake);
        // m_leftMotor.setIdleMode(IdleMode.kBrake);

        // m_rightMotor.setOpenLoopRampRate(m_config.m_rampRate);
        // m_leftMotor.setOpenLoopRampRate(m_config.m_rampRate);

        // m_rightMotor.setSmartCurrentLimit(m_config.m_currentLimit);
        // m_leftMotor.setSmartCurrentLimit(m_config.m_currentLimit);

        m_followerMotor.setInverted(m_config.m_rightInverted);
        m_masterMotor.setInverted(m_config.m_leftInverted);

        m_followerMotor.follow(m_masterMotor);

        m_masterMotor.setNeutralMode(NeutralMode.Brake);
        m_masterMotor.enableCurrentLimit(true);
        m_masterMotor.configPeakCurrentLimit(m_config.m_peakCurrentLimit);
        m_masterMotor.configPeakCurrentDuration(m_config.m_peakCurrentDuration);
        m_masterMotor.configContinuousCurrentLimit(m_config.m_continuousCurrentLimit);
    }

    public void runIntake(boolean reverse) {
        if (reverse) {
            m_followerMotor.set(m_config.m_reversePercentOutput);
            m_masterMotor.set(m_config.m_reversePercentOutput);
        } else {
            m_followerMotor.set(m_config.m_runPercentOutput);
            m_masterMotor.set(m_config.m_runPercentOutput);
        }
    }

    public void setAxisRollerSpeed(double axisSpeed) {
        double motorSpeed = (-axisSpeed) * m_config.m_rollerAxisMaxSpeed;
        m_masterMotor.set(motorSpeed);
        m_followerMotor.set(motorSpeed);
    }

    public void stopIntake() {
        m_followerMotor.set(0.0);
        m_masterMotor.set(0.0);
    }
}
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

    private WPI_TalonSRX m_masterIntakeMotor;
    private WPI_TalonSRX m_followerIntakeMotor;

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
        // public int m_currentLimit;

        public boolean m_masterInverted;
        public boolean m_followerInverted;
    }

    public Configuration m_config;

    public IntakeRollerSubsystem() {
        m_masterIntakeMotor = new WPI_TalonSRX(Constants.CAN_ID.RIGHT_INTAKE_MOTOR);
        m_followerIntakeMotor = new WPI_TalonSRX(Constants.CAN_ID.LEFT_INTAKE_MOTOR);

        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;

        m_followerIntakeMotor.setInverted(m_config.m_followerInverted);
        m_masterIntakeMotor.setInverted(m_config.m_masterInverted);

        m_followerIntakeMotor.follow(m_masterIntakeMotor);

        m_masterIntakeMotor.setNeutralMode(NeutralMode.Coast);
        m_masterIntakeMotor.enableCurrentLimit(true);
        m_masterIntakeMotor.configPeakCurrentLimit(m_config.m_peakCurrentLimit);
        m_masterIntakeMotor.configPeakCurrentDuration(m_config.m_peakCurrentDuration);
        m_masterIntakeMotor.configContinuousCurrentLimit(m_config.m_continuousCurrentLimit);

        // m_rightMotor.setIdleMode(IdleMode.kBrake);
        // m_leftMotor.setIdleMode(IdleMode.kBrake);

        // m_rightMotor.setOpenLoopRampRate(m_config.m_rampRate);
        // m_leftMotor.setOpenLoopRampRate(m_config.m_rampRate);

        // m_rightMotor.setSmartCurrentLimit(m_config.m_currentLimit);
        // m_leftMotor.setSmartCurrentLimit(m_config.m_currentLimit);

    }

    public void runIntake(boolean reverse) {
        if (reverse) {
            m_masterIntakeMotor.set(m_config.m_reversePercentOutput);
            m_followerIntakeMotor.set(m_config.m_reversePercentOutput);
        } else {
            m_masterIntakeMotor.set(m_config.m_runPercentOutput);
            m_followerIntakeMotor.set(m_config.m_runPercentOutput);
        }
    }

    public void setAxisRollerSpeed(double axisSpeed) {
        double motorSpeed = (-axisSpeed) * m_config.m_rollerAxisMaxSpeed;
        m_followerIntakeMotor.set(motorSpeed);
        m_masterIntakeMotor.set(motorSpeed);
    }

    public void stopIntake() {
        m_masterIntakeMotor.set(0.0);
        m_followerIntakeMotor.set(0.0);
    }
}
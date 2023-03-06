package com.team2357.frc2023.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeRollerSubsystem extends SubsystemBase {
    public static IntakeRollerSubsystem instance = null;

    private TalonSRX m_masterIntakeMotor;
    private TalonSRX m_followerIntakeMotor;

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
        
        public boolean m_masterInverted;
        public boolean m_followerInverted;
    }

    public Configuration m_config;

    public IntakeRollerSubsystem(int masterIntakeMotorId, int followerIntakeMotorId) {
        m_masterIntakeMotor = new TalonSRX(masterIntakeMotorId);
        m_followerIntakeMotor = new TalonSRX(followerIntakeMotorId);

        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;

        m_masterIntakeMotor.setInverted(m_config.m_masterInverted);
        m_followerIntakeMotor.setInverted(m_config.m_followerInverted);

        m_followerIntakeMotor.follow(m_masterIntakeMotor);

        m_masterIntakeMotor.setNeutralMode(NeutralMode.Brake);
        m_masterIntakeMotor.enableCurrentLimit(true);
        m_masterIntakeMotor.configPeakCurrentLimit(m_config.m_peakCurrentLimit);
        m_masterIntakeMotor.configPeakCurrentDuration(m_config.m_peakCurrentDuration);
        m_masterIntakeMotor.configContinuousCurrentLimit(m_config.m_continuousCurrentLimit);

        m_followerIntakeMotor.setNeutralMode(NeutralMode.Brake);
        m_followerIntakeMotor.enableCurrentLimit(true);
        m_followerIntakeMotor.configPeakCurrentLimit(m_config.m_peakCurrentLimit);
        m_followerIntakeMotor.configPeakCurrentDuration(m_config.m_peakCurrentDuration);
        m_followerIntakeMotor.configContinuousCurrentLimit(m_config.m_continuousCurrentLimit);
    }

    public void runIntake(boolean reverse) {
        if (reverse) {
            m_masterIntakeMotor.set(ControlMode.PercentOutput, m_config.m_reversePercentOutput);
        } else {
            m_masterIntakeMotor.set(ControlMode.PercentOutput, m_config.m_runPercentOutput);
        }
    }

    public void setAxisRollerSpeed(double axisSpeed) {
        double motorSpeed = (-axisSpeed) * m_config.m_rollerAxisMaxSpeed;
        m_masterIntakeMotor.set(ControlMode.PercentOutput, motorSpeed);
        manualRunIntake(axisSpeed);
    }

    public void manualRunIntake(double percentOutput) {
        m_masterIntakeMotor.set(ControlMode.PercentOutput, percentOutput);
    }

    public void stopIntake() {
        m_masterIntakeMotor.set(ControlMode.PercentOutput, 0.0);
    }
}
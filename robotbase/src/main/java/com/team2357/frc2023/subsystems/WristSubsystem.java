package com.team2357.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxPIDController;
import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import com.team2357.lib.util.Utility;

public class WristSubsystem extends ClosedLoopSubsystem {
    private static WristSubsystem instance = null;

    public static WristSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public double m_wristAxisMaxSpeed;
        public double m_maxSpeedPercent;

        public int m_wristMotorStallLimitAmps;
        public int m_wristMotorFreeLimitAmps;

        public boolean m_isInverted;

        // smart motion config
        public double m_wristMotorP;
        public double m_wristI;
        public double m_wristD;

        public double m_wristIZone;
        public double m_wristFF;
        public double m_wristMaxOutput;
        public double m_wristMinOutput;
        public double m_wristMaxRPM;
        public double m_wristMaxVel;
        public double m_wristMinVel;
        public double m_wristMaxAcc;
        public double m_wristSmartMotionAllowedError;
        public int m_smartMotionSlot;
    
        public double m_wristAllowedError;
    }

    private Configuration m_config;

    private CANSparkMax m_wristMotor;
    private SparkMaxPIDController m_pidController;

    private double m_targetRotations;

    public WristSubsystem(int motorId) {
        m_wristMotor = new CANSparkMax(motorId, MotorType.kBrushless);

        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;

        m_wristMotor.setIdleMode(IdleMode.kBrake);
        m_wristMotor.setSmartCurrentLimit(m_config.m_wristMotorStallLimitAmps, m_config.m_wristMotorFreeLimitAmps);

        m_pidController = m_wristMotor.getPIDController();

        m_pidController.setP(m_config.m_wristMotorP);
        m_pidController.setI(m_config.m_wristI);
        m_pidController.setD(m_config.m_wristD);
        m_pidController.setIZone(m_config.m_wristIZone);
        m_pidController.setFF(m_targetRotations);
        m_pidController.setOutputRange(m_config.m_wristMinOutput, m_config.m_wristMaxOutput);

        m_pidController.setSmartMotionMaxVelocity(m_config.m_wristMaxVel, m_config.m_smartMotionSlot);
        m_pidController.setSmartMotionMinOutputVelocity(m_config.m_wristMinVel, m_config.m_smartMotionSlot);
        m_pidController.setSmartMotionMaxAccel(m_config.m_wristMaxAcc, m_config.m_smartMotionSlot);
        m_pidController.setSmartMotionAllowedClosedLoopError(m_config.m_wristSmartMotionAllowedError,
                m_config.m_smartMotionSlot);
    }

    public void setRotations(double rotations) {
        setClosedLoopEnabled(true);
        m_targetRotations = rotations;
        m_pidController.setReference(m_targetRotations, CANSparkMax.ControlType.kSmartMotion);
    }

    public void stopMotor() {
        setClosedLoopEnabled(false);
        m_wristMotor.set(0.0);
    }

    public void resetEncoder() {
        m_wristMotor.getEncoder().setPosition(0);
        m_targetRotations = 0;
    }

    public double getRotations() {
        return m_wristMotor.getEncoder().getPosition();
    }

    public boolean isAtRotations() {
        return Utility.isWithinTolerance(getRotations(), m_targetRotations, m_config.m_wristAllowedError);
    }

    public double getAmps() {
        return m_wristMotor.getOutputCurrent();
    }

    public void setWristAxisSpeed(double axisSpeed) {
        setClosedLoopEnabled(false);
        double motorSpeed = (-axisSpeed) * m_config.m_wristAxisMaxSpeed;
        m_wristMotor.set(motorSpeed);
    }

    public void manualRotate(double speed) {
        setClosedLoopEnabled(false);
        m_wristMotor.set(speed);
    }

    @Override
    public void periodic() {
        if (isClosedLoopEnabled() && isAtRotations()) {
            m_pidController.setReference(m_targetRotations, ControlType.kSmartMotion);
        }
    }
    
}

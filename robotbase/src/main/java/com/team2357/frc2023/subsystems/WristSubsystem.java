package com.team2357.frc2023.subsystems;

import javax.lang.model.util.AbstractAnnotationValueVisitor14;

import org.ejml.data.MatrixType;

import com.revrobotics.CANSparkMax;
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

        public int m_wristMotorStallLimitAmps;
        public int m_wristMotorFreeLimitAmps;

        public boolean m_isInverted;

        // smart motion config
        public double m_wristMotorP;
        public double m_wristMotorI;
        public double m_wristMotorD;

        public double m_wristMotorIZone;
        public double m_wristMotorFF;
        public double m_wristMotorMaxOutput;
        public double m_wristMotorMinOutput;
        public double m_wristMotorMaxRPM;
        public double m_wristMotorMaxVel;
        public double m_wristMotorMinVel;
        public double m_wristMotorMaxAcc;
        public double m_wristMotorAllowedError;
        public double m_maxSpeedPercent;
        public int m_smartMotionSlot;
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
        m_pidController.setI(m_config.m_wristMotorI);
        m_pidController.setD(m_config.m_wristMotorD);
        m_pidController.setIZone(m_config.m_wristMotorIZone);
        m_pidController.setFF(m_targetRotations);
        m_pidController.setOutputRange(m_config.m_wristMotorMinOutput, m_config.m_wristMotorMaxOutput);

        m_pidController.setSmartMotionMaxVelocity(m_config.m_wristMotorMaxVel, m_config.m_smartMotionSlot);
        m_pidController.setSmartMotionMinOutputVelocity(m_config.m_wristMotorMinVel, m_config.m_smartMotionSlot);
        m_pidController.setSmartMotionMaxAccel(m_config.m_wristMotorMaxAcc, m_config.m_smartMotionSlot);
        m_pidController.setSmartMotionAllowedClosedLoopError(m_config.m_wristMotorAllowedError,
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
        return Utility.isWithinTolerance(getRotations(), m_targetRotations, m_config.m_wristMotorAllowedError);
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
        m_wristMotor.set(speed);
    }

    @Override
    public void periodic() {
        if (isClosedLoopEnabled() && isAtRotations()) {
            setClosedLoopEnabled(false);
        }
    }
    
}

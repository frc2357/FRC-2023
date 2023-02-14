package com.team2357.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import com.team2357.lib.util.Utility;

public class ArmExtensionSubsystem extends ClosedLoopSubsystem {
    private static ArmExtensionSubsystem instance = null;

    public static ArmExtensionSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public double m_extendAxisMaxSpeed;

        public IdleMode m_extendMotorIdleMode = IdleMode.kBrake;

        public int m_extendMotorStallLimitAmps;
        public int m_extendMotorFreeLimitAmps;

        public boolean m_isInverted = false;

        public int m_extendGrippedAmps;

        // smart motion config
        public double m_extendMotorP;
        public double m_extendMotorI;
        public double m_extendMotorD;
        public double m_extendMotorIZone;
        public double m_extendMotorFF;
        public double m_extendMotorMaxOutput;
        public double m_extendMotorMinOutput;
        public double m_extendMotorMaxRPM;
        public double m_extendMotorMaxVel;
        public double m_extendMotorMinVel;
        public double m_extendMotorMaxAcc;
        public double m_extendMotorAllowedError;
        public double m_rotationMotorAllowedError;
        public double m_maxSpeedPercent;
        public int m_smartMotionSlot;

        public int m_extendMotorRampRate;
    }

    private Configuration m_config;
    private CANSparkMax m_extendMotor;
    private SparkMaxPIDController m_pidcontroller;
    private double m_targetRotations;

    public ArmExtensionSubsystem(CANSparkMax extender) {
        m_extendMotor = extender;
        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;

        m_extendMotor.setIdleMode(m_config.m_extendMotorIdleMode);
        m_extendMotor.setSmartCurrentLimit(m_config.m_extendMotorStallLimitAmps, m_config.m_extendMotorFreeLimitAmps);
        m_pidcontroller = m_extendMotor.getPIDController();
        configureExtenderPID(m_pidcontroller);

        m_extendMotor.setInverted(m_config.m_isInverted);
        m_extendMotor.setOpenLoopRampRate(m_config.m_extendMotorRampRate);

    }
    //Mehtod for the panic mode to extend the arms
    public void manualExtend(double sensorUnits) {
        m_extendMotor.set(sensorUnits*m_config.m_maxSpeedPercent);
    }

    private void configureExtenderPID(SparkMaxPIDController pidController) {
        // set PID coefficients
        pidController.setP(m_config.m_extendMotorP);
        pidController.setI(m_config.m_extendMotorI);
        pidController.setD(m_config.m_extendMotorD);
        pidController.setIZone(m_config.m_extendMotorIZone);
        pidController.setFF(m_config.m_extendMotorFF);
        pidController.setOutputRange(m_config.m_extendMotorMinOutput, m_config.m_extendMotorMinOutput);

        // Configure smart motion
        pidController.setSmartMotionMaxVelocity(m_config.m_extendMotorMaxVel, m_config.m_smartMotionSlot);
        pidController.setSmartMotionMinOutputVelocity(m_config.m_extendMotorMinVel, m_config.m_smartMotionSlot);
        pidController.setSmartMotionMaxAccel(m_config.m_extendMotorMaxAcc, m_config.m_smartMotionSlot);
        pidController.setSmartMotionAllowedClosedLoopError(m_config.m_extendMotorAllowedError, m_config.m_smartMotionSlot);
    }

    public void stopExtensionMotors() {
        setClosedLoopEnabled(false);
        m_extendMotor.set(0);
    }

    public void resetEncoders() {
        m_extendMotor.getEncoder().setPosition(0);
    }

    public void setExtenderRotations(double rotations) {
        setClosedLoopEnabled(true);
        m_targetRotations = rotations;
        m_pidcontroller.setReference(m_targetRotations, CANSparkMax.ControlType.kSmartMotion);
    }
    /**
     * @return Is the Extender arm motor at the setpoint set by m_targetRotations
     */
    public boolean isExtenderRotatorAtRotations() {
        double currentMotorRotations = m_extendMotor.getEncoder().getPosition();
        return Utility.isWithinTolerance(currentMotorRotations, m_targetRotations,
                m_config.m_rotationMotorAllowedError);
    }

    public double getExtenderMotorRotations() {
        return m_extendMotor.getEncoder().getPosition();
    }

    @Override
    public void periodic() {
        if (isClosedLoopEnabled() && isExtenderRotatorAtRotations()) {
            setClosedLoopEnabled(false);
        }
    }

}

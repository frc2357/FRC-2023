package com.team2357.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import com.team2357.lib.util.Utility;

public class ElevatorSubsystem extends ClosedLoopSubsystem {
    
    private static ElevatorSubsystem m_instance;

    public static ElevatorSubsystem getInstance() {
        return m_instance;
    }

    public static class Configuration {
        public double m_extendAxisMaxSpeed = 0;

        public IdleMode m_extendMotorIdleMode = IdleMode.kBrake;

        public int m_extendMotorStallLimitAmps = 0;
        public int m_extendMotorFreeLimitAmps = 0;

        public boolean m_isInverted = false;

        public int m_extendGrippedAmps = 0;

        // smart motion config
        public double m_extendMotorP = 0;
        public double m_extendMotorI = 0;
        public double m_extendMotorD = 0;
        public double m_extendMotorIZone = 0;
        public double m_extendMotorFF = 0;
        public double m_extendMotorMaxOutput = 0;
        public double m_extendMotorMinOutput = 0;
        public double m_extendMotorMaxRPM = 0;
        public double m_extendMotorMaxVel = 0;
        public double m_extendMotorMinVel = 0;
        public double m_extendMotorMaxAcc = 0;
        public double m_extendMotorAllowedError = 0;
        public int m_smartMotionSlot = 0;
    }

    private Configuration m_config;
    private CANSparkMax m_rightMotor;
    private CANSparkMax m_leftMotor;
    private SparkMaxPIDController m_rightPidController;
    private SparkMaxPIDController m_leftPidController;

    private double m_targetRotations;

    public ElevatorSubsystem(CANSparkMax rightMotor, CANSparkMax leftMotor) {
        m_rightMotor = rightMotor;
        m_leftMotor = leftMotor;

        m_instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;

        m_rightMotor.setIdleMode(m_config.m_extendMotorIdleMode);
        m_leftMotor.setIdleMode(m_config.m_extendMotorIdleMode);

        m_rightPidController = m_rightMotor.getPIDController();
        m_leftPidController = m_leftMotor.getPIDController();
        configureExtenderPID(m_rightPidController);
        configureExtenderPID(m_leftPidController);

        m_rightMotor.setInverted(m_config.m_isInverted);
        m_leftMotor.setInverted(m_config.m_isInverted);
    }

    private void configureExtenderPID(SparkMaxPIDController pidController) {
        pidController.setP(m_config.m_extendMotorP);
        pidController.setI(m_config.m_extendMotorI);
        pidController.setD(m_config.m_extendMotorD);
        pidController.setIZone(m_config.m_extendMotorIZone);
        pidController.setFF(m_config.m_extendMotorFF);
        pidController.setOutputRange(m_config.m_extendMotorMinOutput, m_config.m_extendMotorMaxOutput);

        // Smart motion
        pidController.setSmartMotionMaxVelocity(m_config.m_extendMotorMaxVel, m_config.m_smartMotionSlot);
        pidController.setSmartMotionMinOutputVelocity(m_config.m_extendMotorMinVel, m_config.m_smartMotionSlot);
        pidController.setSmartMotionMaxAccel(m_config.m_extendMotorMaxAcc, m_config.m_smartMotionSlot);
        pidController.setSmartMotionAllowedClosedLoopError(m_config.m_extendMotorAllowedError, m_config.m_smartMotionSlot);
    }

    public void setElevatorRotations(double rotations) {
        setClosedLoopEnabled(true);
        m_targetRotations = rotations;
        m_rightPidController.setReference(m_targetRotations, CANSparkMax.ControlType.kSmartMotion);
        m_leftPidController.setReference(m_targetRotations, CANSparkMax.ControlType.kSmartMotion);
    }

    public void extend(double sensorUnits) {
        m_rightMotor.set(sensorUnits);
        m_leftMotor.set(sensorUnits);
    }

    public void stopExtensionMotors() {
        setClosedLoopEnabled(false);
        m_rightMotor.set(0);
        m_leftMotor.set(0);
    }

    public void resetEncoders() {
        m_rightMotor.getEncoder().setPosition(0);
        m_leftMotor.getEncoder().setPosition(0);
    }

    public boolean isExtenderRotatorAtRotations() {
        double rightMotorRotations = m_rightMotor.getEncoder().getPosition();
        double leftMotorRotations = m_leftMotor.getEncoder().getPosition();

        return Utility.isWithinTolerance(leftMotorRotations, rightMotorRotations, leftMotorRotations)
    }

    @Override
    public void periodic() {
        if (isClosedLoopEnabled() && isExtenderRotatorAtRotations()) {
            setClosedLoopEnabled(false);
        }
    }


}

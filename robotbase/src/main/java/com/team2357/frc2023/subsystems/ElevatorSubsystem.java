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
        public double m_elevatorAxisMaxSpeed = 0;

        public IdleMode m_elevatorMotorIdleMode = IdleMode.kBrake;

        public int m_elevatorMotorStallLimitAmps = 0;
        public int m_elevatorMotorFreeLimitAmps = 0;

        public boolean m_isInverted = false;

        // smart motion config
        public double m_elevatorMotorP = 0;
        public double m_elevatorMotorI = 0;
        public double m_elevatorMotorD = 0;
        public double m_elevatorMotorIZone = 0;
        public double m_elevatorMotorFF = 0;
        public double m_elevatorMotorMaxOutput = 0;
        public double m_elevatorMotorMinOutput = 0;
        public double m_elevatorMotorMaxRPM = 0;
        public double m_elevatorMotorMaxVel = 0;
        public double m_elevatorMotorMinVel = 0;
        public double m_elevatorMotorMaxAcc = 0;
        public double m_elevatorMotorAllowedError = 0;
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

        m_rightMotor.setIdleMode(m_config.m_elevatorMotorIdleMode);
        m_leftMotor.setIdleMode(m_config.m_elevatorMotorIdleMode);

        m_rightPidController = m_rightMotor.getPIDController();
        m_leftPidController = m_leftMotor.getPIDController();
        configureElevatorerPID(m_rightPidController);
        configureElevatorerPID(m_leftPidController);

        m_rightMotor.setInverted(m_config.m_isInverted);
        m_leftMotor.setInverted(m_config.m_isInverted);
    }

    private void configureElevatorerPID(SparkMaxPIDController pidController) {
        pidController.setP(m_config.m_elevatorMotorP);
        pidController.setI(m_config.m_elevatorMotorI);
        pidController.setD(m_config.m_elevatorMotorD);
        pidController.setIZone(m_config.m_elevatorMotorIZone);
        pidController.setFF(m_config.m_elevatorMotorFF);
        pidController.setOutputRange(m_config.m_elevatorMotorMinOutput, m_config.m_elevatorMotorMaxOutput);

        // Smart motion
        pidController.setSmartMotionMaxVelocity(m_config.m_elevatorMotorMaxVel, m_config.m_smartMotionSlot);
        pidController.setSmartMotionMinOutputVelocity(m_config.m_elevatorMotorMinVel, m_config.m_smartMotionSlot);
        pidController.setSmartMotionMaxAccel(m_config.m_elevatorMotorMaxAcc, m_config.m_smartMotionSlot);
        pidController.setSmartMotionAllowedClosedLoopError(m_config.m_elevatorMotorAllowedError,
                m_config.m_smartMotionSlot);
    }

    public void setElevatorRotations(double rotations) {
        setClosedLoopEnabled(true);
        m_targetRotations = rotations;
        m_rightPidController.setReference(m_targetRotations, CANSparkMax.ControlType.kSmartMotion);
        m_leftPidController.setReference(m_targetRotations, CANSparkMax.ControlType.kSmartMotion);
    }

    public boolean isElevatorAtRotations() {
        return isMotorAtRotations(m_rightMotor) && isMotorAtRotations(m_leftMotor);
    }

    public boolean isMotorAtRotations(CANSparkMax motor) {
        return Utility.isWithinTolerance(motor.getEncoder().getPosition(), m_targetRotations,
                m_config.m_elevatorMotorAllowedError);
    }

    public void setElevatorAxisSpeed(double axisSpeed) {
        setClosedLoopEnabled(false);

        double motorSpeed = (-axisSpeed) * m_config.m_elevatorAxisMaxSpeed;

        m_rightMotor.set(motorSpeed);
        m_leftMotor.set(motorSpeed);
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

    public double getLeftMotorRotations() {
        return m_leftMotor.getEncoder().getPosition();
    }

    public double getRightMotorRotations() {
        return m_rightMotor.getEncoder().getPosition();
    }

    @Override
    public void periodic() {
        if (isClosedLoopEnabled() && isElevatorAtRotations()) {
            setClosedLoopEnabled(false);
        }
    }

}

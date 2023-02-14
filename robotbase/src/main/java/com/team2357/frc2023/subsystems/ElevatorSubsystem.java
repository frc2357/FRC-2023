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
        public boolean m_isFollowerInverted = false;

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
    private CANSparkMax m_masterMotor;
    private CANSparkMax m_followerMotor;
    private SparkMaxPIDController m_pidController;

    private double m_targetRotations;

    public ElevatorSubsystem(CANSparkMax masterMotor, CANSparkMax followerMotor) {
        m_masterMotor = masterMotor;
        m_followerMotor = followerMotor;

        m_instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;

        configureElevatorMotor(m_masterMotor);
        configureElevatorMotor(m_followerMotor);

        m_pidController = m_masterMotor.getPIDController();
        configureElevatorPID(m_pidController);

        m_masterMotor.setInverted(m_config.m_isInverted);
        m_followerMotor.follow(m_masterMotor, m_config.m_isFollowerInverted);
    }

    public void configureElevatorMotor(CANSparkMax motor) {
        motor.setIdleMode(m_config.m_elevatorMotorIdleMode);
        motor.setSmartCurrentLimit(m_config.m_elevatorMotorStallLimitAmps, m_config.m_elevatorMotorFreeLimitAmps);
    }

    private void configureElevatorPID(SparkMaxPIDController pidController) {
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
        m_pidController.setReference(m_targetRotations, CANSparkMax.ControlType.kSmartMotion);
    }

    public boolean isElevatorAtRotations() {
        return isMotorAtRotations(m_masterMotor) && isMotorAtRotations(m_followerMotor);
    }

    public boolean isMotorAtRotations(CANSparkMax motor) {
        return Utility.isWithinTolerance(motor.getEncoder().getPosition(), m_targetRotations,
                m_config.m_elevatorMotorAllowedError);
    }

    public void setElevatorAxisSpeed(double axisSpeed) {
        setClosedLoopEnabled(false);

        double motorSpeed = (-axisSpeed) * m_config.m_elevatorAxisMaxSpeed;

        m_masterMotor.set(motorSpeed);
    }

    public void stopExtensionMotors() {
        setClosedLoopEnabled(false);
        m_masterMotor.set(0);
    }

    public void resetEncoders() {
        m_masterMotor.getEncoder().setPosition(0);
    }

    public double getMasterMotorRotations() {
        return m_masterMotor.getEncoder().getPosition();
    }

    public double getFollowerMotorRotations() {
        return m_followerMotor.getEncoder().getPosition();
    }

    @Override
    public void periodic() {
        if (isClosedLoopEnabled() && isElevatorAtRotations()) {
            setClosedLoopEnabled(false);
        }
    }

}

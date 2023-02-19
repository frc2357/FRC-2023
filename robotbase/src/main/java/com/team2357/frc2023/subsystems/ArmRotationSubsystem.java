package com.team2357.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.SparkMaxPIDController;
import com.team2357.frc2023.shuffleboard.ShuffleboardPIDTuner;
import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import com.team2357.lib.util.Utility;

public class ArmRotationSubsystem extends ClosedLoopSubsystem {
    private static ArmRotationSubsystem instance = null;

    public static ArmRotationSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public double m_rotationAxisMaxSpeed;

        public IdleMode m_rotationMotorIdleMode;

        public int m_rotationMotorStallLimitAmps;
        public int m_rotationMotorFreeLimitAmps;

        public boolean m_isFollowerInverted;

        // smart motion config
        public double m_rotationMotorP;
        public double m_rotationMotorI;
        public double m_rotationMotorD;
        
        public double m_rotationMotorIZone;
        public double m_rotationMotorFF;
        public double m_rotationMotorMaxOutput;
        public double m_rotationMotorMinOutput;
        public double m_rotationMotorMaxRPM;
        public double m_rotationMotorMaxVel;
        public double m_rotationMotorMinVel;
        public double m_rotationMotorMaxAcc;
        public double m_rotationMotorAllowedError;
        public double m_maxSpeedPercent;
        public int m_smartMotionSlot;
    }

    private Configuration m_config;
    private CANSparkMax m_masterRotationMotor;
    private CANSparkMax m_followerRotationMotor;
    private SparkMaxPIDController m_pidController;
    private double m_targetRotations;
    private ShuffleboardPIDTuner m_shuffleboardPIDTuner;
    public ArmRotationSubsystem(CANSparkMax masterRotationMotor, CANSparkMax followerRotationMotor) {
        instance = this;
        m_masterRotationMotor = masterRotationMotor;
        m_followerRotationMotor = followerRotationMotor;
    }

    public void configure(Configuration config) {
        m_config = config;
        m_shuffleboardPIDTuner = new ShuffleboardPIDTuner("Arm Rotation",0.2,0.2,0.2,m_config.m_rotationMotorP,m_config.m_rotationMotorI,m_config.m_rotationMotorD);
        configureRotationMotor(m_masterRotationMotor);
        configureRotationMotor(m_followerRotationMotor);

        m_pidController = m_masterRotationMotor.getPIDController();
        configureRotationPID(m_pidController);

        m_masterRotationMotor.setInverted(!m_config.m_isFollowerInverted);
        m_followerRotationMotor.follow(m_masterRotationMotor, m_config.m_isFollowerInverted);
    }

    private void configureRotationMotor(CANSparkMax motor) {
        motor.setIdleMode(m_config.m_rotationMotorIdleMode);
        motor.setSmartCurrentLimit(m_config.m_rotationMotorStallLimitAmps,
                m_config.m_rotationMotorFreeLimitAmps);
    }

    private void configureRotationPID(SparkMaxPIDController pidController) {
        // set PID coefficients
        pidController.setP(m_config.m_rotationMotorP);
        pidController.setI(m_config.m_rotationMotorI);
        pidController.setD(m_config.m_rotationMotorD);
        pidController.setIZone(m_config.m_rotationMotorIZone);
        pidController.setFF(m_config.m_rotationMotorFF);
        pidController.setOutputRange(m_config.m_rotationMotorMinOutput, m_config.m_rotationMotorMinOutput);

        // Configure smart motion
        pidController.setSmartMotionMaxVelocity(m_config.m_rotationMotorMaxVel, m_config.m_smartMotionSlot);
        pidController.setSmartMotionMinOutputVelocity(m_config.m_rotationMotorMinVel, m_config.m_smartMotionSlot);
        pidController.setSmartMotionMaxAccel(m_config.m_rotationMotorMaxAcc, m_config.m_smartMotionSlot);
        pidController.setSmartMotionAllowedClosedLoopError(m_config.m_rotationMotorAllowedError, m_config.m_smartMotionSlot);
    }

    public void setRotatorRotations(double rotations) {
        setClosedLoopEnabled(true);
        m_targetRotations = rotations;
        m_pidController.setReference(m_targetRotations, CANSparkMax.ControlType.kSmartMotion);
    }

    public boolean isRotatorAtRotations() {
        return isMotorAtRotations(m_masterRotationMotor) && isMotorAtRotations(m_followerRotationMotor);
    }

    public boolean isMotorAtRotations(CANSparkMax motor) {
        return Utility.isWithinTolerance(motor.getEncoder().getPosition(), m_targetRotations,
                m_config.m_rotationMotorAllowedError);

    }

    public void setRotationAxisSpeed(double axisSpeed) {
        setClosedLoopEnabled(false);

        double motorSpeed = (-axisSpeed) * m_config.m_rotationAxisMaxSpeed;

        m_masterRotationMotor.set(motorSpeed);
    }
    //Method for the panic mode to rotate the arms
    public void manualRotate(double sensorUnits) {
        m_masterRotationMotor.set(sensorUnits*m_config.m_maxSpeedPercent);
    }

    // Method to stop the motors
    public void stopRotationMotors() {
        setClosedLoopEnabled(false);
        m_masterRotationMotor.set(0);
    }

    public void resetEncoders() {
        m_masterRotationMotor.getEncoder().setPosition(0);
    }

    public double getMasterMotorRotations() {
        return m_masterRotationMotor.getEncoder().getPosition();
    }

    public double getFollowerMotorRotations() {
        return m_followerRotationMotor.getEncoder().getPosition();
    }
    public void updatePID() {
        m_pidController.setP(m_shuffleboardPIDTuner.getDouble("P"));
        m_pidController.setI(m_shuffleboardPIDTuner.getDouble("I"));
        m_pidController.setD(m_shuffleboardPIDTuner.getDouble("D"));
    }
    @Override
    public void periodic() {
        if (isClosedLoopEnabled() && isRotatorAtRotations()) {
            setClosedLoopEnabled(false);
        }
        if(m_shuffleboardPIDTuner.arePIDsUpdated(m_pidController.getP(), m_pidController.getI(), m_pidController.getD())){
            updatePID();
        }
    }
}
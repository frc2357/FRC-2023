package com.team2357.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.SparkMaxPIDController;
import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import com.team2357.lib.util.Utility;
import com.team2357.frc2023.Constants;
public class ArmRotationSubsystem extends ClosedLoopSubsystem{
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

    public ArmRotationSubsystem(CANSparkMax masterRotationMotor, CANSparkMax followerRotationMotor) {
        instance = this;
        m_masterRotationMotor = masterRotationMotor;
        m_followerRotationMotor = followerRotationMotor;
    }

    public void configure(Configuration config) {
        m_config = config;

        configureRotationMotor(m_masterRotationMotor);
        configureRotationMotor(m_followerRotationMotor);

        m_pidController = m_masterRotationMotor.getPIDController();
        configureRotationPID(m_pidController);

        m_masterRotationMotor.setInverted(!m_config.m_isFollowerInverted);
        m_followerRotationMotor.follow(m_masterRotationMotor,m_config.m_isFollowerInverted);
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

    public void rotate(double sensorUnits){
        m_masterRotationMotor.set(sensorUnits*m_config.m_maxSpeedPercent);
    }

    public void setRotationAxisSpeed(double axisSpeed) {
        setClosedLoopEnabled(false);

        double motorSpeed = (-axisSpeed) * m_config.m_rotationAxisMaxSpeed;

        m_masterRotationMotor.set(motorSpeed);
        m_followerRotationMotor.set(motorSpeed);
    }

    // Method to stop the motors
    public void stopRotationMotors() {
        setClosedLoopEnabled(false);
        m_masterRotationMotor.set(0);
        m_followerRotationMotor.set(0);
    }

    public void resetEncoders() {
        m_masterRotationMotor.getEncoder().setPosition(0);
        m_followerRotationMotor.getEncoder().setPosition(0);
    }
    public boolean isRotatorAtRotations() {
        return  isMasterRotatorAtRotations()&& isFollowerRotatorAtRotations();
    }

    /**
     * @return Is the master arm motor at the setpoint set by m_targetRotations
     */
    public boolean isMasterRotatorAtRotations() {
        double currentMotorRotations = m_masterRotationMotor.getEncoder().getPosition();
        return Utility.isWithinTolerance(currentMotorRotations, m_targetRotations,
                m_config.m_rotationMotorAllowedError);
    }

    /**
     * @return Is the follower arm motor at the setpoint set by m_targetRotations
     */
    public boolean isFollowerRotatorAtRotations() {
        double currentMotorRotations = m_followerRotationMotor.getEncoder().getPosition();
        return Utility.isWithinTolerance(currentMotorRotations, m_targetRotations,
                m_config.m_rotationMotorAllowedError);
    }
    public double getMasterMotorRotations() {
        return m_masterRotationMotor.getEncoder().getPosition();
    }

    public double getFollowerMotorRotations() {
        return m_followerRotationMotor.getEncoder().getPosition();
    }

    @Override
    public void periodic() {
        if (isClosedLoopEnabled() && isRotatorAtRotations()) {
            setClosedLoopEnabled(false);
        }
    }
}
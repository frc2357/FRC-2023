package com.team2357.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.revrobotics.SparkMaxPIDController;
import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import com.team2357.lib.util.Utility;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ArmRotationSubsystem extends ClosedLoopSubsystem {
    private static ArmRotationSubsystem instance = null;

    public static ArmRotationSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public boolean m_isEncoderInverted;
        public double m_encoderOffset;
        
        public double m_rotationZeroTolerance;
        
        public double m_rotationAxisMaxSpeed;

        public IdleMode m_rotationMotorIdleMode;

        public int m_rotationMotorStallLimitAmps;
        public int m_rotationMotorFreeLimitAmps;

        public boolean m_isInverted;

        public double m_shuffleboardTunerPRange;
        public double m_shuffleboardTunerIRange;
        public double m_shuffleboardTunerDRange;

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

        // Arm Feedforward

        /**
         * Static gain for feed forward
         */
        public double m_feedforwardKs;

        /**
         * Gravity gain for feed forward
         */
        public double m_feedforwardKg;

        /**
         * Velocity gain for feed forward
         */
        public double m_feedforwardKv;

        /**
         * Acceleration gain for feed forward
         */
        public double m_feedforwardKa;

        /**
         * Number of rotations the arm is at when parallel with the floor
         */
        public double m_armHorizontalRotations;

        /**
         * How many motor rotations = 1 radian
         */
        public double m_rotationsPerRadian;
    }

    private Configuration m_config;
    private CANSparkMax m_rotationMotor;

    private SparkMaxPIDController m_pidController;
    private SparkMaxAbsoluteEncoder m_absoluteEncoder;
    private ArmFeedforward m_feedforward;

    private double m_targetRotations;

    public ArmRotationSubsystem(int motorId) {
        instance = this;
        m_rotationMotor = new CANSparkMax(motorId, MotorType.kBrushless);
    }

    public void configure(Configuration config) {
        m_config = config;
        configureRotationMotor(m_rotationMotor);

        m_pidController = m_rotationMotor.getPIDController();
        configureRotationPID(m_pidController);
        m_pidController.setFeedbackDevice(m_rotationMotor.getEncoder());

        m_rotationMotor.setInverted(m_config.m_isInverted);

        m_feedforward = new ArmFeedforward(m_config.m_feedforwardKs, m_config.m_feedforwardKg, m_config.m_feedforwardKv,
                m_config.m_feedforwardKa);

        m_absoluteEncoder = m_rotationMotor.getAbsoluteEncoder(Type.kDutyCycle);
        m_absoluteEncoder.setInverted(m_config.m_isEncoderInverted);
        m_absoluteEncoder.setZeroOffset(m_config.m_encoderOffset);
        
        resetEncoder();
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
        pidController.setOutputRange(m_config.m_rotationMotorMinOutput, m_config.m_rotationMotorMaxOutput);

        // Configure smart motion
        pidController.setSmartMotionMaxVelocity(m_config.m_rotationMotorMaxVel, m_config.m_smartMotionSlot);
        pidController.setSmartMotionMinOutputVelocity(m_config.m_rotationMotorMinVel, m_config.m_smartMotionSlot);
        pidController.setSmartMotionMaxAccel(m_config.m_rotationMotorMaxAcc, m_config.m_smartMotionSlot);
        pidController.setSmartMotionAllowedClosedLoopError(m_config.m_rotationMotorAllowedError,
                m_config.m_smartMotionSlot);
    }

    public void setRotations(double rotations) {
        setClosedLoopEnabled(true);
        m_targetRotations = rotations;
    }

    /**
     * 
     * @param rotations The rotation setpoint
     * @return The radians to input into feed forward calculation
     */
    public double calculateFeedforwardRadians(double rotations) {

        return (rotations - m_config.m_armHorizontalRotations) / m_config.m_rotationsPerRadian;
    }

    public boolean isAtRotations() {
        return Utility.isWithinTolerance(getMotorRotations(), m_targetRotations,
                m_config.m_rotationMotorAllowedError);
    }

    public void setRotationAxisSpeed(double axisSpeed) {
        double motorSpeed = (-axisSpeed) * m_config.m_rotationAxisMaxSpeed;

        m_rotationMotor.set(motorSpeed);
    }

    public void endManualControl() {
        stopRotationMotors();
        m_targetRotations = getMotorRotations();
        setClosedLoopEnabled(true);
    }

    // Method for the panic mode to rotate the arms
    public void manualRotate(double percentSpeed) {
        m_rotationMotor.set(percentSpeed);
    }

    // Method to stop the motors
    public void stopRotationMotors() {
        setClosedLoopEnabled(false);
        m_rotationMotor.set(0);
    }

    public void resetEncoder() {
        m_rotationMotor.getEncoder().setPosition(0);
        m_targetRotations = 0;
    }

    public double getMotorRotations() {
        return m_rotationMotor.getEncoder().getPosition();
    }

    public void setTargetRotationsToCurrentRotations(){
        m_targetRotations = m_rotationMotor.getEncoder().getPosition();
    }

    public double getAmps() {
        return m_rotationMotor.getOutputCurrent();
    }

    public boolean isZeroed() {
        return (m_targetRotations >= -m_config.m_rotationZeroTolerance && m_targetRotations <= m_config.m_rotationZeroTolerance) && isAtRotations();
    }

    public double getAbsoluteEncoderPosition() {
        return m_absoluteEncoder.getPosition();
    }

    @Override
    public void periodic() {
        if (isClosedLoopEnabled()) {
            double feedforwardVolts = m_feedforward.calculate(calculateFeedforwardRadians(m_targetRotations), 0);
            m_pidController.setReference(m_targetRotations, CANSparkMax.ControlType.kSmartMotion,
                   m_config.m_smartMotionSlot, feedforwardVolts, ArbFFUnits.kVoltage);
        }
     
        //System.out.println("Speed: " + m_rotationMotor.getAppliedOutput() + "Amp: " + getAmps());

        // System.out.println("Current rot: " + getMotorRotations() + " target: " + m_targetRotations );
        // System.out.println("Angle: " + m_absoluteEncoder.getPosition());
        // SmartDashboard.putNumber("Arm Rotations", getMotorRotations());
    }
}
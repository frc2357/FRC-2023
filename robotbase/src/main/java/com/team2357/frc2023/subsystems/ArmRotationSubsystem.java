package com.team2357.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.revrobotics.SparkMaxPIDController;
import com.team2357.frc2023.shuffleboard.ShuffleboardPIDTuner;
import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import com.team2357.lib.util.Utility;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.wpilibj.DriverStation;

public class ArmRotationSubsystem extends ClosedLoopSubsystem {
    private static ArmRotationSubsystem instance = null;

    public static ArmRotationSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public double m_positionZeroTolerance;

        public double m_rotationAxisMaxSpeed;

        public IdleMode m_rotationMotorIdleMode;

        public int m_rotationMotorStallLimitAmps;
        public int m_rotationMotorFreeLimitAmps;

        public boolean m_isInverted;

        public double m_shuffleboardTunerPRange;
        public double m_shuffleboardTunerIRange;
        public double m_shuffleboardTunerDRange;

        // smart motion config
        public double m_positionP;
        public double m_positionI;
        public double m_positionD;

        public double m_positionIZone;
        public double m_positionFF;
        public double m_positionMaxOutput;
        public double m_positionMinOutput;
        public double m_positionMaxRPM;
        public double m_positionMaxVel;
        public double m_positionMinVel;
        public double m_positionMaxAcc;
        public double m_positionAllowedError;
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
         * Zero position for the arm
         */
        public double m_zeroPosition;

        /**
         * Position the arm is at when parallel with the floor
         */
        public double m_armHorizontalPosition;

        /**
         * How many motor position = 1 radian
         */
        public double m_positionToRadian;

        /**
         * Is absolute encoder inverted
         */
        public boolean m_isEncoderInverted;

        /**
         * The offset for the absolute encoder
         */
        public double m_encoderOffset;

        /**
         * The lower allowable limit of position from 0-1
         */
        public double m_lowerPositionLimit;

        /**
         * The upper allowable limit of position from 0-1
         */
        public double m_upperPositionLimit;
    }

    private Configuration m_config;
    private CANSparkMax m_rotationMotor;

    private SparkMaxPIDController m_pidController;
    private SparkMaxAbsoluteEncoder m_encoder;
    private ArmFeedforward m_feedforward;

    private double m_targetPosition;
    private ShuffleboardPIDTuner m_shuffleboardPIDTuner;

    public ArmRotationSubsystem(int motorId) {
        instance = this;
        m_rotationMotor = new CANSparkMax(motorId, MotorType.kBrushless);
    }

    public void configure(Configuration config) {
        m_config = config;
        m_shuffleboardPIDTuner = new ShuffleboardPIDTuner("Arm Rotation", config.m_shuffleboardTunerPRange,
                m_config.m_shuffleboardTunerIRange, m_config.m_shuffleboardTunerDRange, m_config.m_positionP,
                m_config.m_positionI, m_config.m_positionD);
        configureRotationMotor(m_rotationMotor);

        m_pidController = m_rotationMotor.getPIDController();
        configurePositionPID(m_pidController);

        m_rotationMotor.setInverted(m_config.m_isInverted);

        m_feedforward = new ArmFeedforward(m_config.m_feedforwardKs, m_config.m_feedforwardKg, m_config.m_feedforwardKv,
                m_config.m_feedforwardKa);

        m_encoder = m_rotationMotor.getAbsoluteEncoder(Type.kDutyCycle);
        m_encoder.setInverted(m_config.m_isEncoderInverted);
        m_encoder.setZeroOffset(m_config.m_encoderOffset);
        m_pidController.setFeedbackDevice(m_encoder);
    }

    private void configureRotationMotor(CANSparkMax motor) {
        motor.setIdleMode(m_config.m_rotationMotorIdleMode);
        motor.setSmartCurrentLimit(m_config.m_rotationMotorStallLimitAmps,
                m_config.m_rotationMotorFreeLimitAmps);
    }

    private void configurePositionPID(SparkMaxPIDController pidController) {
        // set PID coefficients
        pidController.setP(m_config.m_positionP);
        pidController.setI(m_config.m_positionI);
        pidController.setD(m_config.m_positionD);
        pidController.setIZone(m_config.m_positionIZone);
        pidController.setFF(m_config.m_positionFF);
        pidController.setOutputRange(m_config.m_positionMinOutput, m_config.m_positionMaxOutput);

        // Configure smart motion
        pidController.setSmartMotionMaxVelocity(m_config.m_positionMaxVel, m_config.m_smartMotionSlot);
        pidController.setSmartMotionMinOutputVelocity(m_config.m_positionMinVel, m_config.m_smartMotionSlot);
        pidController.setSmartMotionMaxAccel(m_config.m_positionMaxAcc, m_config.m_smartMotionSlot);
        pidController.setSmartMotionAllowedClosedLoopError(m_config.m_positionAllowedError,
                m_config.m_smartMotionSlot);
    }

    public double getZeroPosition() {
        return m_config.m_zeroPosition;
    }

    public void setPosition(double position) {
        if (position < m_config.m_lowerPositionLimit || position > m_config.m_upperPositionLimit) {
            DriverStation.reportError("ARM TRYING TO GO BEYOND LIMITS", false);
            return;
        }
        setClosedLoopEnabled(true);
        m_targetPosition = position;
    }

    /**
     * 
     * @param position The position setpoint
     * @return The radians to input into feed forward calculation
     */
    public double calculateFeedforwardRadians(double position) {
        return (position - m_config.m_armHorizontalPosition) / m_config.m_positionToRadian;
    }

    public boolean isAtPosition() {
        return Utility.isWithinTolerance(getMotorPosition(), m_targetPosition,
                m_config.m_positionAllowedError);
    }

    public void setRotationAxisSpeed(double axisSpeed) {
        double motorSpeed = (-axisSpeed) * m_config.m_rotationAxisMaxSpeed;

        m_rotationMotor.set(motorSpeed);
    }

    public void endAxisCommand() {
        stopRotationMotors();
        setTargetPositionToCurrentPosition();
        setClosedLoopEnabled(true);
    }

    // Method for the panic mode to rotate the arms
    public void manualRotate(double sensorUnits) {
        m_rotationMotor.set(sensorUnits);
    }

    // Method to stop the motors
    public void stopRotationMotors() {
        setClosedLoopEnabled(false);
        m_rotationMotor.set(0);
    }

    public double getMotorPosition() {
        return m_encoder.getPosition();
    }

    public void setTargetPositionToCurrentPosition() {
        m_targetPosition = getMotorPosition();
    }

    public void updatePID() {
        m_pidController.setP(m_shuffleboardPIDTuner.getPValue());
        m_pidController.setI(m_shuffleboardPIDTuner.getIValue());
        m_pidController.setD(m_shuffleboardPIDTuner.getDValue());
    }

    public double getAmps() {
        return m_rotationMotor.getOutputCurrent();
    }

    public boolean isZeroed() {
        return (m_targetPosition >= -m_config.m_positionZeroTolerance
                && m_targetPosition <= m_config.m_positionZeroTolerance) && isAtPosition();
    }

    @Override
    public void periodic() {
        if (isClosedLoopEnabled()) {
            double feedforwardVolts = m_feedforward.calculate(calculateFeedforwardRadians(m_targetPosition), 0);
            m_pidController.setReference(m_targetPosition, CANSparkMax.ControlType.kSmartMotion,
                    m_config.m_smartMotionSlot, feedforwardVolts, ArbFFUnits.kVoltage);
        }
        if (m_shuffleboardPIDTuner.arePIDsUpdated()) {
            updatePID();
        }

        // System.out.println("Speed: " + m_rotationMotor.getAppliedOutput() + "Amp: " +
        // getAmps());

        // System.out.println("Current rot: " + getMotorRotations());
        // SmartDashboard.putNumber("Rotations", getMotorRotations());
    }
}
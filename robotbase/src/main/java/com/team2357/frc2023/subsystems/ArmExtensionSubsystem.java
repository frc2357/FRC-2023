package com.team2357.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team2357.frc2023.shuffleboard.ShuffleboardPIDTuner;
import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import com.team2357.lib.util.Utility;

public class ArmExtensionSubsystem extends ClosedLoopSubsystem {
    private static ArmExtensionSubsystem instance = null;

    public static ArmExtensionSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public double m_extendAxisMaxSpeed;
        public double m_maxSpeedPercent;

        public IdleMode m_extendMotorIdleMode = IdleMode.kBrake;

        public int m_extendMotorStallLimitAmps;
        public int m_extendMotorFreeLimitAmps;

        public boolean m_isInverted = false;

        public int m_extendGrippedAmps;

        public double m_shuffleboardTunerPRange;
        public double m_shuffleboardTunerIRange;
        public double m_shuffleboardTunerDRange;

        // PID for smart motion

        // Extend PID
        public double m_extendP;
        public double m_extendI;
        public double m_extendD;
        public double m_extendIZone;
        public double m_extendFF;
        public int m_extendPidSlot;

        // Retract PID
        public double m_retractP;
        public double m_retractI;
        public double m_retractD;
        public double m_retractIZone;
        public double m_retractFF;
        public int m_retractPidSlot;

        // Smart motion
        public double m_pidMaxOutput;
        public double m_pidMinOutput;
        public double m_smartMotionMaxVelRPM;
        public double m_smartMotionMinVelRPM;
        public double m_smartMotionMaxAccRPM;
        public double m_smartMotionRotationAllowedError;

        public double m_rotationAllowedError;

        public int m_extendMotorRampRate;
    }

    private Configuration m_config;
    private CANSparkMax m_extendMotor;
    private SparkMaxPIDController m_pidcontroller;
    private double m_targetRotations;
    private ShuffleboardPIDTuner m_shuffleboardPIDTuner;

    public ArmExtensionSubsystem(int motorId) {
        m_extendMotor = new CANSparkMax(motorId, MotorType.kBrushless);
        instance = this;

    }

    public void configure(Configuration config) {
        m_config = config;
        m_shuffleboardPIDTuner = new ShuffleboardPIDTuner("Arm Extension", m_config.m_shuffleboardTunerPRange,
                m_config.m_shuffleboardTunerIRange, m_config.m_shuffleboardTunerDRange, m_config.m_extendP,
                m_config.m_extendI, m_config.m_extendD);
        m_extendMotor.setIdleMode(m_config.m_extendMotorIdleMode);
        m_extendMotor.setSmartCurrentLimit(m_config.m_extendMotorStallLimitAmps, m_config.m_extendMotorFreeLimitAmps);
        m_pidcontroller = m_extendMotor.getPIDController();
        configureExtenderPID(m_pidcontroller);

        m_extendMotor.setInverted(m_config.m_isInverted);
        m_extendMotor.setOpenLoopRampRate(m_config.m_extendMotorRampRate);

    }

    public void manualExtend(double proportion) {
        m_extendMotor.set(proportion * m_config.m_maxSpeedPercent);
    }

    private void configureExtenderPID(SparkMaxPIDController pidController) {
        // set PID coefficients for extension
        pidController.setP(m_config.m_extendP, m_config.m_extendPidSlot);
        pidController.setI(m_config.m_extendI, m_config.m_extendPidSlot);
        pidController.setD(m_config.m_extendD, m_config.m_extendPidSlot);
        pidController.setIZone(m_config.m_extendIZone, m_config.m_extendPidSlot);
        pidController.setFF(m_config.m_extendFF, m_config.m_extendPidSlot);

        // Set PID coefficeints for retraction
        pidController.setP(m_config.m_retractP, m_config.m_retractPidSlot);
        pidController.setI(m_config.m_retractI, m_config.m_retractPidSlot);
        pidController.setD(m_config.m_retractD, m_config.m_retractPidSlot);
        pidController.setIZone(m_config.m_retractIZone, m_config.m_retractPidSlot);
        pidController.setFF(m_config.m_retractFF, m_config.m_retractPidSlot);

        configureSmartMotion(pidController, m_config.m_extendPidSlot);
        configureSmartMotion(pidController, m_config.m_retractPidSlot);
    }

    public void configureSmartMotion(SparkMaxPIDController pidController, int pidSlot) {
        pidController.setOutputRange(m_config.m_pidMinOutput, m_config.m_pidMaxOutput, pidSlot);
        pidController.setSmartMotionMaxVelocity(m_config.m_smartMotionMaxVelRPM, pidSlot);
        pidController.setSmartMotionMinOutputVelocity(m_config.m_smartMotionMinVelRPM, pidSlot);
        pidController.setSmartMotionMaxAccel(m_config.m_smartMotionMaxAccRPM, pidSlot);
        pidController.setSmartMotionAllowedClosedLoopError(m_config.m_smartMotionRotationAllowedError,
                pidSlot);
    }

    public void stopMotor() {
        setClosedLoopEnabled(false);
        m_extendMotor.set(0);
    }

    public void resetEncoder() {
        m_extendMotor.getEncoder().setPosition(0);
    }

    public void setExtensionRotations(double rotations) {
        setClosedLoopEnabled(true);
        m_targetRotations = rotations;
        m_pidcontroller.setReference(m_targetRotations, CANSparkMax.ControlType.kSmartMotion, m_config.m_extendPidSlot);
    }

    public void setRetractionRotations(double rotations) {
        setClosedLoopEnabled(true);
        m_targetRotations = rotations;
        m_pidcontroller.setReference(m_targetRotations, CANSparkMax.ControlType.kSmartMotion, m_config.m_retractPidSlot);
    }

    /**
     * @return Is the Extender arm motor at the setpoint set by m_targetRotations
     */
    public boolean isMotorAtRotations() {
        double currentMotorRotations = m_extendMotor.getEncoder().getPosition();
        return Utility.isWithinTolerance(currentMotorRotations, m_targetRotations,
                m_config.m_rotationAllowedError);
    }

    public double getMotorRotations() {
        return m_extendMotor.getEncoder().getPosition();
    }

    public void updatePID() {
        m_pidcontroller.setP(m_shuffleboardPIDTuner.getPValue());
        m_pidcontroller.setI(m_shuffleboardPIDTuner.getIValue());
        m_pidcontroller.setD(m_shuffleboardPIDTuner.getDValue());
    }

    @Override
    public void periodic() {
        if (m_shuffleboardPIDTuner.arePIDsUpdated()) {
            updatePID();
        }
        if (isClosedLoopEnabled() && isMotorAtRotations()) {
            setClosedLoopEnabled(false);
        }
    }
}

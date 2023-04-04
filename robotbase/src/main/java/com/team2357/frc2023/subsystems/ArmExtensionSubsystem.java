package com.team2357.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import com.team2357.lib.util.Utility;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

    public ArmExtensionSubsystem(int motorId) {
        m_extendMotor = new CANSparkMax(motorId, MotorType.kBrushless);
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
        m_extendMotor.enableVoltageCompensation(12);

        resetEncoder();
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

        configureSmartMotion(pidController, m_config.m_extendPidSlot);
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
        m_targetRotations = 0;
    }

    private void setMotorRotations(double rotations, int pidSlot) {
        setClosedLoopEnabled(true);
        m_targetRotations = rotations;
        m_pidcontroller.setReference(m_targetRotations, CANSparkMax.ControlType.kSmartMotion, pidSlot);
    }

    public void setExtensionRotations(double rotations) {
        setMotorRotations(rotations, m_config.m_extendPidSlot);
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

    public double getAmps() {
        return m_extendMotor.getOutputCurrent();
    }

    @Override
    public void periodic() {
        if (isClosedLoopEnabled() && isMotorAtRotations()) {
            setClosedLoopEnabled(false);
        }

       SmartDashboard.putNumber("arm extension rot", getMotorRotations());
       //System.out.println("Arm ext: " + getMotorRotations());
    }
}

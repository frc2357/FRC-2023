package com.team2357.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;

public class ArmExtender {
    public static class Configuration
    {
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
    }
    private Configuration m_config;
    private CANSparkMax m_extenderMotor;
    private SparkMaxPIDController m_pidcontroller;

    public ArmExtender(CANSparkMax extender,int currentlimit,double ramprate){
        m_extenderMotor = extender;
        m_extenderMotor.setSmartCurrentLimit(currentlimit);
        m_extenderMotor.setOpenLoopRampRate(ramprate);
    }
    public void configure(Configuration config) {
        m_config = config;

        m_extenderMotor.setIdleMode(m_config.m_extendMotorIdleMode);
        m_extenderMotor.setSmartCurrentLimit(m_config.m_extendMotorStallLimitAmps,m_config.m_extendMotorFreeLimitAmps);

        m_pidcontroller = m_extenderMotor.getPIDController();
        configureClimberPID(m_pidcontroller);

        m_extenderMotor.setInverted(m_config.m_isInverted);
    }
    public void extend(double sensorUnits) {
        m_extenderMotor.set(sensorUnits);
    }
    private void configureClimberPID(SparkMaxPIDController pidController) {
        // set PID coefficients
        pidController.setP(m_config.m_extendMotorP);
        pidController.setI(m_config.m_extendMotorI);
        pidController.setD(m_config.m_extendMotorD);
        pidController.setIZone(m_config.m_extendMotorIZone);
        pidController.setFF(m_config.m_extendMotorFF);
        pidController.setOutputRange(m_config.m_extendMotorMinOutput, m_config.m_extendMotorMinOutput);

        // Configure smart motion
        int smartMotionSlot = 0;
        pidController.setSmartMotionMaxVelocity(m_config.m_extendMotorMaxVel, smartMotionSlot);
        pidController.setSmartMotionMinOutputVelocity(m_config.m_extendMotorMinVel, smartMotionSlot);
        pidController.setSmartMotionMaxAccel(m_config.m_extendMotorMaxAcc, smartMotionSlot);
        pidController.setSmartMotionAllowedClosedLoopError(m_config.m_extendMotorAllowedError, smartMotionSlot);
    }

}

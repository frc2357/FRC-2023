package com.team2357.frc2023.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team2357.frc2023.Constants;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeRollerSubsystem extends SubsystemBase {
    public static IntakeRollerSubsystem instance = null;

    private CANSparkMax m_topIntakeMotor;
    private TalonFX m_bottomIntakeMotor;

    // Used for SmartDashboard. Set in constants for permanent numbers
    private double m_topMotorIntakePO = Constants.INTAKE_ROLLER.TOP_MOTOR_INTAKE_PERCENT_OUTPUT;
    private double m_bottomMotorIntakePO = Constants.INTAKE_ROLLER.BOTTOM_MOTOR_INTAKE_PERCENT_OUTPUT;
    private double m_topMotorEjectPO = Constants.INTAKE_ROLLER.TOP_MOTOR_EJECT_PERCENT_OUTPUT;
    private double m_bottomMotorEjectPO = Constants.INTAKE_ROLLER.BOTTOM_MOTOR_EJECT_PERCENT_OUTPUT;
    private double m_topMotorIndexPO = Constants.INTAKE_ROLLER.TOP_MOTOR_INDEX_PERCENT_OUTPUT;
    private double m_bottomMotorIndexPO = Constants.INTAKE_ROLLER.BOTTOM_MOTOR_INDEX_PERCENT_OUTPUT;
    private double m_topMotorRollPO = Constants.INTAKE_ROLLER.TOP_MOTOR_ROLL_PERCENT_OUTPUT;
    private double m_bottomMotorRollPO = Constants.INTAKE_ROLLER.BOTTOM_MOTOR_ROLL_PERCENT_OUTPUT;

    public static IntakeRollerSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        public double m_runPercentOutput;
        public double m_reversePercentOutput;

        public double m_rollerAxisMaxSpeed;

        public double m_rampRate;

        public int m_peakCurrentLimit;
        public int m_peakCurrentDuration;
        public int m_continuousCurrentLimit;
        
        public boolean m_masterInverted;
        public boolean m_followerInverted;

    }

    public Configuration m_config;

    public IntakeRollerSubsystem(int masterIntakeMotorId, int followerIntakeMotorId) {
        m_topIntakeMotor = new CANSparkMax(masterIntakeMotorId, MotorType.kBrushless);
        m_bottomIntakeMotor = new TalonFX(followerIntakeMotorId, "CANivore");

        initSmartDashboard();

        instance = this;
    }

    private void initSmartDashboard() {
        SmartDashboard.putNumber("(PICKUP) Top Motor Percent Output", m_topMotorIntakePO);
        SmartDashboard.putNumber("(PICKUP) Bottom Motor Percent Output", m_bottomMotorIntakePO);
        SmartDashboard.putNumber("(EJECT) Top Motor Percent Output", m_topMotorEjectPO);
        SmartDashboard.putNumber("(EJECT) Bottom Motor Percent Output", m_bottomMotorEjectPO);
        SmartDashboard.putNumber("(INDEX) Top Motor Percent Output", m_topMotorIndexPO);
        SmartDashboard.putNumber("(INDEX) Bottom Motor Percent Output", m_bottomMotorIndexPO);
        SmartDashboard.putNumber("(ROLL) Top Motor Percent Output", m_topMotorRollPO);
        SmartDashboard.putNumber("(ROLL) Bottom Motor Percent Output", m_bottomMotorRollPO);
    }

    public void configure(Configuration config) {
        m_config = config;

        m_topIntakeMotor.setInverted(m_config.m_masterInverted);
        m_bottomIntakeMotor.setInverted(m_config.m_followerInverted);

        // m_bottomIntakeMotor.follow(m_topIntakeMotor);

        m_topIntakeMotor.setIdleMode(IdleMode.kBrake);
        m_topIntakeMotor.setSmartCurrentLimit(m_config.m_peakCurrentLimit, m_config.m_continuousCurrentLimit);

        m_bottomIntakeMotor.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 25, 30, 0));
        m_bottomIntakeMotor.configOpenloopRamp(m_config.m_rampRate);
        m_bottomIntakeMotor.configClosedloopRamp(m_config.m_rampRate);
        m_bottomIntakeMotor.setInverted(m_config.m_followerInverted);
        // m_bottomIntakeMotor.setIdleMode(IdleMode.kBrake);
        // m_bottomIntakeMotor.setSmartCurrentLimit(m_config.m_peakCurrentLimit, m_config.m_continuousCurrentLimit);

        // m_topIntakeMotor.setNeutralMode(NeutralMode.Brake);
        // m_topIntakeMotor.enableCurrentLimit(true);
        // m_topIntakeMotor.configPeakCurrentLimit(m_config.m_peakCurrentLimit);
        // m_topIntakeMotor.configPeakCurrentDuration(m_config.m_peakCurrentDuration);
        // m_topIntakeMotor.configContinuousCurrentLimit(m_config.m_continuousCurrentLimit);

        // m_bottomIntakeMotor.setNeutralMode(NeutralMode.Brake);
        // m_bottomIntakeMotor.enableCurrentLimit(true);
        // m_bottomIntakeMotor.configPeakCurrentLimit(m_config.m_peakCurrentLimit);
        // m_bottomIntakeMotor.configPeakCurrentDuration(m_config.m_peakCurrentDuration);
        // m_bottomIntakeMotor.configContinuousCurrentLimit(m_config.m_continuousCurrentLimit);
    }

    public void runIntake(boolean reverse) {
        if (reverse) {
            runIntake(m_config.m_reversePercentOutput);
        } else {
            runIntake(m_config.m_runPercentOutput);
        }
    }

    public void runIntake(double percentOutput) {
        m_topIntakeMotor.set(percentOutput);
    }

    public void setAxisRollerSpeed(double axisSpeed) {
        double motorSpeed = (-axisSpeed) * m_config.m_rollerAxisMaxSpeed;

        m_topIntakeMotor.set(motorSpeed);
    }

    public void manualRunIntake(double percentOutput) {
        m_topIntakeMotor.set(percentOutput);
    }

    public double getCurrent() {
        return 0;
    }

    public boolean isStalled(double stallCurrent) {
        return (0 >= stallCurrent) ||
            (m_bottomIntakeMotor.getStatorCurrent() >= stallCurrent);
    }

    public void intakeCube() {
        m_topIntakeMotor.set(m_topMotorIntakePO);
        m_bottomIntakeMotor.set(ControlMode.PercentOutput, m_bottomMotorIntakePO);
    }

    public void rollCube() {
        m_topIntakeMotor.set(m_topMotorRollPO);
        m_bottomIntakeMotor.set(ControlMode.PercentOutput, m_bottomMotorRollPO);
    
    }

    public void indexCube() {
        m_topIntakeMotor.set(m_topMotorIndexPO);
        m_bottomIntakeMotor.set(ControlMode.PercentOutput, m_bottomMotorIndexPO);
    
    }

    public void ejectCube() {
        m_topIntakeMotor.set(m_topMotorEjectPO);
        m_bottomIntakeMotor.set(ControlMode.PercentOutput, m_bottomMotorEjectPO);
    }


    public void stopIntake() {
        m_topIntakeMotor.set(0.0);
        m_bottomIntakeMotor.set(ControlMode.PercentOutput, 0.0);
    }

    @Override
    public void periodic() {
        m_topMotorIntakePO = SmartDashboard.getNumber("(PICKUP) Top Motor Percent Output", 0);
        m_topMotorEjectPO = SmartDashboard.getNumber("(EJECT) Top Motor Percent Output", 0);
        m_topMotorIndexPO = SmartDashboard.getNumber("(INDEX) Top Motor Percent Output", 0);
        m_topMotorRollPO = SmartDashboard.getNumber("(ROLL) Top Motor Percent Output", 0);
        m_bottomMotorIntakePO = SmartDashboard.getNumber("(PICKUP) Bottom Motor Percent Output", 0);
        m_bottomMotorEjectPO = SmartDashboard.getNumber("(EJECT) Bottom Motor Percent Output", 0);
        m_bottomMotorIndexPO = SmartDashboard.getNumber("(INDEX) Bottom Motor Percent Output", 0);
        m_bottomMotorRollPO = SmartDashboard.getNumber("(ROLL) Bottom Motor Percent Output", 0);
    }
}
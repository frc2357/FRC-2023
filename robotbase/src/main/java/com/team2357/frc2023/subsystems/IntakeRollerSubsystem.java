package com.team2357.frc2023.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2357.frc2023.Constants;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeRollerSubsystem extends SubsystemBase {
    public static IntakeRollerSubsystem instance = null;

    private TalonSRX m_topIntakeMotor;
    private TalonSRX m_bottomIntakeMotor;

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
        m_topIntakeMotor = new TalonSRX(masterIntakeMotorId);
        m_bottomIntakeMotor = new TalonSRX(followerIntakeMotorId);

        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;

        m_topIntakeMotor.setInverted(m_config.m_masterInverted);
        m_bottomIntakeMotor.setInverted(m_config.m_followerInverted);

        // m_bottomIntakeMotor.follow(m_topIntakeMotor);

        m_topIntakeMotor.setNeutralMode(NeutralMode.Brake);
        m_topIntakeMotor.enableCurrentLimit(true);
        m_topIntakeMotor.configPeakCurrentLimit(m_config.m_peakCurrentLimit);
        m_topIntakeMotor.configPeakCurrentDuration(m_config.m_peakCurrentDuration);
        m_topIntakeMotor.configContinuousCurrentLimit(m_config.m_continuousCurrentLimit);

        m_bottomIntakeMotor.setNeutralMode(NeutralMode.Brake);
        m_bottomIntakeMotor.enableCurrentLimit(true);
        m_bottomIntakeMotor.configPeakCurrentLimit(m_config.m_peakCurrentLimit);
        m_bottomIntakeMotor.configPeakCurrentDuration(m_config.m_peakCurrentDuration);
        m_bottomIntakeMotor.configContinuousCurrentLimit(m_config.m_continuousCurrentLimit);
    }

    public void runIntake(boolean reverse) {
        if (reverse) {
            runIntake(m_config.m_reversePercentOutput);
        } else {
            runIntake(m_config.m_runPercentOutput);
        }
    }

    public void runIntake(double percentOutput) {
        m_topIntakeMotor.set(ControlMode.PercentOutput, percentOutput);
    }

    public void setAxisRollerSpeed(double axisSpeed) {
        double motorSpeed = (-axisSpeed) * m_config.m_rollerAxisMaxSpeed;

        m_topIntakeMotor.set(ControlMode.PercentOutput, motorSpeed);
    }

    public void manualRunIntake(double percentOutput) {
        m_topIntakeMotor.set(ControlMode.PercentOutput, percentOutput);
    }

    public double getCurrent() {
        return m_topIntakeMotor.getStatorCurrent();
    }

    public boolean isStalled(double stallCurrent) {
        return (m_topIntakeMotor.getStatorCurrent() >= stallCurrent) ||
            (m_bottomIntakeMotor.getStatorCurrent() >= stallCurrent);
    }

    public void intakeCube() {
        m_topIntakeMotor.set(ControlMode.PercentOutput, Constants.INTAKE_ROLLER.TOP_MOTOR_INTAKE_PERCENT_OUTPUT);
        m_bottomIntakeMotor.set(ControlMode.PercentOutput, Constants.INTAKE_ROLLER.BOTTOM_MOTOR_INTAKE_PERCENT_OUTPUT);
    }

    public void rollCube() {
        m_topIntakeMotor.set(ControlMode.PercentOutput, Constants.INTAKE_ROLLER.TOP_MOTOR_ROLL_PERCENT_OUTPUT);
        m_bottomIntakeMotor.set(ControlMode.PercentOutput, Constants.INTAKE_ROLLER.BOTTOM_MOTOR_ROLL_PERCENT_OUTPUT);
    
    }

    public void indexCube() {
        m_topIntakeMotor.set(ControlMode.PercentOutput, Constants.INTAKE_ROLLER.TOP_MOTOR_INDEX_PERCENT_OUTPUT);
        m_bottomIntakeMotor.set(ControlMode.PercentOutput, Constants.INTAKE_ROLLER.BOTTOM_MOTOR_INDEX_PERCENT_OUTPUT);
    
    }

    public void ejectCube() {
        m_topIntakeMotor.set(ControlMode.PercentOutput, Constants.INTAKE_ROLLER.TOP_MOTOR_EJECT_PERCENT_OUTPUT);
        m_bottomIntakeMotor.set(ControlMode.PercentOutput, Constants.INTAKE_ROLLER.BOTTOM_MOTOR_EJECT_PERCENT_OUTPUT);
    }


    public void stopIntake() {
        m_topIntakeMotor.set(ControlMode.PercentOutput, 0.0);
        m_bottomIntakeMotor.set(ControlMode.PercentOutput, 0.0);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Top roller amp draw", m_topIntakeMotor.getStatorCurrent());
        SmartDashboard.putNumber("Bottom roller amp draw", m_bottomIntakeMotor.getStatorCurrent());
    }
}
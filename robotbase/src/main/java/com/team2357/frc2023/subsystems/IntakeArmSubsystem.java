package com.team2357.frc2023.subsystems;

import org.ejml.data.MatrixType;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team2357.frc2023.shuffleboard.ShuffleboardPIDTuner;
import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import com.team2357.lib.util.Utility;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeArmSubsystem extends ClosedLoopSubsystem {
    private static IntakeArmSubsystem instance = null;

    public static IntakeArmSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {
        // Pneumatics
        public int m_deployMilliseconds = 0;
        public int m_stowMilliseconds = 0;

        // Winch
        public double m_winchAxisMaxSpeed;

        public IdleMode m_winchMotorIdleMode;

        public int m_winchMotorStallLimitAmps;
        public int m_winchMotorFreeLimitAmps;

        public boolean m_isInverted;

        public double m_shuffleboardTunerPRange;
        public double m_shuffleboardTunerIRange;
        public double m_shuffleboardTunerDRange;

        // smart motion config
        public double m_winchMotorP;
        public double m_winchMotorI;
        public double m_winchMotorD;

        public double m_winchMotorIZone;
        public double m_winchMotorFF;
        public double m_winchMotorMaxOutput;
        public double m_winchMotorMinOutput;
        public double m_winchMotorMaxRPM;
        public double m_winchMotorMaxVel;
        public double m_winchMotorMinVel;
        public double m_winchMotorMaxAcc;
        public double m_winchMotorAllowedError;
        public double m_maxSpeedPercent;
        public int m_smartMotionSlot;

        public double m_winchDeployRotations;
        public double m_winchStowRotations;
    }

    public Configuration m_config;

    private enum ArmState {
        Unknown, Deployed, Stowed
    };

    private CANSparkMax m_winchMotor;
    private SparkMaxPIDController m_winchPIDController;
    private double m_targetRotations;

    private DoubleSolenoid m_intakeSolenoid;

    private ArmState m_currentState;
    private ArmState m_desiredState;
    private long m_lastActionMillis;

    private ShuffleboardPIDTuner m_shuffleboardPIDTuner;

    public IntakeArmSubsystem(int forwardChannel, int reverseChannel, int winchMotorId) {
        m_intakeSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, forwardChannel, reverseChannel);
        m_winchMotor = new CANSparkMax(winchMotorId, MotorType.kBrushless);

        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;

        m_shuffleboardPIDTuner = new ShuffleboardPIDTuner("Intake Arm", config.m_shuffleboardTunerPRange,
                config.m_shuffleboardTunerIRange, config.m_shuffleboardTunerDRange, config.m_winchMotorP,
                config.m_winchMotorI, config.m_winchMotorD);

        configureWinchMotor(m_winchMotor);

        m_winchPIDController = m_winchMotor.getPIDController();
        configureWinchPID(m_winchPIDController);

        m_winchMotor.setInverted(m_config.m_isInverted);
    }

    private void configureWinchMotor(CANSparkMax motor) {
        motor.setIdleMode(m_config.m_winchMotorIdleMode);
        motor.setSmartCurrentLimit(m_config.m_winchMotorStallLimitAmps, m_config.m_winchMotorFreeLimitAmps);
    }

    private void configureWinchPID(SparkMaxPIDController pidController) {
        // PID
        pidController.setP(m_config.m_winchMotorP);
        pidController.setI(m_config.m_winchMotorI);
        pidController.setD(m_config.m_winchMotorD);
        pidController.setIZone(m_config.m_winchMotorIZone);
        pidController.setFF(m_config.m_winchMotorFF);
        pidController.setOutputRange(m_config.m_winchMotorMinOutput, m_config.m_winchMotorMaxOutput);

        // smart motion
        pidController.setSmartMotionMaxVelocity(m_config.m_winchMotorMaxVel, m_config.m_smartMotionSlot);
        pidController.setSmartMotionMinOutputVelocity(m_config.m_winchMotorMinVel, m_config.m_smartMotionSlot);
        pidController.setSmartMotionMaxAccel(m_config.m_winchMotorMaxAcc, m_config.m_smartMotionSlot);
        pidController.setSmartMotionAllowedClosedLoopError(m_config.m_winchMotorAllowedError,
                m_config.m_smartMotionSlot);
    }

    public void setWinchRotation(double rotations) {
        setClosedLoopEnabled(true);
        m_targetRotations = rotations;
        m_winchPIDController.setReference(m_targetRotations, CANSparkMax.ControlType.kSmartMotion);
    }

    public void setWinchAxisSpeed(double axisSpeed) {
        setClosedLoopEnabled(false);
        double motorSpeed = (-axisSpeed) * m_config.m_winchAxisMaxSpeed;
        m_winchMotor.set(motorSpeed);
    }

    public void stopWinchMotors() {
        setClosedLoopEnabled(false);
        m_winchMotor.set(0);
    }

    public void resetEncoders() {
        m_winchMotor.getEncoder().setPosition(0);
    }

    public double getWinchRotations() {
        return m_winchMotor.getEncoder().getPosition();
    }

    public void updatePID() {
        m_winchPIDController.setP(m_shuffleboardPIDTuner.getPValue());
        m_winchPIDController.setI(m_shuffleboardPIDTuner.getIValue());
        m_winchPIDController.setD(m_shuffleboardPIDTuner.getDValue());
    }

    public boolean isWinchAtRotations() {
        return Utility.isWithinTolerance(m_winchMotor.getEncoder().getPosition(), m_targetRotations,
                m_config.m_winchMotorAllowedError);
    }

    public boolean isDeploying() {
        return (m_desiredState == ArmState.Deployed && m_currentState != ArmState.Deployed);
    }

    public boolean isDeployed() {
        return (m_desiredState == ArmState.Deployed && m_currentState == ArmState.Deployed);
    }

    public boolean isStowing() {
        return (m_desiredState == ArmState.Stowed && m_currentState != ArmState.Stowed);
    }

    public boolean isStowed() {
        return (m_desiredState == ArmState.Stowed && m_currentState == ArmState.Stowed);
    }

    public void deploy() {
        m_currentState = ArmState.Unknown;
        m_desiredState = ArmState.Deployed;
        m_lastActionMillis = 0;
    }

    public void stow() {
        m_currentState = ArmState.Unknown;
        m_desiredState = ArmState.Stowed;
        m_lastActionMillis = 0;
    }

    @Override
    public void periodic() {
        if (m_currentState != m_desiredState) {
            if (m_desiredState == ArmState.Deployed) {
                deployPeriodic();
            } else if (m_desiredState == ArmState.Stowed) {
                stowPeriodic();
            }
        }

        if (isClosedLoopEnabled() && isWinchAtRotations()) {
            setClosedLoopEnabled(false);
        }
        if (m_shuffleboardPIDTuner.arePIDsUpdated()) {
            updatePID();
        }
    }

    private void deployPeriodic() {
        long now = System.currentTimeMillis();

        if (m_lastActionMillis == 0) {
            m_intakeSolenoid.set(DoubleSolenoid.Value.kForward);
            setWinchRotation(m_config.m_winchDeployRotations);
            m_lastActionMillis = now;
        } else if (now > m_lastActionMillis + m_config.m_deployMilliseconds) {
            m_intakeSolenoid.set(DoubleSolenoid.Value.kOff);
            m_currentState = ArmState.Deployed;
            stopWinchMotors();
            m_lastActionMillis = 0;
        }
    }

    private void stowPeriodic() {
        long now = System.currentTimeMillis();

        if (m_lastActionMillis == 0) {
            m_intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
            setWinchRotation(m_config.m_winchStowRotations);
            m_lastActionMillis = now;
        } else if (now > m_lastActionMillis + m_config.m_stowMilliseconds) {
            m_intakeSolenoid.set(DoubleSolenoid.Value.kOff);
            m_currentState = ArmState.Stowed;
            stopWinchMotors();
            m_lastActionMillis = 0;
        }
    }
}

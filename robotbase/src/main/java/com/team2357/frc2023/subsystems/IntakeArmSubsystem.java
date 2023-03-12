package com.team2357.frc2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team2357.frc2023.shuffleboard.ShuffleboardPIDTuner;
import com.team2357.lib.subsystems.ClosedLoopSubsystem;
import com.team2357.lib.util.Utility;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
        // Deploy PID
        public double m_winchDeployP;
        public double m_winchDeployI;
        public double m_winchDeployD;
        public double m_winchDeployIZone;
        public double m_winchDeployFF;
        public int m_winchDeployPidSlot;

        // Stow PID
        public double m_winchStowP;
        public double m_winchStowI;
        public double m_winchStowD;
        public double m_winchStowIZone;
        public double m_winchStowFF;
        public int m_winchStowPidSlot;

        // Smart motion
        public double m_pidMaxOutput;
        public double m_pidMinOutput;
        public double m_smartMotionMaxVelRPM;
        public double m_smartMotionMinVelRPM;
        public double m_smartMotionMaxAccRPM;
        public double m_smartMotionRotationAllowedError;

        public double m_winchMotorAllowedError;
        
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

    private ShuffleboardPIDTuner m_shuffleboardPIDTuner;

    public IntakeArmSubsystem(int forwardChannel, int reverseChannel, int winchMotorId) {
        m_intakeSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, forwardChannel, reverseChannel);
        m_winchMotor = new CANSparkMax(winchMotorId, MotorType.kBrushless);

        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;

        m_shuffleboardPIDTuner = new ShuffleboardPIDTuner("Intake Arm", config.m_shuffleboardTunerPRange,
                config.m_shuffleboardTunerIRange, config.m_shuffleboardTunerDRange, config.m_winchDeployP,
                config.m_winchDeployI, config.m_winchDeployD);

        configureWinchMotor(m_winchMotor);

        m_winchPIDController = m_winchMotor.getPIDController();
        configureWinchPID(m_winchPIDController);

        m_winchMotor.setInverted(m_config.m_isInverted);
        resetEncoders();
    }

    private void configureWinchMotor(CANSparkMax motor) {
        motor.setIdleMode(m_config.m_winchMotorIdleMode);
        motor.setSmartCurrentLimit(m_config.m_winchMotorStallLimitAmps, m_config.m_winchMotorFreeLimitAmps);
        motor.enableVoltageCompensation(12);
    }

    private void configureWinchPID(SparkMaxPIDController pidController) {
       // set PID coefficients for extension
       pidController.setP(m_config.m_winchDeployP, m_config.m_winchDeployPidSlot);
       pidController.setI(m_config.m_winchDeployI, m_config.m_winchDeployPidSlot);
       pidController.setD(m_config.m_winchDeployD, m_config.m_winchDeployPidSlot);
       pidController.setIZone(m_config.m_winchDeployIZone, m_config.m_winchDeployPidSlot);
       pidController.setFF(m_config.m_winchDeployFF, m_config.m_winchDeployPidSlot);

       // Set PID coefficeints for retraction
       pidController.setP(m_config.m_winchStowP, m_config.m_winchStowPidSlot);
       pidController.setI(m_config.m_winchStowI, m_config.m_winchStowPidSlot);
       pidController.setD(m_config.m_winchStowD, m_config.m_winchStowPidSlot);
       pidController.setIZone(m_config.m_winchStowIZone, m_config.m_winchStowPidSlot);
       pidController.setFF(m_config.m_winchStowFF, m_config.m_winchStowPidSlot);

        // smart motion
        configureSmartMotion(pidController, m_config.m_winchDeployPidSlot);
        configureSmartMotion(pidController, m_config.m_winchStowPidSlot);
    }

    public void configureSmartMotion(SparkMaxPIDController pidController, int pidSlot) {
        pidController.setOutputRange(m_config.m_pidMinOutput, m_config.m_pidMaxOutput, pidSlot);
        pidController.setSmartMotionMaxVelocity(m_config.m_smartMotionMaxVelRPM, pidSlot);
        pidController.setSmartMotionMinOutputVelocity(m_config.m_smartMotionMinVelRPM, pidSlot);
        pidController.setSmartMotionMaxAccel(m_config.m_smartMotionMaxAccRPM, pidSlot);
        pidController.setSmartMotionAllowedClosedLoopError(m_config.m_smartMotionRotationAllowedError,
                pidSlot);
    }

    public void setWinchRotations(double rotations) {
        int pidSlot = getWinchRotations() > rotations ? m_config.m_winchStowPidSlot : m_config.m_deployMilliseconds;
        setWinchRotations(rotations, pidSlot);
    }

    public void setWinchRotations(double rotations, int pidSlot) {
        setClosedLoopEnabled(true);
        m_targetRotations = rotations;
        m_winchPIDController.setReference(m_targetRotations, CANSparkMax.ControlType.kSmartMotion, pidSlot);
    }

    public void setWinchAxisSpeed(double axisSpeed) {
        setClosedLoopEnabled(false);
        double motorSpeed = (-axisSpeed) * m_config.m_winchAxisMaxSpeed;
        m_winchMotor.set(motorSpeed);
    }

    public void stopWinchMotor() {
        setClosedLoopEnabled(false);
        m_winchMotor.set(0);
    }

    public void resetEncoders() {
        m_winchMotor.getEncoder().setPosition(0);
        m_targetRotations = 0;
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
        setWinchRotations(m_config.m_winchDeployRotations, m_config.m_winchDeployPidSlot);
    }

    public void stow() {
        m_currentState = ArmState.Unknown;
        m_desiredState = ArmState.Stowed;
        setWinchRotations(m_config.m_winchStowRotations, m_config.m_winchStowPidSlot);
    }

    public void extendSolenoid() {
        m_intakeSolenoid.set(Value.kForward);
    }

    public void stopSolenoid() {
        m_intakeSolenoid.set(Value.kOff);
    }

    public double getAmps() {
        return m_winchMotor.getOutputCurrent();
    } 

    public void manualRotate(Double speed){
        m_winchMotor.set(speed);
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

    //    SmartDashboard.putNumber("Winch rotations", m_winchMotor.getEncoder().getPosition());
    }

    private void deployPeriodic() {
        if (!isWinchAtRotations()) {
        } else if (isWinchAtRotations()) {
            m_currentState = ArmState.Deployed;
        }
    }

    private void stowPeriodic() {
        if (!isWinchAtRotations()) {
        } else if (isWinchAtRotations()) {
            m_currentState = ArmState.Stowed;
        }
    }
}

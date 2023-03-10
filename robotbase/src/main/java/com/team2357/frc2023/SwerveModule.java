package com.team2357.frc2023;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.sensors.CANCoder;
import com.team2357.frc2023.util.swerve.CTREModuleState;
import com.team2357.frc2023.util.swerve.Conversions;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public class SwerveModule {
    public int m_modNumber;

    private TalonFX m_steerMotor;
    private TalonFX m_driveMotor;
    private CANCoder m_canCoder;

    private Rotation2d m_angleOffset;
    private Rotation2d m_lastAngle;

    SimpleMotorFeedforward m_feedforward = new SimpleMotorFeedforward(0, 0);

    public SwerveModule(int modNumber, SwerveModuleConstants moduleConstants) {
        m_modNumber = modNumber;
        m_angleOffset = moduleConstants.angleOffset;

        m_canCoder = new CANCoder(moduleConstants.cancoderID);
        configCANCoder();

        m_steerMotor = new TalonFX(moduleConstants.steerMotorID);
        configSteerMotor();

        m_driveMotor = new TalonFX(moduleConstants.driveMotorID);
        configDriveMotor();

        m_lastAngle = getState().angle;
    }

    public void setDesiredState(SwerveModuleState desiredState, boolean isOpenLoop) {
        desiredState = CTREModuleState.optimize(desiredState, getState().angle);
        setAngle(desiredState);
        setSpeed(desiredState, isOpenLoop);
    }

    private void setSpeed(SwerveModuleState desiredState, boolean isOpenLoop) {
        if (isOpenLoop) {
            double percentOutput = desiredState.speedMetersPerSecond / Constants.DRIVE.MAX_SPEED;
            m_driveMotor.set(ControlMode.PercentOutput, percentOutput);
        } else {
            double velocity = Conversions.MPSToFalcon(desiredState.speedMetersPerSecond, Constants.DRIVE.WHEEL_CIRCUMFERENCE, Constants.DRIVE.GEAR_RATIO);
            m_driveMotor.set(ControlMode.Velocity, velocity, DemandType.ArbitraryFeedForward, m_feedforward.calculate(desiredState.speedMetersPerSecond));
        }
    }

    private void setAngle(SwerveModuleState desiredState) {
        Rotation2d angle = (Math.abs(desiredState.speedMetersPerSecond) <= (Constants.DRIVE.MAX_SPEED * .01)) ? m_lastAngle : desiredState.angle;

        m_steerMotor.set(ControlMode.Position, Conversions.degreesToFalcon(angle.getDegrees(), Constants.DRIVE.ANGLE_GEAR_RATIO));
        m_lastAngle = angle;
    }

    private Rotation2d getAngle() {
        return Rotation2d.fromDegrees(Conversions.falconToDegrees(m_steerMotor.getSelectedSensorPosition() - m_angleOffset.getDegrees(), Constants.DRIVE.ANGLE_GEAR_RATIO));
    }

    public Rotation2d getAbsoluteAngle() {
        return Rotation2d.fromDegrees(m_canCoder.getAbsolutePosition());
    }

    public void resetToAbsolute() {
        double absolute = Conversions.degreesToFalcon(getAbsoluteAngle().getDegrees() - m_angleOffset.getDegrees(), Constants.DRIVE.ANGLE_GEAR_RATIO);
        m_steerMotor.setSelectedSensorPosition(absolute);
    }

    private void configCANCoder() {
        m_canCoder.configFactoryDefault();
    }

    private void configSteerMotor() {
        m_steerMotor.configFactoryDefault();
    }

    private void configDriveMotor() {
        m_driveMotor.configFactoryDefault();
    }

    public SwerveModuleState getState() {
        return new SwerveModuleState(
            Conversions.falconToMPS(m_driveMotor.getSelectedSensorVelocity(), Constants.DRIVE.WHEEL_CIRCUMFERENCE, Constants.DRIVE.GEAR_RATIO),
            getAngle()
        );
    }

    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(
            Conversions.falconToMeters(m_driveMotor.getSelectedSensorPosition(), Constants.DRIVE.WHEEL_CIRCUMFERENCE, Constants.DRIVE.GEAR_RATIO),
            getAngle()
        );
    }
}

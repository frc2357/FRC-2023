package com.team2357.frc2023.subsystems;

import com.ctre.phoenix.sensors.Pigeon2;
import com.team2357.frc2023.Constants;
import com.team2357.frc2023.SwerveModule;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Team364SwerveDriveSubsystem extends SubsystemBase {
    private SwerveDriveOdometry m_odometry;
    private SwerveModule[] m_swerveModules;
    private Pigeon2 m_gyro;

    public Team364SwerveDriveSubsystem() {
        m_gyro = new Pigeon2(Constants.CAN_ID.PIGEON_ID);
        m_gyro.configFactoryDefault();
        zeroGyro();

        m_swerveModules = new SwerveModule[] {
            new SwerveModule(0, Constants.DRIVE.Mod0.constants),
            new SwerveModule(0, Constants.DRIVE.Mod1.constants),
            new SwerveModule(0, Constants.DRIVE.Mod2.constants),
            new SwerveModule(0, Constants.DRIVE.Mod3.constants)
        };

        Timer.delay(1.0);
        resetModulesToAbsolute();

        m_odometry = new SwerveDriveOdometry(Constants.DRIVE.KINEMATICS, getYaw(), getModulePositions());
    }

    public void drive(Translation2d translation, double rotation, boolean fieldRelative, boolean isOpenLoop) {
        SwerveModuleState[] swerveModuleStates =
            Constants.DRIVE.KINEMATICS.toSwerveModuleStates(
                fieldRelative ? ChassisSpeeds.fromFieldRelativeSpeeds(
                                    translation.getX(), 
                                    translation.getY(), 
                                    rotation, 
                                    getYaw()
                                )
                                : new ChassisSpeeds(
                                    translation.getX(), 
                                    translation.getY(), 
                                    rotation)
                                );

        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, Constants.DRIVE.MAX_SPEED);

        for (SwerveModule module : m_swerveModules) {
            module.setDesiredState(swerveModuleStates[module.m_modNumber], isOpenLoop);
        }
    }

    public void setModuleStates(SwerveModuleState[] desiredStates) {
        SwerveDriveKinematics.desaturateWheelSpeeds(desiredStates, Constants.DRIVE.MAX_SPEED);

        for (SwerveModule module : m_swerveModules) {
            module.setDesiredState(desiredStates[module.m_modNumber], false);
        }
    }

    public Pose2d getPose() {
        return m_odometry.getPoseMeters();
    }

    public void resetOdometry(Pose2d pose) {
        m_odometry.resetPosition(getYaw(), getModulePositions(), pose);
    }

    public SwerveModuleState[] getModuleStates() {
        SwerveModuleState[] states = new SwerveModuleState[4];
        for (SwerveModule mod : m_swerveModules) {
            states[mod.m_modNumber] = mod.getState();
        }
        return states;
    }

    public SwerveModulePosition[] getModulePositions() {
        SwerveModulePosition[] states = new SwerveModulePosition[4];
        for (SwerveModule mod : m_swerveModules) {
            states[mod.m_modNumber] = mod.getPosition();
        }
        return states;
    }

    public void zeroGyro() {
        m_gyro.setYaw(0);
    }

    public Rotation2d getYaw() {
        return Rotation2d.fromDegrees(m_gyro.getYaw());
    }

    public void resetModulesToAbsolute() {
        for (SwerveModule module : m_swerveModules) {
            module.resetToAbsolute();
        }
    }

    @Override
    public void periodic() {
        m_odometry.update(getYaw(), getModulePositions());
    }
}

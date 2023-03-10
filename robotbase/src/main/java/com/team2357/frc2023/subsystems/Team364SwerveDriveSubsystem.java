package com.team2357.frc2023.subsystems;

import com.ctre.phoenix.sensors.Pigeon2;
import com.team2357.frc2023.Constants;
import com.team2357.frc2023.SwerveModule;
import com.ctre.phoenix.motorcontrol.NeutralMode;
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
    private static Team364SwerveDriveSubsystem m_instance = null;

    public Team364SwerveDriveSubsystem getInstance() {
        return m_instance;
    }

    public class Configuration {
        public SwerveDriveKinematics m_kinematics;

        public double m_maxVelocityMetersPerSecond;
        public double m_maxAngularVelocityRadiansPerSecond;
        public double m_maxAngularAccelerationRadiansPerSecondSquared;

        public double m_angleGearRatio;
        public double m_driveGearRatio;
        public double m_wheelCircumference;

        public boolean m_steerMotorInverted;
        public boolean m_driveMotorInverted;

        public NeutralMode m_driveMotorNeutralMode;
        public NeutralMode m_steerMotorNeutralMode;
    }

    private Configuration m_config;

    private SwerveDriveOdometry m_odometry;

    private SwerveModule m_frontLeftModule;
    private SwerveModule m_frontRightModule;
    private SwerveModule m_backLeftModule;
    private SwerveModule m_backRightModule;
    private SwerveModule[] m_swerveModules;

    private Pigeon2 m_pigeon;

    public Team364SwerveDriveSubsystem(int pigeonId, SwerveModuleConstants frontLeftConstants, SwerveModuleConstants frontRightConstants, SwerveModuleConstants backLeftConstants, SwerveModuleConstants backRightConstants) {
        m_pigeon = new Pigeon2(pigeonId);
        m_pigeon.configFactoryDefault();
        zeroGyro();

        m_frontLeftModule = new SwerveModule(frontLeftConstants);
        m_frontRightModule = new SwerveModule(frontRightConstants);
        m_backLeftModule = new SwerveModule(backLeftConstants);
        m_backRIghtModule = new SwerveModule(backRightConstants);

        m_swerveModules = new SwerveModule[] {
            m_frontLeftModule,
            m_frontRightModule,
            m_backLeftModule,
            m_backRightModule
        };

        Timer.delay(1.0);
        resetModulesToAbsolute();

        m_instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;

        for (SwerveModule module : m_swerveModules) {
            module.configure(config);
        }

        m_odometry = new SwerveDriveOdometry(m_config.m_kinematics, getYaw(), getModulePositions());
    }

    public void drive(double x, double y, double rotation) {
        ChassisSpeeds chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
            x * m_config.m_maxVelocityMetersPerSecond,
            y * m_config.m_maxVelocityMetersPerSecond,
            rotation m_config.m_maxAngularVelocityRadiansPerSecond,
            getYaw()
        )
        drive(chassisSpeeds);
    }

    public void drive(chassisSpeeds) {
        SwerveModuleState[] states = m_config.m_kinematics.toSwerveModuleStates(chassisSpeeds);

        SwerveDriveKinematics.desaturateWheelSpeeds(states, m_config.m_maxVelocityMetersPerSecond);

        setModuleStates(states);
    }

    public void setModuleStates(SwerveModuleState[] desiredStates) {
        SwerveDriveKinematics.desaturateWheelSpeeds(desiredStates, m_config.m_maxVelocityMetersPerSecond);

        for (SwerveModule module : m_swerveModules) {
            module.setDesiredState(desiredStates[module.m_modNumber]);
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
        m_pigeon.setYaw(0);
    }

    public Rotation2d getYaw() {
        return Rotation2d.fromDegrees(m_pigeon.getYaw());
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

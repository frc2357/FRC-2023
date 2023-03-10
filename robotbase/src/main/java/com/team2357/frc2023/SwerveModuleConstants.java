package com.team2357.frc2023;

import edu.wpi.first.math.geometry.Rotation2d;

public class SwerveModuleConstants {
    public final int driveMotorID;
    public final int steerMotorID;
    public final int cancoderID;
    public final Rotation2d angleOffset;
    public final int moduleNumber;

    public SwerveModuleConstants(int driveMotorID, int steerMotorID, int cancoderID, Rotation2d angleOffset, int moduleNumber) {
        this.driveMotorID = driveMotorID;
        this.steerMotorID = steerMotorID;
        this.cancoderID = cancoderID;
        this.angleOffset = angleOffset;
        this.moduleNumber = moduleNumber;
    }
}

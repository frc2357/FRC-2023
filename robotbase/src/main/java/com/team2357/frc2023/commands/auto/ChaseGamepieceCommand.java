package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem.LIMELIGHT;
import com.team2357.lib.subsystems.LimelightSubsystem;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ChaseGamepieceCommand extends CommandBase {

    SwerveDriveSubsystem m_swerve;
    ProfiledPIDController m_controller;

    LimelightSubsystem m_ll;

    private boolean m_isRunning;

    public ChaseGamepieceCommand() {
        m_swerve = SwerveDriveSubsystem.getInstance();

        m_controller = m_swerve.getChaseController();

        m_ll = DualLimelightManagerSubsystem.getInstance().getLimelight(LIMELIGHT.RIGHT);

        m_isRunning = false;

        addRequirements(m_swerve);
    }

    @Override
    public void initialize() {

        double xVelMeters = m_swerve.getFieldVelocity().dx;
        double yVelMeters = m_swerve.getFieldVelocity().dy;

        double velMeters = Math.sqrt(Math.pow(xVelMeters, 2) + yVelMeters * xVelMeters);

        m_controller.reset(
            new TrapezoidProfile.State(
                    m_ll.getMetersFromTarget(), velMeters));

        m_isRunning = true;
    }

    @Override
    public void execute() {

        if(m_ll.validTargetExists()) {
            m_swerve.drive(new ChassisSpeeds());
            return;
        }
 
        double outputVelMeters = m_controller.calculate(m_ll.getMetersFromTarget(), 0.0);

        ChassisSpeeds speeds = new ChassisSpeeds(outputVelMeters, 0, 0);
        m_swerve.drive(speeds);
    }

    @Override
    public boolean isFinished() {
        return atGoal();
    }

    @Override
    public void end(boolean interrupted) {
        m_isRunning = false;
        m_swerve.drive(new ChassisSpeeds());
    }

    public boolean atGoal() {
        return m_controller.atGoal();
    }

    public boolean isRunning() {
        return m_isRunning;
    }
}

// What I need: Some rotation controller and some drive controller
// ProfiledPID controllers to do that
// Use pose, translate, geometry classes and functions to smooth movement?
// profiled pid output -> chassis speeds equals rocky scary movement
// Rocky movement caused by x and y changing against eachother when moving
// Look at Frog, broncobots
package com.team2357.frc2023.commands.drive;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class DetectChargeBreakCommand extends CommandBase {

    private enum BalanceState {
        BREAK,
        HIGH,
        FLAT
    }

    private SwerveDriveSubsystem m_swerve = SwerveDriveSubsystem.getInstance();
    BalanceState m_balanceState;
    double m_prevPitch;
    double m_time;

    @Override
    public void initialize() {
        m_balanceState = BalanceState.FLAT;
        m_prevPitch = m_swerve.getPitch();
    }

    @Override
    public void execute() {
        double pitch = m_swerve.getPitch() % 360;
        pitch = pitch > 180 ? pitch - 360 : pitch;

        if (m_balanceState == BalanceState.FLAT) {
            if (Math.abs(pitch) > m_prevPitch) {

                boolean waiting = m_time != 0 && m_time + 0.1 > Timer.getFPGATimestamp();
                if (!waiting) {
                    m_balanceState = BalanceState.HIGH;
                }
            } else {
                m_time = Timer.getFPGATimestamp();
            }
        }

        if (m_balanceState == BalanceState.HIGH) {

            if (Math.abs(pitch) < m_prevPitch) {

                boolean waiting = m_time != 0 && m_time + 0.1 > Timer.getFPGATimestamp();
                if (!waiting) {
                    m_balanceState = BalanceState.BREAK;
                }
            } else {
                m_time = Timer.getFPGATimestamp();

            }
        }

        m_prevPitch = pitch;
    }

    @Override
    public boolean isFinished() {
        return m_balanceState == BalanceState.BREAK;
    }

    @Override
    public void end(boolean interrupted) {
        m_swerve.endTrajectory();
    }
}

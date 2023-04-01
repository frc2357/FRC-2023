package com.team2357.frc2023.commands.util;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * Logs only if the end of the timer is reached
 */
public class LogTimerCommand extends CommandBase {
    private long m_millis;
    private String m_key;
    private String m_message;
    private long m_startTime;

    public LogTimerCommand(double seconds, String key, String message) {
        m_millis = (long)(seconds * 1000);
        m_key = key;
        m_message = message;
    }

    @Override
    public void initialize() {
        m_startTime = System.currentTimeMillis();
    }

    @Override
    public boolean isFinished() {
        long now = System.currentTimeMillis();
        return (m_startTime + m_millis < now);
    }

    @Override
    public void end(boolean interrupted) {
        if (!interrupted) {
            Logger.getInstance().recordMetadata(m_key, m_message);
        }
    }
}

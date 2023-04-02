package com.team2357.frc2023.commands.util;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class LogCommand extends CommandBase {
    private String m_key;
    private String m_message;
    private boolean m_logToConsole;

    public LogCommand(String key, String message) {
    }

    public LogCommand(String key, String message, boolean logToConsole) {
        m_key = key;
        m_message = message;
        m_logToConsole = logToConsole;
    }

    @Override
    public void initialize() {
        //record output records things that we pass into it, like a number or really anything.
        //expect for inputs, thats a different method, called proccesInputs
        Logger.getInstance().recordOutput("Log Command Logged/"+m_key, m_message);
        if (m_logToConsole) {
            System.out.println("LOG [" + m_key + "]:" + m_message);
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}

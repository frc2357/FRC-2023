package com.team2357.frc2023.commands.util;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AlertCommand extends CommandBase {
    
    String m_alert;
    public AlertCommand(String alert) {
        m_alert = alert;
    }

    @Override
    public void initialize() {
        System.out.println(m_alert);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}

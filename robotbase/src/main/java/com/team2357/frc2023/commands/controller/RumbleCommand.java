package com.team2357.frc2023.commands.controller;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.controls.RumbleInterface;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RumbleCommand extends CommandBase {
    private RumbleInterface m_rumbleDevice;
    private double m_timeoutSeconds;
    private Timer m_timer;
    
    /**
     * rumble rumble rumble rumble rumble rrrumble rruuumble rumbl rumble
     * @param rumbleDevice The device to rumble
     * @param timeoutSeconds How long to rumble
     * @return
     */
    public RumbleCommand(RumbleInterface rumbleDevice, double timeoutSeconds) {
        m_rumbleDevice = rumbleDevice;
        m_timeoutSeconds = timeoutSeconds;
    }

    @Override
    public void initialize() {
        m_rumbleDevice.setRumble(RumbleType.kBothRumble, Constants.CONTROLLER.RUMBLE_INTENSITY);

        m_timer = new Timer();
        m_timer.start();
    }

    @Override 
    public boolean isFinished() {  
        return m_timer.hasElapsed(m_timeoutSeconds);
    }

    @Override
    public void end(boolean interrupted) {
        m_rumbleDevice.setRumble(RumbleType.kBothRumble, 0.0);
    }
}

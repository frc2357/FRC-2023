package com.team2357.frc2023.commands.util;

import com.team2357.frc2023.arduino.GamepieceLED;
import com.team2357.frc2023.arduino.GamepieceLED.SIGNAL_COLOR;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class SignalGamepieceCommand extends CommandBase {
    private SIGNAL_COLOR m_color;

    public SignalGamepieceCommand(SIGNAL_COLOR color) {
        m_color = color;
    }

    @Override
    public void execute() {
        GamepieceLED.getInstance().setSignalColor(m_color);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        GamepieceLED.getInstance().setSignalColor(SIGNAL_COLOR.NONE);
    }
    
}

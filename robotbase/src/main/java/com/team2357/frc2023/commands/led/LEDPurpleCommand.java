package com.team2357.frc2023.commands.led;

import com.team2357.frc2023.led.GamepieceLED;
import com.team2357.frc2023.led.GamepieceLED.SIGNAL_COLOR;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class LEDPurpleCommand extends CommandBase{
    @Override
    public void initialize() {
        GamepieceLED.getInstance().setSignalColor(SIGNAL_COLOR.PURPLE);
    }
    @Override
    public boolean isFinished() {
        return true;
    }
}

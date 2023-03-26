package com.team2357.frc2023.commands.led;

import com.team2357.frc2023.led.GamepieceLED;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class LEDBlankCommand extends CommandBase{
    @Override
    public void initialize() {
        GamepieceLED.getInstance().blankLED();
    }
    @Override
    public boolean isFinished() {
        return true;
    }
}

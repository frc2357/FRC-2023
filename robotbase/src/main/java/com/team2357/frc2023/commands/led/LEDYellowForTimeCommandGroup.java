package com.team2357.frc2023.commands.led;

import com.team2357.frc2023.led.GamepieceLED;
import com.team2357.frc2023.led.GamepieceLED.SIGNAL_COLOR;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class LEDYellowForTimeCommandGroup extends SequentialCommandGroup{
    LEDYellowForTimeCommandGroup(Integer seconds) {
        addCommands(new InstantCommand(()  -> {GamepieceLED.getInstance().setSignalColor(SIGNAL_COLOR.PURPLE);}),new WaitCommand(seconds), new LEDBlankCommand());
    }
}


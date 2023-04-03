package com.team2357.frc2023.commands.controller;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.controls.RumbleInterface;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

public class RumbleCommand {
    
    /**
     * rumble rumble rumble rumble rumble rrrumble rruuumble rumbl rumble
     * @param rumbleDevice The device to rumble
     * @param timeoutSeconds How long to rumble
     * @return
     */
    public static Command createRumbleCommand(RumbleInterface rumbleDevice, double timeoutSeconds) {
        return new InstantCommand(() -> {
            rumbleDevice.setRumble(RumbleType.kBothRumble, Constants.CONTROLLER.RUMBLE_INTENSITY);
        }).withTimeout(timeoutSeconds).finallyDo((boolean interrupted) -> {
            rumbleDevice.setRumble(RumbleType.kBothRumble, 0.0);
        });
    }
}

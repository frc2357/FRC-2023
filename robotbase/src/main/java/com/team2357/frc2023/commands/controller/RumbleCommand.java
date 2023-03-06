package com.team2357.frc2023.commands.controller;

import com.team2357.frc2023.Constants;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

public class RumbleCommand {
    
    /**
     * rumble rumble rumble rumble rumble rrrumble rruuumble rumbl rumble
     * @param controller The controller to rumble
     * @param timeoutSeconds How long to rumble
     * @return
     */
    public static Command createRumbleCommand(XboxController controller, double timeoutSeconds) {
        return new InstantCommand(() -> {
            controller.setRumble(RumbleType.kBothRumble, Constants.CONTROLLER.RUMBLE_INTENSITY);
        }).withTimeout(timeoutSeconds).finallyDo((boolean interrupted) -> {
            controller.setRumble(RumbleType.kBothRumble, 0.0);
        });
    }
}

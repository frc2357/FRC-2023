package com.team2357.frc2023.commands.everybot;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.WristSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class WristAmpZeroCommand extends CommandBase {
    
    public WristAmpZeroCommand() {
        addRequirements(WristSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        WristSubsystem.getInstance().manualRotate(Constants.WRIST.WRIST_AMP_ZERO_PERCENT_OUTPUT);
    }

    @Override
    public boolean isFinished() {
        return Math.abs(WristSubsystem.getInstance().getAmps()) >= Constants.WRIST.WRIST_ZERO_MAX_AMPS;
    }

    @Override
    public void end(boolean interrupted) {
        WristSubsystem.getInstance().stopMotor();
    }
    
}

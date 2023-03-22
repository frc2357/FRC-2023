package com.team2357.frc2023.commands.everybot;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.EverybotWristSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class WristAmpZeroCommand extends CommandBase {
    
    public WristAmpZeroCommand() {
        addRequirements(EverybotWristSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        EverybotWristSubsystem.getInstance().manualRotate(Constants.EVERYBOT_WRIST.WRIST_AMP_ZERO_PERCENT_OUTPUT);
    }

    @Override
    public boolean isFinished() {
        return Math.abs(EverybotWristSubsystem.getInstance().getAmps()) >= Constants.EVERYBOT_WRIST.WRIST_ZERO_MAX_AMPS;
    }

    @Override
    public void end(boolean interrupted) {
        EverybotWristSubsystem.getInstance().stopMotor();
    }
    
}

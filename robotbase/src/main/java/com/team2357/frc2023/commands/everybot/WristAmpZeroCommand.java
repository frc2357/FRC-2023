package com.team2357.frc2023.commands.everybot;

import org.littletonrobotics.junction.Logger;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.WristSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class WristAmpZeroCommand extends CommandBase {
    private long m_startTime;
    
    public WristAmpZeroCommand() {
        addRequirements(WristSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        m_startTime = System.currentTimeMillis();
        WristSubsystem.getInstance().manualRotate(Constants.WRIST.WRIST_AMP_ZERO_PERCENT_OUTPUT);
    }

    @Override
    public boolean isFinished() {
        if (m_startTime + Constants.WRIST.WRIST_ZERO_WAIT_MS > System.currentTimeMillis()) {
            return false;
        }
        return WristSubsystem.getInstance().getAmps() >= Constants.WRIST.WRIST_ZERO_MAX_AMPS;
    }

    @Override
    public void end(boolean interrupted) {
        if (!interrupted) {
            Logger.getInstance().recordOutput("Wrist Zero", "success");
            WristSubsystem.getInstance().resetEncoder();
            WristSubsystem.getInstance().setRotations(0);
        } else {
            DriverStation.reportError("Wrist Zero interrupted!", false);
            Logger.getInstance().recordOutput("Wrist Zero", "interrupted");
            WristSubsystem.getInstance().stopMotor();
        }
    }
    
}

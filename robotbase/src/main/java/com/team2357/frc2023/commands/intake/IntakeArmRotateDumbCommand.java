package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.IntakeArmSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeArmRotateDumbCommand extends CommandBase {

    double m_speedPercent;

    public IntakeArmRotateDumbCommand(double speedPercent) {
        m_speedPercent = speedPercent;
    }

    @Override
    public void initialize() {
        IntakeArmSubsystem.getInstance().manualRotate(m_speedPercent);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        IntakeArmSubsystem.getInstance().manualRotate(0.0);
    }
}

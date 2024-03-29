package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.IntakeArmSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeSolenoidExtendCommand extends CommandBase {
    public double m_startMillis;

    public IntakeSolenoidExtendCommand() {
        addRequirements(IntakeArmSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        m_startMillis = System.currentTimeMillis();
        IntakeArmSubsystem.getInstance().extendSolenoid();
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void end(boolean interrupted) {}
}

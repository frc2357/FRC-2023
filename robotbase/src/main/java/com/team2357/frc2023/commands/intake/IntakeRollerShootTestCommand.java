package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeRollerShootTestCommand extends CommandBase{
    private final IntakeRollerSubsystem m_intakeRoller;

    public IntakeRollerShootTestCommand() {
        m_intakeRoller = IntakeRollerSubsystem.getInstance();

        addRequirements(m_intakeRoller);
    }

    @Override
    public void initialize() {
        m_intakeRoller.configureForShooting();

        m_intakeRoller.setIntakeSpeed(-1);
    }

    @Override
    public void end(boolean Interrupted) {
        m_intakeRoller.stopIntake();
        m_intakeRoller.configureForIntake();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}

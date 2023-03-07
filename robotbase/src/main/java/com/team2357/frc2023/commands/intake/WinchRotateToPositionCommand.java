package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.IntakeArmSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class WinchRotateToPositionCommand extends CommandBase {
    private double m_rotations;

    public WinchRotateToPositionCommand(double rotations) {
        m_rotations = rotations;
        addRequirements(IntakeArmSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        IntakeArmSubsystem.getInstance().setWinchRotations(m_rotations);
    }

    @Override
    public boolean isFinished() {
        return IntakeArmSubsystem.getInstance().isWinchAtRotations();
    }

    @Override
    public void end(boolean interrupted) {
        IntakeArmSubsystem.getInstance().stopWinchMotor();
    }

}

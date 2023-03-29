package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.IntakeArmSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeHalfDeployCommand extends CommandBase {
    public IntakeHalfDeployCommand() {
        addRequirements(IntakeArmSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        IntakeArmSubsystem.getInstance().halfDeploy();
    }

    @Override
    public boolean isFinished() {
        return IntakeArmSubsystem.getInstance().isHalfDeployed();
    }
}

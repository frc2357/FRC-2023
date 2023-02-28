package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.IntakeArmSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeArmDeployCommand extends CommandBase {
    public IntakeArmDeployCommand() {
        addRequirements(IntakeArmSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        IntakeArmSubsystem.getInstance().deploy();
    }

    @Override
    public boolean isFinished() {
        return IntakeArmSubsystem.getInstance().isDeployed();
    }
}

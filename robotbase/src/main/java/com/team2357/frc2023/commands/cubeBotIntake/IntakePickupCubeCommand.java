package com.team2357.frc2023.commands.cubeBotIntake;

import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakePickupCubeCommand extends CommandBase {

    private IntakeRollerSubsystem intake;
    
    public IntakePickupCubeCommand() {
        intake = IntakeRollerSubsystem.getInstance();
        addRequirements(intake);
    }

    @Override
    public void execute() {
        intake.intakeCube();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        intake.stopIntake();
    }
    
}

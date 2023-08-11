package com.team2357.frc2023.commands.cubeBotIntake;

import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeIndexCubeCommand extends CommandBase {

    private IntakeRollerSubsystem intake;
    
    public IntakeIndexCubeCommand() {
        intake = IntakeRollerSubsystem.getInstance();
        addRequirements(intake);
    }

    @Override
    public void execute() {
        intake.indexCube();
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

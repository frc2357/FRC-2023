package com.team2357.frc2023.commands.cubeBotIntake;

import com.team2357.frc2023.Robot;
import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeRollCubeCommand extends CommandBase {

    private IntakeRollerSubsystem intake;
    
    public IntakeRollCubeCommand() {
        intake = IntakeRollerSubsystem.getInstance();
        addRequirements(intake);
    }

    @Override
    public void execute() {
        intake.rollCube();
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

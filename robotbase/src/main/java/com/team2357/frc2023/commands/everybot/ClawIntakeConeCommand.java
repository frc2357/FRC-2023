package com.team2357.frc2023.commands.everybot;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.ClawSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ClawIntakeConeCommand extends CommandBase {
    
    public ClawIntakeConeCommand() {
        addRequirements(ClawSubsystem.getInstance());
    }

    @Override
    public void execute() {
        ClawSubsystem.getInstance().intakeCone();
    }

    @Override
    public boolean isFinished() {
        return Math.abs(ClawSubsystem.getInstance().getAmps()) >= Constants.CLAW.CONE_INTAKE_AMP_LIMIT;
    }

    @Override
    public void end(boolean interrupted) {
        ClawSubsystem.getInstance().stopRollers();
    }
    
}

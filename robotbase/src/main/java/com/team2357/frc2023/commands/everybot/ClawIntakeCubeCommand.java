package com.team2357.frc2023.commands.everybot;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.ClawSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ClawIntakeCubeCommand extends CommandBase {
    private long m_startMillis;
    
    public ClawIntakeCubeCommand() {
        addRequirements(ClawSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        m_startMillis = System.currentTimeMillis();
        ClawSubsystem.getInstance().intakeCube();
    }

    @Override
    public boolean isFinished() {
        if (m_startMillis + Constants.CLAW.CUBE_INTAKE_AMP_WAIT > System.currentTimeMillis()) {
            return false;
        }
        return ClawSubsystem.getInstance().getAmps() >= Constants.CLAW.CUBE_INTAKE_AMP_LIMIT;
    }

    @Override
    public void end(boolean interrupted) {
        ClawSubsystem.getInstance().stopRollers();
    }
}
    
package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

import edu.wpi.first.wpilibj2.command.WaitCommand;

public class IntakeAutoStowCommand extends CommandLoggerBase{

    public IntakeDeployCommandGroup m_deployGroup;
    public IntakeStowCommandGroup m_stowGroup;
    public boolean m_spiked = false;
    public long m_initializedAt;

    public IntakeAutoStowCommand(){
        m_deployGroup = new IntakeDeployCommandGroup();
        m_stowGroup = new IntakeStowCommandGroup();
        m_initializedAt = System.currentTimeMillis();
    }

    @Override
    public void execute() {
        if(m_initializedAt+IntakeRollerSubsystem.getInstance().m_config.m_waitTime<System.currentTimeMillis()){
            if(IntakeRollerSubsystem.getInstance().getCurrent()>IntakeRollerSubsystem.getInstance().m_config.m_spikeAmount){
                m_spiked=true;
            }
        }
    }

    @Override
    public void initialize(){
        m_deployGroup.schedule();
    }

    @Override
    public boolean isFinished() {
        return m_spiked;
    }
    
    @Override
    public void end(boolean interrupted){
        m_deployGroup.cancel();
        new WaitCommand(0.25);
        m_stowGroup.schedule();
    }

}

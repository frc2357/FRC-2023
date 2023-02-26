package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

public class IntakeAutoStowCommand extends CommandLoggerBase{

    public IntakeDeployCommandGroup m_deploygroup;
    public IntakeStowCommandGroup m_stowgrroup;
    public boolean m_spiked = false;
    public long m_initializedAt;

    public IntakeAutoStowCommand(){
        m_deploygroup = new IntakeDeployCommandGroup();
        m_stowgrroup = new IntakeStowCommandGroup();
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
        m_deploygroup.schedule();
    }

    @Override
    public boolean isFinished() {
        return m_spiked;
    }
    
    @Override
    public void end(boolean interrupted){
        m_deploygroup.cancel();
        m_stowgrroup.schedule();
    }

}

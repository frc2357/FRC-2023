package com.team2357.frc2023.commands.intake;

import java.util.ArrayList;
import java.util.List;

import com.team2357.frc2023.subsystems.IntakeArmSubsystem;
import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

public class IntakeAutoStowCommand extends CommandLoggerBase{

    public IntakeDeployCommandGroup m_deploygroup;
    public IntakeStowCommandGroup m_stowgrroup;
    public boolean m_spiked = false;
    public long m_initializedat;

    public static class Configuration {
        public double m_spikeamount;
        public double m_waittime;
    }

    public Configuration m_config;

    public void configure(Configuration config){
        m_config = config;
    }

    public IntakeAutoStowCommand(){
        m_deploygroup = new IntakeDeployCommandGroup();
        m_stowgrroup = new IntakeStowCommandGroup();
        m_initializedat = System.currentTimeMillis();
    }

    @Override
    public void execute() {
        if(IntakeArmSubsystem.getInstance().isDeployed()&&m_initializedat+m_config.m_waittime<System.currentTimeMillis()){
            if(IntakeRollerSubsystem.getInstance().getCurrent()>m_config.m_spikeamount){
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

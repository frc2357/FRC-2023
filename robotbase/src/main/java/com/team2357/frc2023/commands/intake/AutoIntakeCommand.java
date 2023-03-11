package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoIntakeCommand extends CommandBase{

    public IntakeDeployCommandGroup m_deployGroup;
    public AutoIntakeCommandGroup m_stowGroup;
    public boolean m_spiked = false;
    public long m_initializedAt;

    public AutoIntakeCommand(){
        m_deployGroup = new IntakeDeployCommandGroup();
        m_stowGroup = new AutoIntakeCommandGroup();
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

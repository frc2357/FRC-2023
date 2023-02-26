package com.team2357.frc2023.commands.intake;

import java.util.ArrayList;
import java.util.List;

import com.team2357.frc2023.subsystems.IntakeArmSubsystem;
import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

public class IntakeAutoStowCommand extends CommandLoggerBase{

    public IntakeDeployCommandGroup m_deploygroup;
    public IntakeStowCommandGroup m_stowgrroup;
    public boolean done = false;

    public IntakeAutoStowCommand(){
        m_deploygroup = new IntakeDeployCommandGroup();
        m_stowgrroup = new IntakeStowCommandGroup();
    }

    public List<Double> currents = new ArrayList<>();
    @Override
    public void execute() {
        if(IntakeArmSubsystem.getInstance().isDeployed()){
            if(currents.size()<50){
                currents.add(IntakeRollerSubsystem.getInstance().getCurrent());
            }else{
                int sum = 0;
                for(int i = 0;i<currents.size();i++){
                    sum+=currents.get(i);
                }
                sum=sum/currents.size();
                if(sum>10){
                    done=true;
                    IntakeArmSubsystem.getInstance().stow();
                }
                currents.clear();
            }
        }
    }

    @Override
    public void initialize(){
        m_deploygroup.schedule();
    }

    @Override
    public boolean isFinished() {
        return done;
    }
    
    @Override
    public void end(boolean interrupted){
        m_deploygroup.cancel();
        m_stowgrroup.schedule();
    }

}

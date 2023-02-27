package com.team2357.frc2023.commands.human.panic;

import com.team2357.frc2023.subsystems.IntakeArmSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeArmToggleCommand extends CommandBase{
    IntakeArmSubsystem m_arm;
    public IntakeArmToggleCommand(){
        m_arm = IntakeArmSubsystem.getInstance();
        addRequirements(m_arm);
    }
    @Override
    public void initialize(){
        if(m_arm.isDeployed() || m_arm.isDeploying()){
            m_arm.stow();
        }else{
            m_arm.deploy();
        }
    }
    @Override
    public boolean isFinished(){
        return m_arm.isStowed() || m_arm.isDeployed();
    }
}

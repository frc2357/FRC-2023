package com.team2357.frc2023.commands.human.panic;

import com.team2357.frc2023.subsystems.IntakeArmSubsystem;
import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeRollerToggle extends CommandBase{
    IntakeArmSubsystem m_arm;
    public IntakeRollerToggle(){
        m_arm = IntakeArmSubsystem.getInstance();
        addRequirements(m_arm);
    }
    @Override
    public void initialize(){
        if(m_arm.isDeployed()){
            m_arm.stow();
        }else{
            m_arm.deploy();
        }
    }
}

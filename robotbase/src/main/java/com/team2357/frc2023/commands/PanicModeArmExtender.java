package com.team2357.frc2023.commands;

import com.team2357.frc2023.subsystems.ArmExtendSubsystem;
import com.team2357.frc2023.subsystems.WristSubsystem;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class PanicModeArmExtender extends CommandBase{
    XboxController controller;
    public PanicModeArmExtender(XboxController m_controller){
        addRequirements(ArmExtendSubsystem.getInstance());
        addRequirements(WristSubsystem.getInstance());
        controller = m_controller;
    }
    @Override 
    public void execute(){
        ArmExtendSubsystem.getInstance().extend(controller.getRightY());
        if(controller.getAButtonPressed()){
            if(WristSubsystem.getInstance().isExtended()|| WristSubsystem.getInstance().isExtending()){
                WristSubsystem.getInstance().contract();
            }
            else{
                WristSubsystem.getInstance().extend();
            }
        }
    }
    @Override
    public void end(boolean interrupted){
        ArmExtendSubsystem.getInstance().stopExtensionMotors();
    }
}

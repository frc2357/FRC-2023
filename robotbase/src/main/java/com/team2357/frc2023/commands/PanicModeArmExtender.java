package com.team2357.frc2023.commands;

import com.team2357.frc2023.subsystems.ArmExtendSubsystem;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class PanicModeArmExtender extends CommandBase{
    XboxController controller;
    public PanicModeArmExtender(XboxController m_controller){
        addRequirements(ArmExtendSubsystem.getInstance());
        controller = m_controller;
    }
    @Override 
    public void execute(){
        ArmExtendSubsystem.getInstance().extend(controller.getRightY());
    }
    @Override
    public void end(boolean interrupted){
        ArmExtendSubsystem.getInstance().stopExtensionMotors();
    }
}

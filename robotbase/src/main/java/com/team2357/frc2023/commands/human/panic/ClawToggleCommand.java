package com.team2357.frc2023.commands.human.panic;

import com.team2357.frc2023.subsystems.ClawSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ClawToggleCommand extends CommandBase{
    public ClawToggleCommand(){
        addRequirements(ClawSubsystem.getInstance());
    }
    @Override
    public void initialize(){
        if(ClawSubsystem.getInstance().isClosed()){
            ClawSubsystem.getInstance().open();
        }
        else{
            ClawSubsystem.getInstance().close();
        }
    }
}

package com.team2357.frc2023.commands;

import com.team2357.frc2023.controls.AxisInterface;
import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

public class IntakeAxisRollerCommand extends CommandLoggerBase {
    private AxisInterface m_axis;

    public IntakeAxisRollerCommand(AxisInterface axi){
        m_axis = axi;
        addRequirements(IntakeRollerSubsystem.getInstance());
    }

    @Override
    public void execute(){
        double axisValue = m_axis.getValue();
        IntakeRollerSubsystem.getInstance().setAxisRollerSpeed(axisValue);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean inter){
        IntakeRollerSubsystem.getInstance().stopIntake();
    }
    
}

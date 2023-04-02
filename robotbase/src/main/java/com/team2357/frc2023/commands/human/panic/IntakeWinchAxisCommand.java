package com.team2357.frc2023.commands.human.panic;

import com.team2357.frc2023.controls.AxisInterface;
import com.team2357.frc2023.subsystems.IntakeArmSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeWinchAxisCommand extends CommandBase{
    private AxisInterface m_axis;

    public IntakeWinchAxisCommand(AxisInterface axis){
        m_axis = axis;
        addRequirements(IntakeArmSubsystem.getInstance());
    }

    @Override
    public void execute(){
        double axisValue = m_axis.getValue();
        if(axisValue >= 0.0) {
            IntakeArmSubsystem.getInstance().stowSolenoid();
        } else {
            IntakeArmSubsystem.getInstance().extendSolenoid();
        }
        IntakeArmSubsystem.getInstance().setWinchAxisSpeed(axisValue);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean inter){
        IntakeArmSubsystem.getInstance().stopWinchMotor();
    }
}

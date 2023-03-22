package com.team2357.frc2023.commands.human.panic;

import com.team2357.frc2023.controls.AxisInterface;
import com.team2357.frc2023.subsystems.EverybotClawSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class EverybotClawRollerAxisCommand extends CommandBase {
    private AxisInterface m_axis;

    public EverybotClawRollerAxisCommand(AxisInterface axis) {
        m_axis = axis;
        addRequirements(EverybotClawSubsystem.getInstance());
    }

    @Override
    public void execute() {
        double axisSpeed = m_axis.getValue();
        EverybotClawSubsystem.getInstance().setAxisRollerSpeed(axisSpeed);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        EverybotClawSubsystem.getInstance().stopRollers();
    }
    
}

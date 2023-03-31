package com.team2357.frc2023.commands.human.panic;

import com.team2357.frc2023.controls.AxisInterface;
import com.team2357.frc2023.subsystems.ClawSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class EverybotClawRollerAxisCommand extends CommandBase {
    private AxisInterface m_axis;

    public EverybotClawRollerAxisCommand(AxisInterface axis) {
        m_axis = axis;
        addRequirements(ClawSubsystem.getInstance());
    }

    @Override
    public void execute() {
        double axisSpeed = m_axis.getValue();
        ClawSubsystem.getInstance().setAxisRollerSpeed(axisSpeed);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        ClawSubsystem.getInstance().stopRollers();
    }
    
}

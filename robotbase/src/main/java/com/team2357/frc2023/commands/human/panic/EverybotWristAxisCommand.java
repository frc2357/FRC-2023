package com.team2357.frc2023.commands.human.panic;

import com.team2357.frc2023.controls.AxisInterface;
import com.team2357.frc2023.subsystems.EverybotWristSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;
import pabeles.concurrency.IntOperatorTask.Max;

public class EverybotWristAxisCommand extends CommandBase {
    private AxisInterface m_axis;

    public EverybotWristAxisCommand(AxisInterface axis) {
        m_axis = axis;
        addRequirements(EverybotWristSubsystem.getInstance());
    }

    @Override
    public void execute() {
        double axisValue = m_axis.getValue();

        EverybotWristSubsystem.getInstance().setWristAxisSpeed(axisValue);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        EverybotWristSubsystem.getInstance().stopMotor();
    }
}

package com.team2357.frc2023.commands.human.panic;

import com.team2357.frc2023.controls.AxisInterface;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmRotationCommand extends CommandBase {
    AxisInterface m_axis;

    public ArmRotationCommand(AxisInterface axis) {
        addRequirements(ArmRotationSubsystem.getInstance());
        m_axis = axis;
    }

    @Override
    public void execute() {
        double axisValue = m_axis.getValue();
        ArmRotationSubsystem.getInstance().manualRotate(axisValue);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        ArmRotationSubsystem.getInstance().stopRotationMotors();
    }
}

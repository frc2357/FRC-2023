package com.team2357.frc2023.commands.human.panic;

import com.team2357.frc2023.controls.AxisInterface;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmRotationAxisCommand extends CommandBase {
    AxisInterface m_axis;

    public ArmRotationAxisCommand(AxisInterface axis) {
        addRequirements(ArmRotationSubsystem.getInstance());
        m_axis = axis;
    }

    @Override
    public void initialize() {
        System.out.println("ArmRotationAxisCommand");
        ArmRotationSubsystem.getInstance().setClosedLoopEnabled(false);
    }

    @Override
    public void execute() {
        double axisValue = m_axis.getValue();
        ArmRotationSubsystem.getInstance().setRotationAxisSpeed(axisValue);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        ArmRotationSubsystem.getInstance().endAxisCommand();
    }
}

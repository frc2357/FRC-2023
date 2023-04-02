package com.team2357.frc2023.commands.human.panic;

import com.team2357.frc2023.controls.AxisInterface;
import com.team2357.frc2023.subsystems.ArmExtensionSubsystem;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmExtensionAxisCommand extends CommandBase {
    AxisInterface m_axis;

    public ArmExtensionAxisCommand(AxisInterface axis) {
        addRequirements(ArmExtensionSubsystem.getInstance());
        m_axis = axis;
    }

    @Override
    public void execute() {
        double axisValue = m_axis.getValue();
        ArmExtensionSubsystem.getInstance().manualExtend(axisValue);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        ArmExtensionSubsystem.getInstance().stopMotor();
    }
}

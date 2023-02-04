package com.team2357.frc2023.commands.human.panic;

import com.team2357.frc2023.controls.AxisInterface;
import com.team2357.frc2023.subsystems.ArmExtendSubsystem;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmExtenderCommand extends CommandBase {
    XboxController controller;
    AxisInterface m_axis;

    public ArmExtenderCommand(AxisInterface axis) {
        addRequirements(ArmExtendSubsystem.getInstance());
        m_axis = axis;
    }

    @Override
    public void execute() {
        double axisValue = m_axis.getValue();
        ArmExtendSubsystem.getInstance().extend(axisValue);
    }

    @Override
    public void end(boolean interrupted) {
        ArmExtendSubsystem.getInstance().stopExtensionMotors();
    }
}

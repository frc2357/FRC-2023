package com.team2357.frc2023.commands.util;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armrotation.ArmRotationAmpZeroCommand;
import com.team2357.frc2023.commands.intake.WinchAmpZeroCommand;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AmpZeroCommandGroup extends ParallelDeadlineGroup{
    static Command whinchZero = new WinchAmpZeroCommand();
    static Command armRotationZero = new ArmRotationAmpZeroCommand();

    public AmpZeroCommandGroup() {
        super(new WaitCommand(Constants.AMP_ZERO.AMP_ZERO_DEADLINE_SECONDS), new WinchAmpZeroCommand(), new ArmRotationAmpZeroCommand());
    }
    
}

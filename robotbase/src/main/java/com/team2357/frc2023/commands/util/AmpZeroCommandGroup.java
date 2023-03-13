package com.team2357.frc2023.commands.util;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armextension.ArmExtendAmpZeroCommand;
import com.team2357.frc2023.commands.intake.WinchAmpZeroCommand;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AmpZeroCommandGroup extends ParallelDeadlineGroup{

    public AmpZeroCommandGroup() {
        super(new WaitCommand(Constants.AMP_ZERO.AMP_ZERO_DEADLINE_SECONDS), new WinchAmpZeroCommand(), new InstantCommand(() -> {ArmRotationSubsystem.getInstance().resetEncoder();}), new ArmExtendAmpZeroCommand());
        // super(new WaitCommand(Constants.AMP_ZERO.AMP_ZERO_DEADLINE_SECONDS), new WinchAmpZeroCommand(), new ArmRotationAmpZeroCommand(), new ArmExtendAmpZeroCommand());
    }
    
}

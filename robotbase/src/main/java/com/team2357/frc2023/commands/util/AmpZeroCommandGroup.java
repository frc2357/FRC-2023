package com.team2357.frc2023.commands.util;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armextension.ArmExtendAmpZeroCommand;
import com.team2357.frc2023.commands.armrotation.ArmRotationZeroCommand;
import com.team2357.frc2023.commands.intake.WinchAmpZeroCommand;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AmpZeroCommandGroup extends ParallelCommandGroup {

    public AmpZeroCommandGroup() {
        addCommands(
                new SequentialCommandGroup(
                        new ArmRotationZeroCommand(),
                        new ArmExtendAmpZeroCommand()),
                new WinchAmpZeroCommand());
    }

}

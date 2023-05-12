package com.team2357.frc2023.commands.auto.support;

import com.team2357.frc2023.commands.everybot.ClawHoldCubeCommand;
import com.team2357.frc2023.commands.everybot.ClawIntakeCubeCommand;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class CubePrePoseClaw extends SequentialCommandGroup {
    public CubePrePoseClaw() {
        super(
            new ClawIntakeCubeCommand().withTimeout(0.25),
            new ClawHoldCubeCommand()
        );
    }
}

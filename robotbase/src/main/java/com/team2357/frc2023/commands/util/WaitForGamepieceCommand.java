package com.team2357.frc2023.commands.util;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class WaitForGamepieceCommand extends CommandBase {
    public WaitForGamepieceCommand() {

    }

    @Override
    public void initialize() {

    }

    @Override
    public boolean isFinished() {
        return Math.abs(IntakeRollerSubsystem.getInstance().getAmps()) > Constants.INTAKE_ROLLER.INTAKE_ROLLER_AMP_LIMIT;
    }
}

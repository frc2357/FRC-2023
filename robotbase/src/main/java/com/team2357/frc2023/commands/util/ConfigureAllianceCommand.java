package com.team2357.frc2023.commands.util;

import com.team2357.frc2023.apriltag.GridCamEstimator;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ConfigureAllianceCommand extends CommandBase{
    private Alliance m_alliance;

    public ConfigureAllianceCommand(Alliance alliance) {
        m_alliance = alliance;
    }

    @Override
    public void initialize() {
        GridCamEstimator.getInstance().configureField(m_alliance);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}

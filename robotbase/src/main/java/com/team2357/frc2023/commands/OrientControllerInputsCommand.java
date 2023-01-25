package com.team2357.frc2023.commands;

import com.team2357.frc2023.controls.SwerveDriveControls;
import com.team2357.frc2023.util.Utils;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class OrientControllerInputsCommand extends CommandBase {

    public OrientControllerInputsCommand() {
        Utils.m_alliance = DriverStation.getAlliance();
    }

    @Override
    public void initialize() {
        Utils.m_alliance = DriverStation.getAlliance();

        if (Utils.m_alliance == DriverStation.Alliance.Red) {
            SwerveDriveControls.isFlipped = true;
        } else {
            SwerveDriveControls.isFlipped = false;
        }
    }

}

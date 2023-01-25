package com.team2357.frc2023.commands;

import com.team2357.frc2023.controls.SwerveDriveControls;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class OrientControllerInputsCommand extends CommandBase {

    public DriverStation.Alliance m_alliance;
    
    public OrientControllerInputsCommand() {}

    @Override
    public void initialize() {
        m_alliance = DriverStation.getAlliance();

        if (m_alliance == DriverStation.Alliance.Red) {
            SwerveDriveControls.isFlipped = true;
        } else {
            SwerveDriveControls.isFlipped = false;
        }
    }

}

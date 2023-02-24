package com.team2357.frc2023.util;

import edu.wpi.first.wpilibj.DriverStation;

public class DriverStationAllianceGetter {
    
    private static DriverStation.Alliance m_alliance;

    public static DriverStation.Alliance getAlliance() {
        if(m_alliance == null) {
            m_alliance = DriverStation.getAlliance();
        }
        return m_alliance;
    }
}

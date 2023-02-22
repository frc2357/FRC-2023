package com.team2357.frc2023.util;

import com.team2357.frc2023.commands.scoring.AutoScoreHighCommand;
import com.team2357.frc2023.commands.scoring.AutoScoreLowCommand;
import com.team2357.frc2023.commands.scoring.AutoScoreMidCommand;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;

public class Utils {
    
    public static DriverStation.Alliance m_alliance;

    
    /**
     * @param row row to score on (low: 0, mid: 1, high: 2)
     * @return Auto score command to run
     */
    public static Command getAutoScoreCommands(int row) {
        switch (row) {
            case 0:
                return new AutoScoreLowCommand();
            case 1:
                return new AutoScoreMidCommand();
            case 2:
                return new AutoScoreHighCommand();
            default:
                return new AutoScoreLowCommand();
        }
    }

}

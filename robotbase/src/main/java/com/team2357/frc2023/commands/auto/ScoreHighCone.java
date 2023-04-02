package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.commands.scoring.cone.ConeHighPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeHighScoreCommand;
import com.team2357.frc2023.commands.util.AutonomousZeroCommand;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ScoreHighCone extends SequentialCommandGroup {
    public ScoreHighCone() {
        addCommands(
            // Initialize
            new AutonomousZeroCommand(),
            // Score cone high
            new ConeHighPrePoseCommand(true),
            new ConeHighScoreCommand()
        );
    }
}

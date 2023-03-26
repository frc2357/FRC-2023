package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.commands.scoring.cone.ConeAutoScoreHighCommandGroup;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class ScoreHighAutoCommand extends ParallelCommandGroup {
    public ScoreHighAutoCommand() {
        addCommands(
            new ConeAutoScoreHighCommandGroup()
        );
    }
}
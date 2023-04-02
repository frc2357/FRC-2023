package com.team2357.frc2023.trajectoryutil;

import com.team2357.frc2023.commands.auto.Col4StowBalance;
import com.team2357.frc2023.commands.auto.Col9Col7Balance;
import com.team2357.frc2023.commands.auto.ScoreHighCone;

import edu.wpi.first.wpilibj2.command.Command;

public class AvailableTrajectoryCommands {
    public static Command scoreHighCone;
    public static Command col4StowBalance;
    public static Command col9Col7Balance;

    public static void generateTrajectories() {
        scoreHighCone = new ScoreHighCone();
        col4StowBalance = new Col4StowBalance();
        col9Col7Balance = new Col9Col7Balance();
    }
}

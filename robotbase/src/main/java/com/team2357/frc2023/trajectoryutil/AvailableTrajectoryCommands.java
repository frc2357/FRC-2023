package com.team2357.frc2023.trajectoryutil;

import com.team2357.frc2023.commands.auto.ScoreHighAutoCommand;
import com.team2357.frc2023.commands.auto.gridone.GridOneScoreOneAndBalance;
import com.team2357.frc2023.commands.auto.gridone.GridOneScoreOneAndMobility;
import com.team2357.frc2023.commands.auto.gridtwo.GridTwoTwoConeAutoCommand;
import com.team2357.frc2023.commands.auto.gridzero.GridZeroTwoConeAutoCommand;

import edu.wpi.first.wpilibj2.command.Command;

public class AvailableTrajectoryCommands {
    public static Command GridZeroTwoConeAuto;

    public static Command GridTwoTwoConeAuto;

    public static Command GridOneScoreOneAndBalance;

    public static Command Score;

    public static Command GridOneMobility;

    public static void generateTrajectories() {
        GridZeroTwoConeAuto = new GridZeroTwoConeAutoCommand();
  
        GridTwoTwoConeAuto = new GridTwoTwoConeAutoCommand();

        GridOneScoreOneAndBalance = new GridOneScoreOneAndBalance();

        Score = new ScoreHighAutoCommand();

        GridOneMobility = new GridOneScoreOneAndMobility();
    }
}

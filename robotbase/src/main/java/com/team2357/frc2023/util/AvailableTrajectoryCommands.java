package com.team2357.frc2023.util;

import com.team2357.frc2023.commands.auto.blue.grid1.BlueGridOneTwoConeAutoCommand;
import com.team2357.frc2023.commands.auto.blue.grid1.BlueGridOneTwoConeBalanceAutoCommand;
import com.team2357.frc2023.commands.auto.blue.grid2.BlueGridTwoTwoConeAutoCommand;
import com.team2357.frc2023.commands.auto.blue.grid2.BlueGridTwoTwoConeBalanceAutoCommand;
import com.team2357.frc2023.commands.auto.blue.grid3.BlueGridThreeTwoConeAutoCommand;
import com.team2357.frc2023.commands.auto.blue.grid3.BlueGridThreeTwoConeBalanceAutoCommand;
import com.team2357.frc2023.commands.auto.red.grid1.RedGridOneTwoConeAutoCommand;
import com.team2357.frc2023.commands.auto.red.grid1.RedGridOneTwoConeBalanceAutoCommand;
import com.team2357.frc2023.commands.auto.red.grid2.RedGridTwoTwoConeAutoCommand;
import com.team2357.frc2023.commands.auto.red.grid2.RedGridTwoTwoConeBalanceAutoCommand;
import com.team2357.frc2023.commands.auto.red.grid3.RedGridThreeTwoConeAutoCommand;
import com.team2357.frc2023.commands.auto.red.grid3.RedGridThreeTwoConeBalanceAutoCommand;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class AvailableTrajectoryCommands {
    public static SequentialCommandGroup blueGridOneTwoConeAuto;
    public static SequentialCommandGroup blueGridOneTwoConeBalanceAuto;
    public static SequentialCommandGroup blueGridTwoTwoConeAuto;
    public static SequentialCommandGroup blueGridTwoTwoConeBalanceAuto;
    public static SequentialCommandGroup blueGridThreeTwoConeAuto;
    public static SequentialCommandGroup blueGridThreeTwoConeBalanceAuto;
    public static SequentialCommandGroup redGridOneTwoConeAuto;
    public static SequentialCommandGroup redGridOneTwoConeBalanceAuto;
    public static SequentialCommandGroup redGridTwoTwoConeAuto;
    public static SequentialCommandGroup redGridTwoTwoConeBalanceAuto;
    public static SequentialCommandGroup redGridThreeTwoConeAuto;
    public static SequentialCommandGroup redGridThreeTwoConeBalanceAuto;

    public static void generateTrajectories() {
        blueGridOneTwoConeAuto = new BlueGridOneTwoConeAutoCommand();
        blueGridOneTwoConeBalanceAuto = new BlueGridOneTwoConeBalanceAutoCommand();
        blueGridTwoTwoConeAuto = new BlueGridTwoTwoConeAutoCommand();
        blueGridTwoTwoConeBalanceAuto = new BlueGridTwoTwoConeBalanceAutoCommand();
        blueGridThreeTwoConeAuto = new BlueGridThreeTwoConeAutoCommand();
        blueGridThreeTwoConeBalanceAuto = new BlueGridThreeTwoConeBalanceAutoCommand();

        redGridOneTwoConeAuto = new RedGridOneTwoConeAutoCommand();
        redGridOneTwoConeBalanceAuto = new RedGridOneTwoConeBalanceAutoCommand();
        redGridTwoTwoConeAuto = new RedGridTwoTwoConeAutoCommand();
        redGridTwoTwoConeBalanceAuto = new RedGridTwoTwoConeBalanceAutoCommand();
        redGridThreeTwoConeAuto = new RedGridThreeTwoConeAutoCommand();
        redGridThreeTwoConeBalanceAuto = new RedGridThreeTwoConeBalanceAutoCommand();
    }
}

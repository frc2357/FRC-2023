package com.team2357.frc2023.trajectoryutil;

import com.team2357.frc2023.commands.auto.blue.gridone.BlueGridTwoTwoConeAutoCommand;
import com.team2357.frc2023.commands.auto.blue.gridone.BlueGridTwoTwoConeBalanceAutoCommand;
import com.team2357.frc2023.commands.auto.blue.gridtwo.BlueGridThreeTwoConeAutoCommand;
import com.team2357.frc2023.commands.auto.blue.gridtwo.BlueGridThreeTwoConeBalanceAutoCommand;
import com.team2357.frc2023.commands.auto.blue.gridzero.BlueGridOneTwoConeAutoCommand;
import com.team2357.frc2023.commands.auto.blue.gridzero.BlueGridOneTwoConeBalanceAutoCommand;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class AvailableTrajectoryCommands {
    public static SequentialCommandGroup blueGridOneTwoConeAuto;
    public static SequentialCommandGroup blueGridOneTwoConeBalanceAuto;
    public static SequentialCommandGroup blueGridTwoTwoConeAuto;
    public static SequentialCommandGroup blueGridTwoTwoConeBalanceAuto;
    public static SequentialCommandGroup blueGridThreeTwoConeAuto;
    public static SequentialCommandGroup blueGridThreeTwoConeBalanceAuto;

    public static void generateTrajectories() {
        blueGridOneTwoConeAuto = new BlueGridOneTwoConeAutoCommand();
        blueGridOneTwoConeBalanceAuto = new BlueGridOneTwoConeBalanceAutoCommand();
        blueGridTwoTwoConeAuto = new BlueGridTwoTwoConeAutoCommand();
        blueGridTwoTwoConeBalanceAuto = new BlueGridTwoTwoConeBalanceAutoCommand();
        blueGridThreeTwoConeAuto = new BlueGridThreeTwoConeAutoCommand();
        blueGridThreeTwoConeBalanceAuto = new BlueGridThreeTwoConeBalanceAutoCommand();

    }
}

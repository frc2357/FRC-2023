package com.team2357.frc2023.trajectoryutil;

import com.team2357.frc2023.commands.auto.gridone.GridOneTwoConeBalanceAutoCommand;
import com.team2357.frc2023.commands.auto.gridone.GridOneTwoTwoConeAutoCommand;
import com.team2357.frc2023.commands.auto.gridtwo.GridTwoTwoConeAutoCommand;
import com.team2357.frc2023.commands.auto.gridtwo.GridTwoTwoConeBalanceAutoCommand;
import com.team2357.frc2023.commands.auto.gridzero.GridZeroTwoConeAutoCommand;
import com.team2357.frc2023.commands.auto.gridzero.GridZeroTwoConeBalanceAutoCommand;

import edu.wpi.first.wpilibj2.command.Command;

public class AvailableTrajectoryCommands {
    public static Command GridZeroTwoConeAuto;
    public static Command GridZeroTwoConeBalanceAuto;

    public static Command GridOneTwoConeAuto;
    public static Command GridOneTwoConeBalanceAuto;

    public static Command GridTwoTwoConeAuto;
    public static Command GridTwoTwoConeBalanceAuto;

    public static void generateTrajectories() {
        GridZeroTwoConeAuto = new GridZeroTwoConeAutoCommand();
        GridZeroTwoConeBalanceAuto = new GridZeroTwoConeBalanceAutoCommand();

        GridOneTwoConeAuto = new GridOneTwoTwoConeAutoCommand();
        GridOneTwoConeBalanceAuto = new GridOneTwoConeBalanceAutoCommand();
        
        GridTwoTwoConeAuto = new GridTwoTwoConeAutoCommand();
        GridTwoTwoConeBalanceAuto = new GridTwoTwoConeBalanceAutoCommand();
    }
}

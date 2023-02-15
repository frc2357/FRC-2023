package com.networktablerelayapp;

import java.io.IOException;
import java.util.Arrays;

import edu.wpi.first.cscore.CameraServerJNI;
import edu.wpi.first.math.WPIMathJNI;
import edu.wpi.first.networktables.BooleanArraySubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;

/**
 * Program
 */
public class Main {

    private static final String BUTTONBOARD_TABLE_NAME = "buttonboard";
    private static final String GRID_STATES_TOPIC_NAME = "GridStates";

    private static final String NT4_CLIENT_IDENTITY = "network table relay";
    private static final int TEAM_NAME = 2357;

    public static void main(String[] args) throws IOException {
        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        WPIMathJNI.Helper.setExtractOnStaticLoad(false);
        CameraServerJNI.Helper.setExtractOnStaticLoad(false);

        CombinedRuntimeLoader.loadLibraries(Main.class, "wpiutiljni", "wpimathjni", "ntcorejni", "cscorejnicvstatic");
        new Main().run();
    }

    public void run() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        NetworkTable buttonboardTable = inst.getTable(BUTTONBOARD_TABLE_NAME);
        BooleanArraySubscriber gridStatesSub = buttonboardTable.getBooleanArrayTopic(GRID_STATES_TOPIC_NAME).subscribe(new boolean[]{});
        boolean[] gridStates = new boolean[] {};

        inst.startClient4(NT4_CLIENT_IDENTITY);
        inst.setServerTeam(TEAM_NAME);
        inst.startDSClient(); 

        while (true) {
            boolean[] newGridStates = gridStatesSub.get();

            if(!Arrays.equals(newGridStates, gridStates)) {
                gridStates = newGridStates;

                // Send to ardunio
            }
        }
    }
}

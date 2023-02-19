package com.networktablerelayapp;

import java.io.IOException;
import java.util.Arrays;

import edu.wpi.first.cscore.CameraServerJNI;
import edu.wpi.first.math.WPIMathJNI;
import edu.wpi.first.networktables.BooleanArraySubscriber;
import edu.wpi.first.networktables.IntegerPublisher;
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

    private static final String ROW_TOPIC_NAME = "row";
    private static final String COL_TOPIC_NAME = "col";

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

        IntegerPublisher rowPub = buttonboardTable.getIntegerTopic(ROW_TOPIC_NAME).publish();
        IntegerPublisher colPub = buttonboardTable.getIntegerTopic(COL_TOPIC_NAME).publish();

        inst.startClient4(NT4_CLIENT_IDENTITY);
        inst.setServerTeam(TEAM_NAME);
        inst.startDSClient(); 

        int row = 0;
        int col = 0;
        while (true) {
            boolean[] newGridStates = gridStatesSub.get();

            if(!Arrays.equals(newGridStates, gridStates)) {
                gridStates = newGridStates;

                // Send to ardunio
            }

            rowPub.set(row);
            colPub.set(col);
            
            row++;
            if(row > 2) {
                row = 0;
            }

            col++;
            if(col > 8) {
                col = 0;
            }

            try {
            Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
    }
}

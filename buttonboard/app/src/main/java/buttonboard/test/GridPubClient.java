package buttonboard.test;

import java.io.IOException;

import edu.wpi.first.cscore.CameraServerJNI;
import edu.wpi.first.math.WPIMathJNI;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.networktables.StringArrayPublisher;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;

import buttonboard.Constants;

public class GridPubClient {
    private String m_serverName;
    private int m_connListenerHandle;
    private NetworkTable m_gridCamTable;
    private StringArrayPublisher m_gridPub;

    public GridPubClient(String serverName) {
        m_serverName = serverName;

        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        WPIMathJNI.Helper.setExtractOnStaticLoad(false);
        CameraServerJNI.Helper.setExtractOnStaticLoad(false);

        try {
            CombinedRuntimeLoader.loadLibraries(
                GridPubClient.class,
                "wpiutiljni",
                "wpimathjni",
                "ntcorejni",
                "cscorejnicvstatic"
            );
        } catch (IOException ioe) {
            System.err.println("Failed to load Network Tables libraries: " + ioe.getMessage());
            return;
        }
    }

    public void open() {
        System.out.println("NetworkTablesClient.open");
        NetworkTableInstance inst = NetworkTableInstance.getDefault();

        m_connListenerHandle = inst.addConnectionListener(
            true,
            event -> {
                if (event.is(NetworkTableEvent.Kind.kConnected)) {
                System.out.println("Connected to " + event.connInfo.remote_ip);
                } else if (event.is(NetworkTableEvent.Kind.kDisconnected)) {
                System.out.println("Disconnected from " + event.connInfo.remote_ip);
                }
            }
        );

        inst.startClient4("gridpub");
        inst.setServer(m_serverName);
        inst.startDSClient(); 

        m_gridCamTable = inst.getTable(Constants.NT_GRIDCAM_TABLE);

        m_gridPub = m_gridCamTable.getStringArrayTopic(Constants.NT_GRID_TOPIC).publish();
    }

    public void close() {
        System.out.println("NetworkTablesClient.close");
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        m_gridPub.close();
        inst.removeListener(m_connListenerHandle);
    }

    public void setGrid(String[] rows) {
        System.out.println("setting grid to:");
        System.out.println(rows[0]);
        System.out.println(rows[1]);
        System.out.println(rows[2]);
        m_gridPub.set(rows);
    }
}

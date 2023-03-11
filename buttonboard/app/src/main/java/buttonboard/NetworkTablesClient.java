package buttonboard;

import java.io.IOException;
import java.lang.Math;
import java.util.EnumSet;

import com.google.common.graph.Network;

import edu.wpi.first.cscore.CameraServerJNI;
import edu.wpi.first.math.WPIMathJNI;
import edu.wpi.first.networktables.IntegerPublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.networktables.StringArraySubscriber;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;

public class NetworkTablesClient {
    private String[] m_grid = {"", "", ""};
    private String m_serverName;
    private int m_connListenerHandle;
    private NetworkTable m_buttonboardTable;
    private NetworkTable m_gridCamTable;
    private StringArraySubscriber m_gridSub;
    private IntegerPublisher m_targetRowPub;
    private IntegerPublisher m_targetColPub;

    public NetworkTablesClient(String serverName) {
        m_serverName = serverName;

        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        WPIMathJNI.Helper.setExtractOnStaticLoad(false);
        CameraServerJNI.Helper.setExtractOnStaticLoad(false);

        try {
            CombinedRuntimeLoader.loadLibraries(
                NetworkTablesClient.class,
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

        inst.startClient4(Constants.NT4_CLIENT_IDENTITY);
        inst.setServer(m_serverName);
        inst.startDSClient(); 

        m_gridCamTable = inst.getTable(Constants.NT_GRIDCAM_TABLE);
        m_buttonboardTable = inst.getTable(Constants.NT_BUTTONBOARD_TABLE);

        m_gridSub = m_gridCamTable.getStringArrayTopic(Constants.NT_GRID_TOPIC).subscribe(new String[] {"", "", ""});

        inst.addListener(
            m_gridSub,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll),
            event -> {
                m_grid = event.valueData.value.getStringArray();
                System.out.println("grid set to:");
                System.out.println(m_grid[0]);
                System.out.println(m_grid[1]);
                System.out.println(m_grid[2]);
            }
        );

        m_targetRowPub = m_buttonboardTable.getIntegerTopic(Constants.NT_TARGET_ROW_TOPIC).publish();
        m_targetRowPub.setDefault(-1);

        m_targetColPub = m_buttonboardTable.getIntegerTopic(Constants.NT_TARGET_COL_TOPIC).publish();
        m_targetColPub.setDefault(-1);
    }

    public void close() {
        System.out.println("NetworkTablesClient.close");
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        m_gridSub.close();
        m_targetRowPub.close();
        m_targetColPub.close();
        inst.removeListener(m_connListenerHandle);
    }

    public void setGridTarget(int row, int col) {
        System.out.println("Set grid target to (row=" + row + ", col=" + col + ")");
        m_targetRowPub.set(row);
        m_targetColPub.set(col);
    }
}

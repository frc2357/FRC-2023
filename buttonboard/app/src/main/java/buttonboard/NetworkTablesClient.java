package buttonboard;

import java.io.IOException;
import java.util.EnumSet;

import edu.wpi.first.cscore.CameraServerJNI;
import edu.wpi.first.math.WPIMathJNI;
import edu.wpi.first.networktables.IntegerPublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.networktables.StringArraySubscriber;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;

public class NetworkTablesClient {
    public static interface GridListener {
        public void gridUpdated(String high, String mid, String low);
    }

    private String m_serverName;
    private int m_connListenerHandle;
    private NetworkTable m_buttonboardTable;
    private NetworkTable m_gridCamTable;
    private StringArraySubscriber m_gridSub;
    private StringPublisher m_alliancePub;
    private IntegerPublisher m_targetRowPub;
    private IntegerPublisher m_targetColPub;
    private IntegerPublisher m_targetTypePub;
    private GridListener m_gridListener;

    public NetworkTablesClient() {
        this(null);
    }

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

    public void setGridListener(GridListener l) {
        m_gridListener = l;
    }

    public void open() {
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
        if (m_serverName != null) {
            inst.setServer(m_serverName);
        } else {
            inst.setServerTeam(Constants.TEAM_NUMBER);
        }
        inst.startDSClient(); 

        m_gridCamTable = inst.getTable(Constants.NT_GRIDCAM_TABLE);
        m_buttonboardTable = inst.getTable(Constants.NT_BUTTONBOARD_TABLE);

        m_gridSub = m_gridCamTable.getStringArrayTopic(Constants.NT_GRID_TOPIC).subscribe(new String[] {"", "", ""});

        inst.addListener(
            m_gridSub,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll),
            event -> {
                String grid[] = event.valueData.value.getStringArray();
                if (m_gridListener != null) {
                    m_gridListener.gridUpdated(grid[0], grid[1], grid[2]);
                }
            }
        );

        m_targetRowPub = m_buttonboardTable.getIntegerTopic(Constants.NT_TARGET_ROW_TOPIC).publish();
        m_targetRowPub.setDefault(-1);

        m_targetColPub = m_buttonboardTable.getIntegerTopic(Constants.NT_TARGET_COL_TOPIC).publish();
        m_targetColPub.setDefault(-1);

        m_targetTypePub = m_buttonboardTable.getIntegerTopic(Constants.NT_TARGET_TYPE_TOPIC).publish();
        m_targetTypePub.setDefault(-1);

        m_alliancePub = m_buttonboardTable.getStringTopic(Constants.NT_ALLIANCE_TOPIC).publish();
        m_alliancePub.setDefault(Constants.ALLIANCE_UNSET);
    }

    public void close() {
        System.out.println("NetworkTablesClient.close");
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        m_gridSub.close();
        m_alliancePub.close();
        m_targetRowPub.close();
        m_targetColPub.close();
        m_targetTypePub.close();
        inst.removeListener(m_connListenerHandle);
    }

    public void setGridTarget(int row, int col, int type) {
        System.out.println("Set grid target to (row=" + row + ", col=" + col + ", type=" + type + ")");
        m_targetRowPub.set(row);
        m_targetColPub.set(col);
        m_targetTypePub.set(type);
    }

    public void setAlliance(String alliance) {
        System.out.println("Set alliance to " + alliance);
        m_alliancePub.set(alliance);
    }
}

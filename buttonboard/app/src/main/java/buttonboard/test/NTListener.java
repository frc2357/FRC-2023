package buttonboard.test;

import java.io.IOException;
import java.util.EnumSet;

import edu.wpi.first.cscore.CameraServerJNI;
import edu.wpi.first.math.WPIMathJNI;
import edu.wpi.first.networktables.IntegerSubscriber;
import edu.wpi.first.networktables.StringArraySubscriber;
import edu.wpi.first.networktables.StringSubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;

import buttonboard.Constants;

public class NTListener {
    private int m_connListenerHandle;
    private int m_topicListenerHandle;
    private int m_gridListenerHandle;
    private int m_targetRowListenerHandle;
    private int m_targetColListenerHandle;
    private int m_targetTypeListenerHandle;
    private int m_allianceListenerHandle;

    private String m_serverName;
    private NetworkTable m_buttonboardTable;
    private NetworkTable m_gridCamTable;
    private StringArraySubscriber m_gridSub;
    private StringSubscriber m_allianceSub;
    private IntegerSubscriber m_targetRowSub;
    private IntegerSubscriber m_targetColSub;
    private IntegerSubscriber m_targetTypeSub;

    public NTListener() {
      this(null);
    }

    public NTListener(String serverName) {
        m_serverName = serverName;

        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        WPIMathJNI.Helper.setExtractOnStaticLoad(false);
        CameraServerJNI.Helper.setExtractOnStaticLoad(false);

        try {
            CombinedRuntimeLoader.loadLibraries(
                NTListener.class,
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

        m_gridListenerHandle = inst.addListener(
            m_gridSub,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll),
            event -> {
                String grid[] = event.valueData.value.getStringArray();
                System.out.println("Grid: " + grid[0] + "/" + grid[1] + "/" + grid[2]);
            }
        );

        m_allianceSub = m_buttonboardTable.getStringTopic(Constants.NT_ALLIANCE_TOPIC).subscribe(Constants.ALLIANCE_UNSET);

        m_allianceListenerHandle = inst.addListener(
            m_allianceSub,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll),
            event -> {
                String alliance = event.valueData.value.getString();
                System.out.println("Alliance: " + alliance);
            }
        );

        m_targetRowSub = m_buttonboardTable.getIntegerTopic(Constants.NT_TARGET_ROW_TOPIC).subscribe(-1);

        m_targetRowListenerHandle = inst.addListener(
            m_targetRowSub,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll),
            event -> {
                long targetRow = event.valueData.value.getInteger();
                System.out.println("Target Row: " + targetRow);
            }
        );

        m_targetColSub = m_buttonboardTable.getIntegerTopic(Constants.NT_TARGET_COL_TOPIC).subscribe(-1);

        m_targetColListenerHandle = inst.addListener(
            m_targetColSub,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll),
            event -> {
                long targetCol = event.valueData.value.getInteger();
                System.out.println("Target Col: " + targetCol);
            }
        );

        m_targetTypeSub = m_buttonboardTable.getIntegerTopic(Constants.NT_TARGET_TYPE_TOPIC).subscribe(-1);

        m_targetTypeListenerHandle = inst.addListener(
            m_targetTypeSub,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll),
            event -> {
                long targetType = event.valueData.value.getInteger();
                System.out.println("Target Type: " + targetType);
            }
        );

        m_connListenerHandle = inst.addConnectionListener(true, event -> {
            if (event.is(NetworkTableEvent.Kind.kConnected)) {
                System.out.println("Connected to " + event.connInfo.remote_id);
            } else if (event.is(NetworkTableEvent.Kind.kDisconnected)) {
                System.out.println("Disconnected from " + event.connInfo.remote_id);
            }
        });
    }

    public void close() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        inst.removeListener(m_topicListenerHandle);
        inst.removeListener(m_gridListenerHandle);
        inst.removeListener(m_targetRowListenerHandle);
        inst.removeListener(m_targetColListenerHandle);
        inst.removeListener(m_targetTypeListenerHandle);
        inst.removeListener(m_allianceListenerHandle);
        inst.removeListener(m_connListenerHandle);
        m_gridSub.close();
        m_targetColSub.close();
        m_targetRowSub.close();
        m_targetTypeSub.close();
    }
}

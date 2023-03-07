package buttonboard;

import java.io.IOException;
import java.util.EnumSet;

import edu.wpi.first.cscore.CameraServerJNI;
import edu.wpi.first.math.WPIMathJNI;
import edu.wpi.first.networktables.IntegerSubscriber;
import edu.wpi.first.networktables.StringArraySubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;

import buttonboard.Constants;

public class NetworkTablesServer {
    private int m_connListenerHandle;
    private int m_topicListenerHandle;
    private int m_gridListenerHandle;
    private int m_targetRowListenerHandle;
    private int m_targetColListenerHandle;

    private NetworkTable m_buttonboardTable;
    private NetworkTable m_gridCamTable;
    private StringArraySubscriber m_gridSub;
    private IntegerSubscriber m_targetRowSub;
    private IntegerSubscriber m_targetColSub;

    public NetworkTablesServer() {
        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        WPIMathJNI.Helper.setExtractOnStaticLoad(false);
        CameraServerJNI.Helper.setExtractOnStaticLoad(false);

        try {
            CombinedRuntimeLoader.loadLibraries(NetworkTablesClient.class, "wpiutiljni", "wpimathjni", "ntcorejni", "cscorejnicvstatic");
        } catch (IOException ioe) {
            System.err.println("Failed to load Network Tables libraries: " + ioe.getMessage());
            return;
        }

        open();
    }

    public void open() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        inst.setServer("localhost");
        inst.startServer();

        m_gridCamTable = inst.getTable(Constants.NT_GRIDCAM_TABLE);
        m_gridSub = m_gridCamTable.getStringArrayTopic(Constants.NT_GRID_TOPIC).subscribe(new String[] {"", "", ""});

        m_buttonboardTable = inst.getTable(Constants.NT_BUTTONBOARD_TABLE);
        m_targetRowSub = m_buttonboardTable.getIntegerTopic(Constants.NT_TARGET_ROW_TOPIC).subscribe(-1);
        m_targetColSub = m_buttonboardTable.getIntegerTopic(Constants.NT_TARGET_COL_TOPIC).subscribe(-1);

        m_connListenerHandle = inst.addConnectionListener(true, event -> {
            if (event.is(NetworkTableEvent.Kind.kConnected)) {
                System.out.println("Connected to " + event.connInfo.remote_id);
            } else if (event.is(NetworkTableEvent.Kind.kDisconnected)) {
                System.out.println("Disconnected from " + event.connInfo.remote_id);
            }
        });

        m_gridListenerHandle = inst.addListener(
            m_gridSub,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll),
            event -> {
                String grid[] = event.valueData.value.getStringArray();
                System.out.println("grid set to:");
                System.out.println(grid[0]);
                System.out.println(grid[1]);
                System.out.println(grid[2]);
            }
        );

        m_targetRowListenerHandle = inst.addListener(
            m_targetRowSub,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll),
            event -> {
                long row = event.valueData.value.getInteger();
                System.out.println("target row set to: " + row);
            }
        );

        m_targetColListenerHandle = inst.addListener(
            m_targetColSub,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll),
            event -> {
                long col = event.valueData.value.getInteger();
                System.out.println("target col set to: " + col);
            }
        );
    }

    public void close() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        inst.removeListener(m_topicListenerHandle);
        inst.removeListener(m_gridListenerHandle);
        inst.removeListener(m_targetRowListenerHandle);
        inst.removeListener(m_targetColListenerHandle);
        inst.removeListener(m_connListenerHandle);
        m_gridSub.close();
        m_targetColSub.close();
        m_targetRowSub.close();
    }
}

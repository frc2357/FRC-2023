package buttonboard;

import java.io.IOException;
import java.util.EnumSet;

import edu.wpi.first.cscore.CameraServerJNI;
import edu.wpi.first.math.WPIMathJNI;
import edu.wpi.first.networktables.IntegerArraySubscriber;
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
    private int m_valueListenerHandle;

    private IntegerArraySubscriber m_gridTargetSub;

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

        NetworkTable datatable = inst.getTable(Constants.NT_TABLE_NAME);
        m_gridTargetSub = datatable.getIntegerArrayTopic(Constants.NT_GRID_TARGET).subscribe(new long[] {-1, -1});

        m_connListenerHandle = inst.addConnectionListener(true, event -> {
            if (event.is(NetworkTableEvent.Kind.kConnected)) {
                System.out.println("Connected to " + event.connInfo.remote_id);
            } else if (event.is(NetworkTableEvent.Kind.kDisconnected)) {
                System.out.println("Disconnected from " + event.connInfo.remote_id);
            }
        });

        m_topicListenerHandle = inst.addListener(
            new String[] { datatable.getPath() + "/" },
            EnumSet.of(NetworkTableEvent.Kind.kTopic),
            event -> {
                if (event.is(NetworkTableEvent.Kind.kPublish)) {
                // topicInfo.name is the full topic name, e.g. "/datatable/X"
                System.out.println("newly published " + event.topicInfo.name);
                }
        });

        m_valueListenerHandle = inst.addListener(
            m_gridTargetSub,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll),
            event -> {
                long gridArray[] = event.valueData.value.getIntegerArray();
                long gridX = gridArray[0];
                long gridY = gridArray[1];
                System.out.println("gridTarget set to (" + gridX + ", " + gridY + ")");
            }
        );
    }

    public void close() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        inst.removeListener(m_topicListenerHandle);
        inst.removeListener(m_valueListenerHandle);
        inst.removeListener(m_connListenerHandle);
        m_gridTargetSub.close();
    }

    public long getGridX() {
        long[] gridTarget = m_gridTargetSub.get();
        return gridTarget[0];
    }

    public long getGridY() {
        long[] gridTarget = m_gridTargetSub.get();
        return gridTarget[1];
    }
}

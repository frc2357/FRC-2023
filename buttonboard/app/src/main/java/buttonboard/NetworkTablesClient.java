package buttonboard;

import java.io.IOException;
import java.lang.Math;

import com.google.common.graph.Network;

import edu.wpi.first.cscore.CameraServerJNI;
import edu.wpi.first.math.WPIMathJNI;
import edu.wpi.first.networktables.IntegerArrayPublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;

public class NetworkTablesClient {
    private long[] m_gridTarget = {-1, -1};
    private IntegerArrayPublisher m_gridTargetPub;

    public NetworkTablesClient() {
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
    }

    public void open() {
        System.out.println("NetworkTablesClient.open");
        NetworkTableInstance inst = NetworkTableInstance.getDefault();

        NetworkTable datatable = inst.getTable(Constants.NT_TABLE_NAME);

        inst.startClient4(Constants.NT4_CLIENT_IDENTITY);
        inst.setServer("localhost");
        inst.startDSClient(); 

        m_gridTargetPub = datatable.getIntegerArrayTopic(Constants.NT_GRID_TARGET).publish();
        m_gridTargetPub.setDefault(new long[] {-1, -1});
    }

    public void close() {
        System.out.println("NetworkTablesClient.close");
        m_gridTargetPub.close();
    }

    public void setGridTarget(long x, long y) {
        m_gridTarget[0] = x;
        m_gridTarget[1] = y;
        m_gridTargetPub.set(m_gridTarget);
    }
}

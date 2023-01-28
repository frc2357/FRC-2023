
import edu.wpi.first.networktables.BooleanArraySubscriber;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.DoubleTopic;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;

import java.io.IOException;
import java.util.Arrays;

import edu.wpi.first.cscore.CameraServerJNI;
import edu.wpi.first.math.WPIMathJNI;
import edu.wpi.first.util.WPIUtilJNI;

/**
 * Program
 */
public class Program {
    public static void main(String[] args) throws IOException {
        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        WPIMathJNI.Helper.setExtractOnStaticLoad(false);
        CameraServerJNI.Helper.setExtractOnStaticLoad(false);

        CombinedRuntimeLoader.loadLibraries(Program.class, "wpiutiljni", "wpimathjni", "ntcorejni", "cscorejnicvstatic");
        new Program().run();
    }

    public void run() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        NetworkTable table = inst.getTable("datatable");
        //DoublePublisher testPub = table.getDoubleTopic("testTopic").publish();
        BooleanArraySubscriber testSub = table.getBooleanArrayTopic("testTopic").subscribe(new boolean[] {});
        
        inst.startClient4("example client");
        //inst.setServer("localhost"); // where TEAM=190, 294, etc, or use inst.setServer("hostname") or similar
        inst.setServerTeam(2357);
        inst.startDSClient(); // recommended if running on DS computer; this gets the robot IP from the DS

        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                System.out.println("interrupted");
                return;
            }

            // System.out.println("Test at start of iteration: "+test);
            // double startTime = System.currentTimeMillis();
            // testPub.set(test);

            boolean[] x = testSub.get();
            double timeStamp = testSub.getLastChange();
            double difference = NetworkTablesJNI.now() - timeStamp;

            System.out.println("test is: " + Arrays.toString(x) + ", set and received in "+difference);
        }
    }
}

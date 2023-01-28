
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.DoubleTopic;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;

import java.io.IOException;

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

        var inst = NetworkTableInstance.getDefault();
        new Program().run();
    }

    public void run() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        NetworkTable table = inst.getTable("datatable");
        //DoublePublisher testPub = table.getDoubleTopic("testTopic").publish();
        DoubleSubscriber testSub = table.getDoubleTopic("testTopic").subscribe(0.0);
        

        // For local only?????
        //inst.startClient4("example client");
        //inst.setServer("localhost"); // where TEAM=190, 294, etc, or use inst.setServer("hostname") or similar
        //inst.startDSClient(); // recommended if running on DS computer; this gets the robot IP from the DS
       
       double test = 0;
        while (true) {
            // try {
            //     Thread.sleep(500);
            // } catch (InterruptedException ex) {
            //     System.out.println("interrupted");
            //     return;
            // }

            // System.out.println("Test at start of iteration: "+test);
            // double startTime = System.currentTimeMillis();
            // testPub.set(test);

            double x = testSub.get();
            double timeStamp = testSub.getLastChange();
            double difference = NetworkTablesJNI.now() - timeStamp;

            System.out.println("test is: " + x + ", set and received in "+difference);
            test+=0.5;
        }
    }
}

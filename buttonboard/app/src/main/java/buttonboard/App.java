/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package buttonboard;

import java.lang.Math;
import buttonboard.ArduinoUSB;
import buttonboard.NetworkTablesClient;
import buttonboard.NetworkTablesServer;

public class App {
    private static final String ARG_TEST_ARDUINO = "test:arduino";
    private static final String ARG_TEST_NTCLIENT = "test:ntclient";
    private static final String ARG_TEST_NTSERVER = "test:ntserver";

    public static void main(String[] args) {
        try {
            if (args.length > 0 && args[0].length() > 0) {
                switch(args[0]) {
                    case ARG_TEST_ARDUINO:
                        testArduino();
                        System.exit(0);
                    case ARG_TEST_NTCLIENT:
                        testNetworkTablesClient();
                        System.exit(0);
                    case ARG_TEST_NTSERVER:
                        testNetworkTablesServer();
                        System.exit(0);
                    default:
                        System.err.println("Unrecognized command: '" + args[0] + "'");
                }
            }
        } catch (Exception e) {
            System.err.println("***");
            System.err.println("Unhandled exception: " + e.getMessage());
            e.printStackTrace();
            System.err.println("***");
        }
    }

    private static void testArduino() {
        ArduinoUSB.scan();
    }

    private static void testNetworkTablesClient() {
        NetworkTablesClient nt = new NetworkTablesClient();
        nt.open();
        try {
            System.out.println("Client ready");
            while (true) {
                long x = (long)(Math.random() * 9);
                long y = (long)(Math.random() * 3);
                System.out.println("Grid target x=" + x + ", y=" + y);
                nt.setGridTarget(x, y);
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            System.out.println("Client exiting");
            nt.close();
            return;
        }
    }

    private static void testNetworkTablesServer() {
        NetworkTablesServer nt = new NetworkTablesServer();
        nt.open();
        try {
            while (true) {
                System.out.println("Grid target x=" + nt.getGridX() + ", y=" + nt.getGridY());
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            System.out.println("Server exiting");
            nt.close();
            return;
        }
    }
}

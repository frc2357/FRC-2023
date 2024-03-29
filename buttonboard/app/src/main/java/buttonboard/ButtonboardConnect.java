/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package buttonboard;

import java.lang.Math;
import java.util.Arrays;
import java.util.List;

import buttonboard.test.GridPubClient;
import buttonboard.test.NTListener;
import buttonboard.arduino.ArduinoButtonboard;
import buttonboard.arduino.ArduinoDriverLights;

public class ButtonboardConnect {
    private static final String ARG_TEST_ARDUINO = "test:buttonboard";
    private static final String ARG_TEST_NTCLIENT = "test:ntclient";
    private static final String ARG_TEST_NTSERVER = "test:ntserver";
    private static final String ARG_TEST_GRIDPUB = "test:gridpub";
    private static final String ARG_TEST_LISTENER = "test:listener";

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                // Run normally
                run();
            }

            if (args.length > 0 && args[0].length() > 0) {
                switch(args[0]) {
                    case ARG_TEST_ARDUINO:
                        testButtonboard();
                        System.exit(0);
                    case ARG_TEST_NTCLIENT:
                        testNetworkTablesClient();
                        System.exit(0);
                    case ARG_TEST_NTSERVER:
                        testNetworkTablesServer();
                        System.exit(0);
                    case ARG_TEST_GRIDPUB:
                        testGridPubClient();
                        System.exit(0);
                    case ARG_TEST_LISTENER:
                        testListener();
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

    private static void run() {
        System.out.println("----- Buttonboard FRC 2357 -----");
        NetworkTablesClient nt = new NetworkTablesClient();
        ArduinoButtonboard buttonboard = new ArduinoButtonboard(nt);
        ArduinoDriverLights driverLights = new ArduinoDriverLights(nt);
        nt.open();
        try {
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            System.out.println("Client exiting");
            nt.close();
            return;
        }
    }

    private static void testButtonboard() {
        System.out.println("TEST: Buttonboard");
        NetworkTablesClient nt = new NetworkTablesClient("localhost");
        ArduinoButtonboard buttonboard = new ArduinoButtonboard(nt);
        nt.open();
        try {
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            return;
        }
    }

    private static void testNetworkTablesClient() {
        System.out.println("TEST: Network Tables Client");
        NetworkTablesClient nt = new NetworkTablesClient("localhost");
        nt.open();
        try {
            System.out.println("Client ready");
            while (true) {
                int x = (int)(Math.random() * 9);
                int y = (int)(Math.random() * 3);
                int type = (int)(Math.random() * 3) - 1;
                nt.setGridTarget(x, y, type);
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            System.out.println("Client exiting");
            nt.close();
            return;
        }
    }

    private static void testGridPubClient() {
        System.out.println("TEST: Grid Publisher Client");
        GridPubClient nt = new GridPubClient("localhost");
        nt.open();
        try {
            System.out.println("Grid Pub Client ready");
            List<Integer> coneColumns = Arrays.asList(0, 2, 3, 5, 6, 8);
            List<Integer> cubeColumns = Arrays.asList(1, 4, 7);
            String[] rows = {"---------", "---------", "---------"};
            while (true) {
                // Each second, change one node on the grid randomly
                int row = (int)(Math.random() * 3);
                int col = (int)(Math.random() * 9);
                int nodeInt = (int)(Math.random() * 3);
                char nodeChar = nodeInt == 1 ? 'A' : nodeInt == 2 ? 'O' : '-';
                if (nodeChar == 'O' && row < 2 && coneColumns.contains(col)) {
                    // Has to be a cone
                    nodeChar = 'A';
                } else if (nodeChar == 'A' && row < 2 && cubeColumns.contains(col)) {
                    // Has to be a cube
                    nodeChar = 'O';
                }
                char[] nodeChars = rows[row].toCharArray();
                nodeChars[col] = nodeChar;
                rows[row] = new String(nodeChars);
                nt.setGrid(rows);
                Thread.sleep(5000);
            }
        } catch (InterruptedException ie) {
            System.out.println("Client exiting");
            nt.close();
            return;
        }
    }

    private static void testNetworkTablesServer() {
        System.out.println("TEST: Network Tables Server");
        NetworkTablesServer nt = new NetworkTablesServer();
        nt.open();
        try {
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            System.out.println("Server exiting");
            nt.close();
            return;
        }
    }

    private static void testListener() {
        System.out.println("TEST: Network Tables Listener");
        NTListener nt = new NTListener();
        nt.open();
        try {
            System.out.println("Listener ready");
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            System.out.println("Listener exiting");
            nt.close();
            return;
        }
    }
}

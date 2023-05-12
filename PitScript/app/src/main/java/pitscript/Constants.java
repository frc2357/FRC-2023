package pitscript;

public class Constants {
    // IP of the roborio
    public static final String ROBOT_IP = "10.23.57.2";
    // Username you use to connect to it
    public static final String USERNAME = "admin";
    // Should always be empty
    public static final String PASSWORD = "";
    // Where the files will go when downloaded
    public static final String LOCAL_FILE_DESTINATION = "C:\\Logs";
    /*
     * Where the files are stored on the robot, might change from year to year.
     * // You NEED the extra dash on the end, it tells the computer to get a file
     * // not a directory
     */
    public static final String REMOTE_FILE_PATH = "/home/lvuser/Logs/";

    //this is just the size of the groups that will be grouped into tasks
    public static final int FILE_GROUP_SIZE = 1;

    //the file extension that you want to transfer. this will probably not change
    public static final String DESIRED_FILE_TYPE = ".wpilog";

    public static final int THREAD_POOL_THREAD_COUNT = 10;
}

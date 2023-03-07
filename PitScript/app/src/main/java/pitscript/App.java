package pitscript;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class App {
    // IP of the roborio
    public static final String ROBOT_IP = "10.23.57.2";
    // Username you use to connect to it
    public static final String USERNAME = "lvuser";
    // Should always be empty
    public static final String PASSWORD = "";
    // Where the files will go when downloaded
    public static final String LOCAL_FILE_DESTINATION = "C:\\Logs";
    /* Where the files are stored on the robot, might change from year to year.
    // You NEED the extra dash on the end, it tells the computer to get a file
    // not a directory */
    public static final String REMOTE_FILE_PATH = "/home/lvuser/Logs/";

    public static OurSocketFactory m_socketFactory;
    public static JSch ssh;

    public static void main(String[] args) {

        File folderCheck = new File(LOCAL_FILE_DESTINATION);
        Boolean folderMaker = folderCheck.mkdir();
        // the socket factory lets it work over ethernet, IDK how, but it does.
        m_socketFactory = new OurSocketFactory();

        ssh = new JSch();
        GUI.MakeGUI();
    }

    public static boolean TransferFiles() {
        try {
            // The port should always be 22, because thats what SSH uses normally.
            // Its also what happens if we dont give it a port, so dont worry about it.
            Session session = ssh.getSession(USERNAME, ROBOT_IP, 22);
            // Whatever you do, DONT REMOVE THE BELOW LINE, it will keep it from connecting.
            session.setConfig("StrictHostKeyChecking", "no");
            session.setSocketFactory(m_socketFactory);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftp = (ChannelSftp) channel;
            Vector ls = sftp.ls(REMOTE_FILE_PATH);
            for (Object entry : ls) {
                ChannelSftp.LsEntry e = (ChannelSftp.LsEntry) entry;
                if (e.getFilename().endsWith(".wpilog")) {
                    String actualFileName = (REMOTE_FILE_PATH + e.getFilename());
                    GUI.writeToText("Downloading: " + e.getFilename());
                    GUI.writeToText("Real file name: " + actualFileName);
                    sftp.get(actualFileName, LOCAL_FILE_DESTINATION);
                    GUI.writeToText("Downloaded file.\nDeleting: " + e.getFilename());
                    sftp.rm(actualFileName);
                    GUI.writeToText("File deleted.");
                }
            }

            System.out.println("All files downloaded, closing connections and ending programs");
            channel.disconnect();
            session.disconnect();
            return false;
        } catch (SftpException | JSchException uhoh) {
            System.out.println("the thing broke somewhere, so start fixing it, or make somebody else fix it.");
            StringWriter sw = new StringWriter();
            uhoh.printStackTrace(new PrintWriter(sw));
            GUI.writeToText("Could not transfer files, read the stack trace below\n" + sw.toString());
            try {
                sw.close();
            } catch (IOException e) {
                System.out.println("String writed didnt close properly, its probably gonna be fine though.");
            }
            uhoh.printStackTrace();
            return true;
        }
    }
}
package pitscript;

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

public class FileTransfer implements Runnable {
    private Vector<String> taskedFiles;
    private JSch ssh;
    private Session session;
    private String actualFileName;

    public FileTransfer(Vector<String> completeFilename, JSch newSSH) {
        taskedFiles = completeFilename;
        completeFilename = null;
        ssh = newSSH;
    }

    @Override
    public void run() {
        try {

            try {
                session = ssh.getSession(Constants.USERNAME, Constants.ROBOT_IP, 22);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();
            } catch (JSchException stopComplaining) {
                session = ssh.getSession(Constants.USERNAME, Constants.ROBOT_IP, 22);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();
            }
            Channel channel = session.openChannel("sftp");
            try {
                channel.connect();
            } catch (Exception notOnMyWatch) {
                channel.connect();
            }
            ChannelSftp sftp = (ChannelSftp) channel;
            GUI.neaterSetText(((Long)(Thread.currentThread().getId()%Constants.THREAD_POOL_THREAD_COUNT)).intValue(),"Downloading: " + taskedFiles.toString());
            for (Object entry : taskedFiles) {
                String e = (String) entry;
                actualFileName = Constants.REMOTE_FILE_PATH + e;
                sftp.get(actualFileName, Constants.LOCAL_FILE_DESTINATION);
                try {
                    sftp.rm(actualFileName);
                } catch (SftpException uhoh) {
                    StringWriter sw = new StringWriter();
                    uhoh.printStackTrace(new PrintWriter(sw));
                    GUI.writeToText("Could not transfer files, read the stack trace below\n" + sw.toString());
                    try {
                        sw.close();
                    } catch (IOException e1) {
                    }
                }
            }
            channel.disconnect();
            session.disconnect();
        } catch (SftpException | JSchException uhoh) {
            StringWriter sw = new StringWriter();
            uhoh.printStackTrace(new PrintWriter(sw));
            GUI.writeToText("Could not transfer files, read the stack trace below\n" + sw.toString());
            try {
                sw.close();
            } catch (IOException e) {
            }
            uhoh.printStackTrace();
        }
    }
}

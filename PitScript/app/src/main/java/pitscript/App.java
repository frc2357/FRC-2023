package pitscript;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class App {

    public static OurSocketFactory m_socketFactory;
    public static JSch ssh;

    public Vector<String> filesToTask;

    public static void main(String[] args) {

        File folderCheck = new File(Constants.LOCAL_FILE_DESTINATION);
        Boolean folderMaker = folderCheck.mkdir();
        // the socket factory lets it work over ethernet, IDK how, but it does.
        m_socketFactory = new OurSocketFactory();
        ssh = new JSch();
        GUI.MakeGUI();
    }

    public static Vector<String> getFileList() {
        try {
            // The port should always be 22, because thats what SSH uses normally.
            // Its also what happens if we dont give it a port, so dont worry about it.
            Session session = ssh.getSession(Constants.USERNAME, Constants.ROBOT_IP, 22);
            // Whatever you do, DONT REMOVE THE BELOW LINE, it will keep it from connecting.
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftp = (ChannelSftp) channel;
            Vector<String> ls = (Vector<String>) sftp.ls(Constants.REMOTE_FILE_PATH);
            // closes the connection the made connection once it gets what it needs
            session.disconnect();
            channel.disconnect();
            return ls;
        } catch (SftpException | JSchException e) {
            GUI.writeToText("could not get file list. check connection then redo the thing.");
            e.printStackTrace();
        }
        return null;
    }

    

    public static boolean allTasksDone(List<Future<?>> futures) {
        boolean allDone = true;
        for (Future<?> future : futures) {
            allDone &= future.isDone();
        }
        return allDone;
    }

    /**
     * Deltes all the files on the device. Not multi-threading safe.
     * 
     * @return Whether it worked or not
     */
    public static boolean deleteAllFiles() {
        try {
            // The port should always be 22, because thats what SSH uses normally.
            // Its also what happens if we dont give it a port, so dont worry about it.
            Session session = ssh.getSession(Constants.USERNAME, Constants.ROBOT_IP, 22);
            // Whatever you do, DONT REMOVE THE BELOW LINE, it will keep it from connecting.
            session.setConfig("StrictHostKeyChecking", "no");
            session.setSocketFactory(m_socketFactory);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftp = (ChannelSftp) channel;
            Vector<String> ls = sftp.ls(Constants.REMOTE_FILE_PATH);
            for (Object entry : ls) {
                ChannelSftp.LsEntry e = (ChannelSftp.LsEntry) entry;
                if (e.getFilename().endsWith(".wpilog")) {
                    String actualFileName = (Constants.REMOTE_FILE_PATH + e.getFilename());
                    GUI.writeToText("Deleting: " + e.getFilename());
                    try {
                        sftp.rm(actualFileName);
                    } catch (SftpException uhoh) {
                        GUI.writeToText("File Delete failed");
                        StringWriter sw = new StringWriter();
                        uhoh.printStackTrace(new PrintWriter(sw));
                        GUI.writeToText("Could not transfer files, read the stack trace below\n" + sw.toString());
                        try {
                            sw.close();
                        } catch (IOException e1) {}
                    }
                }
            }
            channel.disconnect();
            session.disconnect();
            return true;
        } catch (SftpException | JSchException uhoh) {
            System.out.println("the thing broke somewhere, so start fixing it, or make somebody else fix it.");
            StringWriter sw = new StringWriter();
            uhoh.printStackTrace(new PrintWriter(sw));
            GUI.writeToText("Could not transfer files, read the stack trace below\n" + sw.toString());
            try {
                sw.close();
            } catch (IOException e) {}
            uhoh.printStackTrace();
        }
        return false;
    }

    public Boolean MultiThreadTransferFiles() {
        try{
        Session session;
            session = ssh.getSession(Constants.USERNAME, Constants.ROBOT_IP, 22);
            // Whatever you do, DONT REMOVE THE BELOW LINE, it will keep it from connecting.
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
        List<Future<?>> futures = new ArrayList<Future<?>>();
        filesToTask = new Vector<>();
        ExecutorService pool;
        Vector<String> fileList = getFileList();
        pool = Executors.newFixedThreadPool(Constants.THREAD_POOL_THREAD_COUNT);
        for (Object entry : fileList) {
            ChannelSftp.LsEntry e = (ChannelSftp.LsEntry) entry;
            if (e.getFilename().endsWith(".wpilog")) {
                filesToTask.add(e.getFilename());
                if (filesToTask.size() == Constants.FILE_GROUP_SIZE) {
                        Future<?> f = pool.submit(new FileTransfer(filesToTask, ssh));
                    futures.add(f);
                    filesToTask = new Vector<String>();
                }
            }
        }
        // waits until all tasks are completed, then makes sure the pool is shutdown
        while (!allTasksDone(futures)) {
        }
        if(getFileList().size()>2){
            System.out.println(getFileList().toString());
            pool.shutdownNow();
            MultiThreadTransferFiles();
        }
        if (allTasksDone(futures)) {
            pool.shutdownNow();
            return true;
        }
        pool.shutdownNow();
        return false;
    }catch(NullPointerException | JSchException uhoh ){return false;}
    }
}
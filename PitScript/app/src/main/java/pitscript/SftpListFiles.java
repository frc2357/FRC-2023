package pitscript;
import java.io.*;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
  
import java.io.IOException;
import java.util.List;
  
public class SftpListFiles {
    SftpListFiles instance = null;
    public SftpListFiles getInstance(){
        instance = this;
        return instance;
    }
    public String getFileName(String hostName, String remoteDir)throws IOException {
        // create a instance of SSHClient
        SSHClient client = new SSHClient();
  
        // add host key verifier
        client.addHostKeyVerifier(new PromiscuousVerifier());
  
        // connect to the sftp server
        client.connect(hostName);
  
        // authenticate by username and password.
        client.authPassword("test_user", "123");
  
        // get new sftpClient.
        SFTPClient sftpClient = client.newSFTPClient();
  
        // Give the path to the directory from which
        // you want to get a list of all the files.
        List<RemoteResourceInfo> resourceInfoList = sftpClient.ls(remoteDir);
        String fileNameList = resourceInfoList.toString();
        return fileNameList;
    }
}
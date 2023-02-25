package org.apache.commons.net.ftp;
import org.apache.commons.net.ftp.FTPClient;
public class App {
    public  FTPClient ftp = new FTPClient();
    public  void main() {
        ftp.connect();
    }
}

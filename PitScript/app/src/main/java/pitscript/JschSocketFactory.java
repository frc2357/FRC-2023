package pitscript;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.jcraft.jsch.SocketFactory;

public class JschSocketFactory implements SocketFactory {
  JschSocketFactory instance = null;
  public JschSocketFactory getInstance(){
    instance = this;
    return instance;
  }
    public Socket createSocket(String host, int port) throws IOException,
                                                             UnknownHostException
    {
      Socket socket=new Socket();
      int timeout = 60000;
      socket.connect(new InetSocketAddress(host, port), timeout);
      return socket;
    }
  
    public InputStream getInputStream(Socket socket) throws IOException
    {
      return socket.getInputStream();
    }
  
    public OutputStream getOutputStream(Socket socket) throws IOException
    {
      return socket.getOutputStream();
    }
  }
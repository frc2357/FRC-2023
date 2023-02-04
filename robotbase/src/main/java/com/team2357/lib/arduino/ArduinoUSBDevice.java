package com.team2357.lib.arduino;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fazecast.jSerialComm.SerialPort;

public class ArduinoUSBDevice {
    private static class SerialPortHandler implements Runnable{
        private static final int READ_TIMEOUT = 250;
        private static final int BYTE_BUFFER_SIZE = 1024;
        private static final int THREAD_SLEEP_MS = 500;

        private Thread m_thread;
        private SerialPort m_serialPort;
        private String m_fileName;
        private byte[] m_byteBuffer = new byte[BYTE_BUFFER_SIZE];
        private StringBuffer m_StringBuffer = new StringBuffer();

        public SerialPortHandler(String fileName){
            this.m_fileName=fileName;
            try {
                    m_serialPort = SerialPort.getCommPort(fileName);
                    m_serialPort.setComPortParameters(
                    115200,
                    8,
                    SerialPort.ONE_STOP_BIT,
                    SerialPort.NO_PARITY
                    );
                  m_serialPort.setComPortTimeouts(
                        SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                        READ_TIMEOUT,
                        0                      
                        );
                } catch (Exception e) {
                    m_serialPort = null;
                    System.err.println(e.getMessage());                   
                }
        }
        public void start(){
            if(m_thread!=null){
                stop();
            }

            System.out.println("Opening serial port '"+m_serialPort.getSystemPortName()+"'");
            boolean success = m_serialPort.openPort();

            if(!success) {
                System.err.println("Error opening serial port '"+m_serialPort.getSystemPortName()+"'");
                return;
            }

            String threadName = "ArduinoUSB[" +m_serialPort.getSystemPortName()+"]";
            m_thread = new Thread(this,threadName);
            m_thread.start();

        }
        public void stop(){
            m_thread=null;
        }
        @Override
        public void run(){
            while(m_thread!=null){
                try{
                    read();
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }
        private void read() throws IOException{
            int byteCount = m_serialPort.readBytes(m_byteBuffer,BYTE_BUFFER_SIZE);
            System.out.println(byteCount);
            if(byteCount==0){
                return;
            }
            String newChars = new String(m_byteBuffer,0,byteCount);
            if(newChars!=null){
                m_StringBuffer.append(newChars);
                int lineBreakIndex = m_StringBuffer.indexOf("\n");
                if(lineBreakIndex>=0){
                    String line = m_StringBuffer.substring(0,lineBreakIndex);
                    System.out.println("line: '"+line+"'");
                    m_StringBuffer.delete(0, lineBreakIndex+1);

                }
            }
        }
        private void write(String msg){
            System.out.println("write '"+msg+"'");
            byte[] bytes = msg.getBytes();
            int bytesWritten = m_serialPort.writeBytes(bytes, bytes.length);
            if (bytesWritten==-1){
                System.err.println("Failed to write bytes");
            }else if(bytesWritten!=bytes.length){
                System.err.println("Incomplete write ("+bytesWritten+" of "+bytes.length+" total bytes)");
            }
        }
        private void sendKeepAlive(){
            write("");
        }
    };
    private static final String DEV_DIR = "/dev";
    private static final String USB_PREFIX = "ttyACM";
    public static void enumerateUSB(){
        Set<String> usbs = listUSBDevices();
        for(String str:usbs){
            System.out.println(str);
            SerialPortHandler sph = new SerialPortHandler(DEV_DIR+"/"+str);
            sph.start();
        }
    }
    private static Set<String> listUSBDevices(){
        return Stream.of(new File(DEV_DIR).listFiles())
        .filter(file -> !file.isDirectory())
        .filter(file -> file.getName().startsWith(USB_PREFIX))
        .map(File::getName)
        .collect(Collectors.toSet());
    }
    /*Static enumeration code to map device names to /dev/tty files
    Instanciate an instance of a class with a device name in constructor
    Log error every 10s that its not connected
    Automaticlly reconnect and handle disconnections without crashing
     */
}

package com.team2357.log;

import com.team2357.log.LogSession;
import com.team2357.log.outputs.LogOutput;
import com.team2357.log.outputs.ZipFileOutput;
import com.team2357.log.util.SineWave;
import java.util.HashMap;
import java.util.Map;

/**
 * RobotLog: Test Session
 *
 * This is a command-line test session utility for RobotLog
 *
 * To use: `./gradlew robotlogtestsession --args='<arguments>'`
 *
 * Valid arguments:
 *
 */
public class RobotLogTestSession {

  LogSession m_session;

  public RobotLogTestSession() {
    displayWelcome();
    installShutdownHook();

    Map<String, Object> header = new HashMap<String, Object>();
    header.put("eventName", "The Main Event");
    header.put("matchNumber", 22);
    header.put("replayNumber", 1);
    header.put("alliance", "blue");
    header.put("driverStationLocation", 3);
    header.put("gameSpecificMessage", "RLR");

    new SineWave("Sine Wave", 0.25D, 5.0D, 5.0D, 0.25D);

    Map<String, LogOutput> outputs = new HashMap<String, LogOutput>();
    outputs.put("file", new ZipFileOutput("./logs", "testlog", header, 0.01D));

    m_session = new LogSession(outputs);
    m_session.subscribeTopic("Sine Wave", "file");
  }

  private void displayWelcome() {
    System.out.println(" _____     _       _   __            ");
    System.out.println("| __  |___| |_ ___| |_|  |   ___ ___ ");
    System.out.println("|    -| . | . | . |  _|  |__| . | . |");
    System.out.println("|__|__|___|___|___|_| |_____|___|_  |");
    System.out.println("   With <3 from Team FRC2357    |___|");
    System.out.println();
    System.out.println("-----------------------------");
    System.out.println("--- RobotLog Test Session ---");
    System.out.println("-----------------------------");
    System.out.flush();
  }

  private void installShutdownHook() {
    Runtime
      .getRuntime()
      .addShutdownHook(
        new Thread() {
          @Override
          public void run() {
            if (m_session != null) {
              m_session.stop();
            }
          }
        }
      );
  }

  public static void main(String[] args) {
    new RobotLogTestSession();
  }
}

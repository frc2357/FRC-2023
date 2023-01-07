package com.team2357.log.outputs;

import java.util.Map;

public class ZipFileOutput extends ThreadLogOutput {

  private final String m_dirPath;
  private final String m_fileName;
  private final Map<String, Object> m_header;
  private final double m_timeSecondsRoundingFactor;

  public ZipFileOutput(
    final String dirPath,
    final String fileName,
    final Map<String, Object> header,
    final double timeSecondsRoundingFactor
  ) {
    super();
    m_dirPath = dirPath;
    m_fileName = fileName;
    m_header = header;
    m_timeSecondsRoundingFactor = timeSecondsRoundingFactor;
  }

  @Override
  public void run() {
    setLogWriter(
      new ZipFileLogWriter(
        m_dirPath,
        m_fileName,
        m_header,
        m_timeSecondsRoundingFactor
      )
    );

    try {
      super.run();
    } catch (Exception e) {
      System.err.println("Exception caught:" + e);
    }
  }
}

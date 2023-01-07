package com.team2357.log.outputs;

import com.team2357.log.lib.Utils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileUtils {

  public static final String ZIP_EXTENSION = ".zip";

  public static ZipOutputStream initZipFile(
    String dirPath,
    String fileName,
    int compressionLevel,
    String internalFileName
  ) throws IOException, FileNotFoundException {
    ZipOutputStream zipOut = createZipOutputStream(dirPath, fileName);
    zipOut.setLevel(compressionLevel);
    zipOut.putNextEntry(new ZipEntry(internalFileName));
    return zipOut;
  }

  public static void completeZipFile(ZipOutputStream zipOut)
    throws IOException {
    zipOut.closeEntry();
    zipOut.close();
  }

  public static ZipOutputStream createZipOutputStream(
    String dirPath,
    String fileName
  ) throws FileNotFoundException {
    File dir = getOrCreateDir(dirPath);
    File zipFile = getFile(dir, fileName);

    System.out.println("ZipFileOutput: Creating zip file '" + zipFile + "'");
    FileOutputStream fileOut = new FileOutputStream(zipFile);
    BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut);
    return new ZipOutputStream(bufferedOut);
  }

  public static File getFile(File dir, String fileName) {
    return getFile(dir, fileName, 0);
  }

  public static File getFile(File dir, String fileName, int count) {
    String fullFileName =
      fileName + (count > 0 ? "." + count : "") + ZIP_EXTENSION;

    File file = new File(dir, fullFileName);

    if (file.exists()) {
      return getFile(dir, fileName, count + 1);
    }
    return file;
  }

  public static File getOrCreateDir(String path) {
    File dir = new File(path);

    if (!dir.exists()) {
      String parentPath = dir.getParent();

      if (parentPath == null) {
        System.err.println(
          "ZipFileOutput: Cannot create dir (" +
          path +
          ") for unknown parent dir"
        );
        return null;
      }

      File parentDir = getOrCreateDir(parentPath);
      if (parentDir == null) {
        // No need to log to stderr here, the parent recursive call should have already logged.
        return null;
      }

      System.out.println("ZipFileOutput: Creating directory '" + path + "'");
      if (!dir.mkdir()) {
        System.err.println(
          "ZipFileOutput: Failed to create parent directory: " + parentPath
        );
        return null;
      }
    }

    if (!dir.isDirectory()) {
      System.err.println("ZipFileOutput: Path is not directory: " + path);
      return null;
    }
    if (!dir.canWrite() || !dir.canExecute()) {
      System.err.println(
        "ZipFileOutput: Insufficient privileges for directory: " + path
      );
      return null;
    }

    // Everything checks out.
    return dir;
  }

  public static String printHeader(Map<String, Object> header) {
    StringBuffer sb = new StringBuffer();
    boolean needsComma = false;

    sb.append("\"header\": { ");

    for (Map.Entry<String, Object> entry : header.entrySet()) {
      String keyStr = "\"" + entry.getKey() + "\"";
      String valueStr = printValue(
        entry.getValue(),
        entry.getValue().getClass()
      );

      if (needsComma) {
        sb.append(", ");
      }
      sb.append(keyStr + ": " + valueStr);
      needsComma = true;
    }

    sb.append(" }");
    return sb.toString();
  }

  public static String printTopic(String topicName, Class<?> valueType) {
    String topicStr = "\"" + topicName + "\"";
    String valueTypeStr = "\"" + printValueType(valueType) + "\"";

    return "{ \"name\":" + topicStr + ", \"type\":" + valueTypeStr + " }";
  }

  public static String printEntry(
    String topicName,
    Object value,
    Class<?> valueType,
    double relativeNanos,
    double timeRoundingFactor
  ) {
    String topicStr = "\"" + topicName + "\"";
    String valueStr = printValue(value, valueType);
    double timeSeconds = Utils.roundByFactor(
      ((double) relativeNanos) / Utils.NANO,
      timeRoundingFactor
    );
    String timeStr = Double.toString(timeSeconds);

    return (
      "{ \"topic\":" +
      topicStr +
      ", \"value\":" +
      valueStr +
      ", \"time\":" +
      timeStr +
      " }"
    );
  }

  public static String printValueType(Class<?> valueType) {
    if (valueType == String.class) {
      return "string";
    }
    if (valueType == Double.class) {
      return "decimal";
    }
    if (valueType == Integer.class) {
      return "integer";
    }
    if (valueType == Boolean.class) {
      return "boolean";
    }
    System.err.println(
      "ZipFileUtils.parseValueType: Unrecognized value type: " +
      valueType.getName()
    );
    return "";
  }

  public static String printValue(Object value, Class<?> valueType) {
    if (valueType == String.class) {
      // Escape double quotes
      return "\"" + String.valueOf(value).replaceAll("\"", "\\\"") + "\"";
    }
    if (valueType == Double.class) {
      return Double.toString((double) value);
    }
    if (valueType == Integer.class) {
      return Double.toString((int) value);
    }
    if (valueType == Boolean.class) {
      return Boolean.toString((boolean) value);
    }
    System.err.println(
      "ZipFileUtils.parseValue: Unrecognized value type: " + valueType.getName()
    );
    return "";
  }
}

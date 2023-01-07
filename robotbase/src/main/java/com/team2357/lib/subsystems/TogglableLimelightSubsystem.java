package com.team2357.lib.subsystems;

import com.team2357.lib.commands.InvertDriveCommand; //Imported for javadoc
import com.team2357.lib.subsystems.LimelightSubsystem;
import edu.wpi.first.networktables.NetworkTableEntry;

/**
 * The subsystem for the limelight. This version is toggleable, so that when the robot switches sides,
 * (Because of the {@link InvertDriveCommand}) the camera stream is able to switch cameras. It is also
 * for switching to different views (vision target tracking, Human view, etc.).
 *
 * @category Camera
 * @category Subsystem
 */
public class TogglableLimelightSubsystem extends LimelightSubsystem {

  public enum PipelineIndex {
    UNKNOWN(-1),
    VISION_TARGET(0),
    HUMAN_VIEW(1);

    public final int index;

    private PipelineIndex(int index) {
      this.index = index;
    }

    /**
     * @param index The index of the pipeline you want returned.
     *
     * @return The value of the pipeline with the index you passed in. Will return -1 (AKA UNKNOWN) if not
     *         found.
     */
    public static PipelineIndex getPipelineByIndex(int index) {
      for (PipelineIndex i : PipelineIndex.values()) {
        if (i.index == index) {
          return i;
        }
      }
      return UNKNOWN;
    }
  }

  private NetworkTableEntry m_stream = super.m_Table.getEntry("stream");
  private NetworkTableEntry m_pipeline = super.m_Table.getEntry("pipeline");

  /**
   * Sets the camera stream.
   *
   * @param isLimelightPrimary True if the limelight is primary, false if not.
   */
  public TogglableLimelightSubsystem(boolean isLimelightPrimary) {
    setStream(isLimelightPrimary);
    setPipeline(PipelineIndex.HUMAN_VIEW);
  }

  public void setPipeline(PipelineIndex p) {
    m_pipeline.setDouble(p.index);
  }

  public void setStream(boolean isLimelightPrimary) {
    m_stream.setValue(isLimelightPrimary ? 1 : 2);
  }

  public void toggleStream() {
    setStream(m_stream.getDouble(1.0) != 1.0);
  }
}

package com.team2357.lib.subsystems;

import com.team2357.lib.commands.InvertDriveCommand; //Imported for javadoc

import edu.wpi.first.networktables.DoubleSubscriber;

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

  private DoubleSubscriber m_stream = super.m_table.getDoubleTopic("stream").subscribe(1.0);

  /**
   * Sets the camera stream.
   *
   * @param isLimelightPrimary True if the limelight is primary, false if not.
   */
  public TogglableLimelightSubsystem(boolean isLimelightPrimary) {
    super("limelight");
    setStream(isLimelightPrimary);
    setPipeline(PipelineIndex.HUMAN_VIEW);
  }

  public void setPipeline(PipelineIndex p) {
    super.setPipeline(p.index);
  }

  public void toggleStream() {
    setStream(m_stream.get() != 1.0);
  }
}

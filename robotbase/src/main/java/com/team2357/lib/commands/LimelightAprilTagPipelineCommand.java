package com.team2357.lib.commands;

import com.team2357.lib.subsystems.LimelightSubsystem;

/**
 * Changes the camera pipeline (Which camera stream is being shown).
 *
 * @category Camera
 */
public class LimelightAprilTagPipelineCommand extends CommandLoggerBase {

  private boolean m_wait;

  /**
   * Create human pipeline command
   * @param wait True if the command should wait until the pipeline is active to finish, false if no wait required.
   */
  public LimelightAprilTagPipelineCommand(boolean wait) {
    m_wait = wait;
  }

  @Override
  public void initialize() {
    LimelightSubsystem.getInstance().setAprilTagPipelineActive();
  }

  @Override
  public boolean isFinished() {
    if (m_wait) {
      return LimelightSubsystem.getInstance().isAprilTagPipelineActive();
    }
    return true;
  }
}

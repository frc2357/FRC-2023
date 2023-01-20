package com.team2357.lib.commands;

import com.team2357.lib.subsystems.LimelightSubsystem;
/**
 * Changes the camera pipeline (Which camera stream is being shown).
 *
 * @category Camera
 */
public class LimelightTargetingPipelineCommand extends CommandLoggerBase {

  private boolean m_wait;

  /**
   * Create targeting pipeline command
   * @param wait True if the command should wait until the pipeline is active to finish, false if no wait required.
   */
  public LimelightTargetingPipelineCommand(boolean wait) {
    m_wait = wait;
  }

  @Override
  public void initialize() {
    LimelightSubsystem.getInstance().setTargetingPipelineActive();
  }

  @Override
  public boolean isFinished() {
    if (m_wait) {
      return LimelightSubsystem.getInstance().isTargetingPipelineActive();
    }
    return true;
  }
}

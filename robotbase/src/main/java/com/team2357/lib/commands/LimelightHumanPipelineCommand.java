package com.team2357.lib.commands;

import com.team2357.lib.subsystems.LimelightSubsystem;

/**
 * Changes the camera pipeline (Which camera stream is being shown).
 *
 * @category Camera
 */
public class LimelightHumanPipelineCommand extends CommandLoggerBase {

  private boolean m_wait;

  /**
   * Create human pipeline command
   * @param wait True if the command should wait until the pipeline is active to finish, false if no wait required.
   */
  public LimelightHumanPipelineCommand(boolean wait) {
    m_wait = wait;
  }

  @Override
  public void initialize() {
    LimelightSubsystem.getInstance().setHumanPipelineActive();
  }

  @Override
  public boolean isFinished() {
    if (m_wait) {
      return LimelightSubsystem.getInstance().isHumanPipelineActive();
    }
    return true;
  }
}

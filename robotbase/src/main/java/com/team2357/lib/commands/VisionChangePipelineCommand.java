package com.team2357.lib.commands;

import com.team2357.lib.subsystems.TogglableLimelightSubsystem;
import com.team2357.lib.subsystems.TogglableLimelightSubsystem.PipelineIndex;

/**
 * Changes the camera pipeline (Which camera stream is being shown).
 *
 * @category Camera
 */
public class VisionChangePipelineCommand extends CommandLoggerBase {

  private TogglableLimelightSubsystem m_visionSub;

  /**
   * @param visionSub The {@link TogglableLimelightSubsystem}.
   */
  public VisionChangePipelineCommand(TogglableLimelightSubsystem visionSub) {
    m_visionSub = visionSub;
  }

  @Override
  public void initialize() {
    m_visionSub.setPipeline(PipelineIndex.VISION_TARGET);
  }

  @Override
  public void end(boolean interrupted) {
    super.end(interrupted);
    m_visionSub.setPipeline(PipelineIndex.HUMAN_VIEW);
  }
}

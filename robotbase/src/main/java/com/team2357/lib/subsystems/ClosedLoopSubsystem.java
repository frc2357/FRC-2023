package com.team2357.lib.subsystems;

import com.team2357.lib.util.ClosedLoopSystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * This class is basically {@link SubsystemBase} but with a getter and setter for a closed loop variable.
 * Also implements {@link ClosedLoopSubsystem} so you don't need to implement it in your subsystem.
 */
public abstract class ClosedLoopSubsystem
  extends SubsystemBase
  implements ClosedLoopSystem {

  protected boolean m_closedLoopEnabled = true;

  /**
   * Setter for m_closedLoopEnabled
   *
   * @param closedLoopEnabled boolean value to set this class' closed loop variable.
   */
  public void setClosedLoopEnabled(boolean closedLoopEnabled) {
    m_closedLoopEnabled = closedLoopEnabled;
  }

  /**
   * Getter for m_closedLoopEnabled
   *
   * @return boolean m_closedLoopEnabled. True if closed loop is enabled, false if not.
   * If closedLoop is false (the loop is open) then failsafe has activated/is active.
   */
  public boolean isClosedLoopEnabled() {
    return m_closedLoopEnabled;
  }
}
//Proudly made by GameMagma, 2020

package com.team2357.lib.subsystems;

import edu.wpi.first.wpilibj.PowerDistribution;

public class PDHSubsystem extends PowerDistribution {

  public static PDHSubsystem instance = null;

  public static PDHSubsystem getInstance() {
    return instance;
  }

  public PDHSubsystem(int canId) {
    super(canId, ModuleType.kRev);
    instance = this;
  }
}

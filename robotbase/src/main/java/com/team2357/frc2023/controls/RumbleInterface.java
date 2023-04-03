package com.team2357.frc2023.controls;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public interface RumbleInterface {
    public void setRumble(RumbleType type, double intensity);
}

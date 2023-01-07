package com.team2357.lib.controllers;

/**
 * Informs commands on user inputs for arcade style drive
 */
public interface ArcadeAxisInput {
  /**
   * Get the desired speed factor.
   * @return A value from -1.0 (full reverse) to 1.0 (full forward)
   */
  public double getSpeed();

  /**
   * Get the desired turn factor.
   * @return A value from -1.0 (full counter-clockwise) to 1.0 (full clockwise)
   */
  public double getTurn();
}

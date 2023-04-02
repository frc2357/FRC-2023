package com.team2357.frc2023.state;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class RobotState {
    private static final RobotState s_instance = new RobotState();

    public static enum State {
        ROBOT_INIT,                    // Robot is initializing
        ROBOT_DISABLED,                // Robot is in disabled mode and no alliance is selected
        ROBOT_STOWED_EMPTY,            // Robot is in teleop and has no game piece
        ROBOT_STOWED_CONE,             // Robot currently has a cone and is in stowed/transit pose
        ROBOT_STOWED_CUBE,             // Robot currently has a cube and is in stowed/transit pose
        ROBOT_PRE_INTAKING_CONE,       // Robot is signaling to the human player we want a cone
        ROBOT_PRE_INTAKING_CUBE,       // Robot is signaling to the human player we want a cube
        ROBOT_INTAKING_CONE,           // Robot is currently trying to collect a cone
        ROBOT_INTAKING_CUBE,           // Robot is currently trying to collect a cube
        ROBOT_PRE_SCORE_CONE_HIGH,     // Robot is in the "pre-score" pose to score a cone in a high node
        ROBOT_PRE_SCORE_CUBE_HIGH,     // Robot is in the "pre-score" pose to score a cube in a high node
        ROBOT_PRE_SCORE_CONE_MID,      // Robot is in the "pre-score" pose to score a cone in a mid node
        ROBOT_PRE_SCORE_CUBE_MID,      // Robot is in the "pre-score" pose to score a cube in a mid node
        ROBOT_PRE_SCORE_CONE_LOW,      // Robot is in the "pre-score" pose to score a cone in a low node
        ROBOT_PRE_SCORE_CUBE_LOW,      // Robot is in the "pre-score" pose to score a cube in a low node
    };

    public static enum DriveControlState {
        FIELD_RELATIVE, // Manual control of the robot is field relative
        ROBOT_CENTRIC // Manual control of the robot is robot centric
    }

    public static Alliance getAlliance() {
        return s_instance.m_alliance;
    }

    public static State getState() {
        return s_instance.m_currentState;
    }

    public static boolean isZeroed() {
        return s_instance.m_zeroed;
    }

    public static void setRobotZeroed(boolean zeroed) {
        s_instance.setZeroed(zeroed);
    }

    public static boolean hasCone() {
        State state = s_instance.m_currentState;
        return state == State.ROBOT_STOWED_CONE ||
          state == State.ROBOT_PRE_SCORE_CONE_HIGH ||
          state == State.ROBOT_PRE_SCORE_CONE_MID ||
          state == State.ROBOT_PRE_SCORE_CUBE_LOW;
    }

    public static boolean hasCube() {
        State state = s_instance.m_currentState;
        return state == State.ROBOT_STOWED_CUBE ||
          state == State.ROBOT_PRE_SCORE_CUBE_HIGH ||
          state == State.ROBOT_PRE_SCORE_CUBE_MID ||
          state == State.ROBOT_PRE_SCORE_CUBE_LOW;
    }

    public static void onDriverAllianceSelect(Alliance alliance) {
        s_instance.setAlliance(alliance);
    }

    public static void robotInit() {
        setState(State.ROBOT_INIT);
    }

    public static void disabledInit() {
        setState(State.ROBOT_DISABLED);
    }

    public static void autonomousInit() {
        if (s_instance.m_alliance == Alliance.Invalid) {
            Logger.getInstance().recordOutput("Driver Set Alliance", "not set before auto");
            s_instance.setAlliance(DriverStation.getAlliance());
        }
        setState(State.ROBOT_STOWED_EMPTY);
    }

    public static void setState(State newState) {
        s_instance.setCurrentState(newState);
    }

    public static boolean isFieldRelative() {
        return s_instance.m_currentDriveControlState == DriveControlState.FIELD_RELATIVE;
    }

    public static DriveControlState getDriveControlState() {
        return s_instance.m_currentDriveControlState;
    }

    public static void setDriveControlState(DriveControlState driveControlState) {
        s_instance.setCurrentDriveControlState(driveControlState);
    }

    private Alliance m_alliance;
    private State m_currentState;
    private DriveControlState m_currentDriveControlState;
    private boolean m_zeroed;

    private RobotState() {
        m_alliance = Alliance.Invalid;
        m_currentState = State.ROBOT_INIT;
        m_currentDriveControlState = DriveControlState.FIELD_RELATIVE;
        m_zeroed = false;
    }

    private void setAlliance(Alliance alliance) {
        Logger.getInstance().recordOutput("Driver Set Alliance", alliance.name());

        m_alliance = alliance;
        LEDState.getInstance().updateLEDs(m_currentState, m_alliance);
    }

    private void setZeroed(boolean zeroed) {
        m_zeroed = zeroed;
    }

    private void setCurrentState(State newState) {
        Logger.getInstance().recordOutput("Robot State", newState.name());
        m_currentState = newState;
        LEDState.getInstance().updateLEDs(m_currentState, m_alliance);
    }

    private void setCurrentDriveControlState(DriveControlState driveControlState) {
        Logger.getInstance().recordOutput("Robot Drive Control State", driveControlState.name());
        m_currentDriveControlState = driveControlState;
    }
}

package com.team2357.frc2023.state;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class RobotState {
    private static final RobotState s_instance = new RobotState();

    public static enum State {
        ROBOT_INIT,                    // Robot is initializing
        ROBOT_DISABLED,                // Robot is in disabled mode and no alliance is selected
        ROBOT_AUTONOMOUS,              // Robot is currently running its autonomous command
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

    public static enum GamePiece {
        NONE,
        CONE,
        CUBE
    };

    public static Alliance getAlliance() {
        return s_instance.m_alliance;
    }

    public static State getState() {
        return s_instance.m_currentState;
    }

    public static boolean hasCone() {
        return s_instance.m_gamePiece == GamePiece.CONE;
    }

    public static boolean hasCube() {
        return s_instance.m_gamePiece == GamePiece.CUBE;
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
        setState(State.ROBOT_AUTONOMOUS);
    }

    public static void setState(State newState) {
        s_instance.setCurrentState(newState);
    }

    public static void teleopInit() {
        if (hasCone()) {
            setState(State.ROBOT_STOWED_CONE);
        } else if (hasCube()) {
            setState(State.ROBOT_STOWED_CUBE);
        } else {
            setState(State.ROBOT_STOWED_EMPTY);
        }
    }

    private Alliance m_alliance;
    private State m_currentState;
    private GamePiece m_gamePiece;

    private RobotState() {
        m_alliance = Alliance.Invalid;
        m_currentState = State.ROBOT_INIT;
        m_gamePiece = GamePiece.NONE;
    }

    private void setAlliance(Alliance alliance) {
        Logger.getInstance().recordOutput("Driver Set Alliance", alliance.name());

        m_alliance = alliance;
        LEDState.getInstance().updateLEDs(m_currentState, m_alliance);
    }

    private void setCurrentState(State newState) {
        Logger.getInstance().recordOutput("Robot State", newState.name());

        switch (newState) {
            case ROBOT_STOWED_CONE:
                m_gamePiece = GamePiece.CONE;
                break;
            case ROBOT_STOWED_CUBE:
                m_gamePiece = GamePiece.CUBE;
                break;
            case ROBOT_STOWED_EMPTY:
                m_gamePiece = GamePiece.NONE;
                break;
            default:
                break;
        }

        m_currentState = newState;

        LEDState.getInstance().updateLEDs(m_currentState, m_alliance);
    }
}

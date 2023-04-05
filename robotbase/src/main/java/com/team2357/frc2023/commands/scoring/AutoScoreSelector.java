package com.team2357.frc2023.commands.scoring;

import java.util.Map;
import java.util.function.Supplier;

import org.littletonrobotics.junction.Logger;

import com.team2357.frc2023.commands.scoring.cone.ConeHighScoreCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeLowScoreCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeMidScoreCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeHighScoreCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeLowScoreCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeMidScoreCommand;
import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.state.RobotState.State;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SelectCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoScoreSelector {
    private enum ScoreCommand {
        CONE_LOW_SCORE,
        CONE_MID_SCORE,
        CONE_HIGH_SCORE,
        CUBE_LOW_SCORE,
        CUBE_MID_SCORE,
        CUBE_HIGH_SCORE,
        NONE
    }

    private final Supplier<Integer> m_rowSupplier;
    private final Supplier<Integer> m_colSupplier;
    
    public final SelectCommand m_selectCommand;

    public AutoScoreSelector(Supplier<Integer> rowSupplier, Supplier<Integer> colSupplier) {
        m_rowSupplier = rowSupplier;
        m_colSupplier = colSupplier;
        
        m_selectCommand = new SelectCommand (
            Map.ofEntries(
                Map.entry(ScoreCommand.CONE_LOW_SCORE, new ConeLowScoreCommand()),
                Map.entry(ScoreCommand.CONE_MID_SCORE, new ConeMidScoreCommand()),
                Map.entry(ScoreCommand.CONE_HIGH_SCORE, new ConeHighScoreCommand()),
                Map.entry(ScoreCommand.CUBE_LOW_SCORE, new CubeLowScoreCommand()),
                Map.entry(ScoreCommand.CUBE_MID_SCORE, new CubeMidScoreCommand()),
                Map.entry(ScoreCommand.CUBE_HIGH_SCORE, new CubeHighScoreCommand()),
                Map.entry(ScoreCommand.NONE, new WaitCommand(0))
            ), this::selectScoreCommand
        );      
    }

    public SelectCommand getSelectCommand() {
        return m_selectCommand;
    };
    
    private ScoreCommand selectScoreCommand() {
        int row = m_rowSupplier.get();
        int col = m_colSupplier.get();

        switch (row) {
            case 2:
                switch (RobotState.getState()) {
                    case ROBOT_PRE_SCORE_CONE_LOW:
                        return ScoreCommand.CONE_LOW_SCORE;
                    case ROBOT_PRE_SCORE_CUBE_LOW:
                        return ScoreCommand.CUBE_LOW_SCORE;
                    default:
                        break;
                }
            case 1:
                switch (col % 3) {
                    case 0:
                    case 2:
                        return verifyPreposeToScore(State.ROBOT_PRE_SCORE_CONE_MID, ScoreCommand.CONE_MID_SCORE);
                    case 1:
                        return verifyPreposeToScore(State.ROBOT_PRE_SCORE_CUBE_MID, ScoreCommand.CUBE_MID_SCORE);
                }
            case 0:
                switch (col % 3) {
                    case 0:
                    case 2:
                        return verifyPreposeToScore(State.ROBOT_PRE_SCORE_CONE_HIGH, ScoreCommand.CONE_HIGH_SCORE);
                    case 1:
                        return verifyPreposeToScore(State.ROBOT_PRE_SCORE_CUBE_HIGH, ScoreCommand.CUBE_HIGH_SCORE);
                }
        }

        String message = "No target selected";
        DriverStation.reportWarning(message, false);
        Logger.getInstance().recordOutput("Auto Score", message);

        return ScoreCommand.NONE;
    }

    public static ScoreCommand verifyPreposeToScore(State preposeState, ScoreCommand scoreCommand) {
        if (!RobotState.isInState(preposeState)) {
            DriverStation.reportWarning(
                    "Tried to score " + preposeState.name() + " from " + RobotState.getState().name(), false);
            return ScoreCommand.NONE;
        }
        return scoreCommand;
    }

}

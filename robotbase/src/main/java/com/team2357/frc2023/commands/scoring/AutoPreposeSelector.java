package com.team2357.frc2023.commands.scoring;

import java.util.Map;
import java.util.function.Supplier;

import org.littletonrobotics.junction.Logger;

import com.team2357.frc2023.commands.scoring.cone.ConeHighPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeLowPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cone.ConeMidPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeHighPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeLowPrePoseCommand;
import com.team2357.frc2023.commands.scoring.cube.CubeMidPrePoseCommand;
import com.team2357.frc2023.networktables.Buttonboard;
import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.state.RobotState.State;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SelectCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoPreposeSelector {

    private enum PreposeCommands {
        CONE_LOW_PREPOSE,
        CONE_MID_PREPOSE,
        CONE_HIGH_PREPOSE,
        CUBE_LOW_PREPOSE,
        CUBE_MID_PREPOSE,
        CUBE_HIGH_PREPOSE,
        NONE
    }

    private final Supplier<Integer> m_rowSupplier;
    private final Supplier<Integer> m_colSupplier;
    private final Supplier<Buttonboard.Gamepiece> m_gamepieceSupplier;
    
    public final SelectCommand m_selectCommand;

    public AutoPreposeSelector(Supplier<Integer> rowSupplier, Supplier<Integer> colSupplier, Supplier<Buttonboard.Gamepiece> gamepieceSupplier) {
        m_rowSupplier = rowSupplier;
        m_colSupplier = colSupplier;
        m_gamepieceSupplier = gamepieceSupplier;
        
        m_selectCommand = new SelectCommand (
            Map.ofEntries(
                Map.entry(PreposeCommands.CONE_LOW_PREPOSE, new ConeLowPrePoseCommand()),
                Map.entry(PreposeCommands.CONE_MID_PREPOSE, new ConeMidPrePoseCommand()),
                Map.entry(PreposeCommands.CONE_HIGH_PREPOSE, new ConeHighPrePoseCommand()),
                Map.entry(PreposeCommands.CUBE_LOW_PREPOSE, new CubeLowPrePoseCommand()),
                Map.entry(PreposeCommands.CUBE_MID_PREPOSE, new CubeMidPrePoseCommand()),
                Map.entry(PreposeCommands.CUBE_HIGH_PREPOSE, new CubeHighPrePoseCommand()),
                Map.entry(PreposeCommands.NONE, new WaitCommand(0))
            ), this::selectPreposeCommand
        );      
    }

    public SelectCommand getSelectCommand() {
        return m_selectCommand;
    };

    private PreposeCommands selectPreposeCommand() {

        Integer row = m_rowSupplier.get();
        Integer col = m_colSupplier.get();
        Buttonboard.Gamepiece gamepiece = m_gamepieceSupplier.get();

        switch (row) {
            case 2:
                switch (gamepiece) {
                    case CONE:
                        return verifyNotInPrepose(State.ROBOT_PRE_SCORE_CONE_LOW, PreposeCommands.CONE_LOW_PREPOSE);
                    case CUBE:
                        return verifyNotInPrepose(State.ROBOT_PRE_SCORE_CUBE_LOW, PreposeCommands.CUBE_LOW_PREPOSE);
                    default:
                        return PreposeCommands.NONE;
                }
            case 1:
                switch (col % 3) {
                    case 0:
                    case 2:
                        return verifyNotInPrepose(State.ROBOT_PRE_SCORE_CONE_MID, PreposeCommands.CONE_MID_PREPOSE);
                    case 1:
                        return verifyNotInPrepose(State.ROBOT_PRE_SCORE_CUBE_MID, PreposeCommands.CUBE_MID_PREPOSE);
                }
            case 0:
                switch (col % 3) {
                    case 0:
                    case 2:
                        return verifyNotInPrepose(State.ROBOT_PRE_SCORE_CONE_HIGH, PreposeCommands.CONE_HIGH_PREPOSE);
                    case 1:
                        return verifyNotInPrepose(State.ROBOT_PRE_SCORE_CUBE_HIGH, PreposeCommands.CUBE_HIGH_PREPOSE);
                }
        }

        String message = "No target selected";
        DriverStation.reportWarning(message, false);
        Logger.getInstance().recordOutput("Auto Prepose", message);

        return PreposeCommands.NONE;
    }

    public static PreposeCommands verifyNotInPrepose(State preposeState, PreposeCommands preposeCommand) {
        if (RobotState.isInState(preposeState)) {
            return preposeCommand;
        }
        return preposeCommand;
    }
} 
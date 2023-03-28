package com.team2357.frc2023.commands.state;

import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class SetRobotStateCommand extends CommandBase {
    private RobotState.State m_state;

    public SetRobotStateCommand(RobotState.State state) {
        m_state = state;
    }

    @Override
    public void initialize() {
        RobotState.setState(m_state);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}

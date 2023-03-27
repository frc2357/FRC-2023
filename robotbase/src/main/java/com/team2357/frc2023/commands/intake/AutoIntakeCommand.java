package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoIntakeCommand extends CommandBase{
    public Command m_deployGroup;
    public IntakeStowCommandGroup m_stowGroup;
    public SequentialCommandGroup m_spikedGroup;

    public boolean m_spiked = false;

    public AutoIntakeCommand(RobotState.GamePiece gamePiece) {
        if (gamePiece == RobotState.GamePiece.CUBE) {
            m_deployGroup = new IntakeDeployCubeCommandGroup();
        } else {
            m_deployGroup = new IntakeDeployConeCommandGroup();
        }
        m_stowGroup = new IntakeStowCommandGroup();

    }

    @Override
    public void execute() {
        if(IntakeRollerSubsystem.getInstance().getCurrent()>= Constants.INTAKE_ROLLER.AUTO_INTAKE_CURRENT_LIMIT){
            m_spiked=true;
        }
    }

    @Override
    public void initialize(){
        m_deployGroup.schedule();
    }

    @Override
    public boolean isFinished() {
        return m_spiked;
    }
    
    @Override
    public void end(boolean interrupted){
        m_deployGroup.cancel();
        if(m_spiked){
            m_spikedGroup.addCommands(new WaitCommand(Constants.INTAKE_ROLLER.AUTO_INTAKE_WAIT_TIME));
        }
        m_spikedGroup.addCommands(new IntakeStowCommandGroup());
        m_spikedGroup.schedule();
    }

}

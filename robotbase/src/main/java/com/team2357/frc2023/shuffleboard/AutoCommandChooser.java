package com.team2357.frc2023.shuffleboard;

import com.team2357.frc2023.trajectoryutil.AvailableTrajectoryCommands;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoCommandChooser {

    private AutoActionChooser autoChooser;

    private enum automodes {
        NONE { public String toString() { return "None"; } },
        SCORE_HIGH_CONE { public String toString() { return "High Cone"; } },
        COL_4_STOW_BALANCE { public String toString() { return "Col 4,Stow,Balance"; } },
        COL_9_COL_7_BALANCE { public String toString() { return "Col 9,Col 7,Balance"; } },
    }

    private class AutoActionChooser {
        protected SendableChooser<automodes> m_chooser;

        protected AutoActionChooser() {
            m_chooser = new SendableChooser<>();

            m_chooser.setDefaultOption("None", automodes.NONE);
            for (automodes automode : automodes.values()) {
                if (automode != automodes.NONE) {
                    m_chooser.addOption(automode.toString(), automode);
                }
            }

            SmartDashboard.putData("Auto chooser", m_chooser);
        }

        public Command getActionCommand() {
            switch (m_chooser.getSelected()) {
                case SCORE_HIGH_CONE:
                    return AvailableTrajectoryCommands.scoreHighCone;
                case COL_4_STOW_BALANCE:
                    return AvailableTrajectoryCommands.col4StowBalance;
                case COL_9_COL_7_BALANCE:
                    return AvailableTrajectoryCommands.col9Col7Balance;
                case NONE:
                default:
                    return new WaitCommand(0);
            }
        }
        
    }

    public AutoCommandChooser() {
        autoChooser = new AutoActionChooser();
    }

    public Command generateCommand() {
        CommandScheduler.getInstance().removeComposedCommand(autoChooser.getActionCommand());
        return new ParallelCommandGroup(
            autoChooser.getActionCommand()
        );
    }
}
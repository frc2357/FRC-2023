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
        NONE,
        // GRID_0_TWO_CONE,
        // GRID_0_TWO_CONE_BALANCE,
        // GRID_1_TWO_CONE,
        // GRID_1_TWO_CONE_BALANCE,
        // GRID_2_TWO_CONE,
        // GRID_2_TWO_CONE_BALANCE
        GRID_ZERO,
        GRID_ONE,
        GRID_TWO
    }

    private class AutoActionChooser {
        protected SendableChooser<automodes> m_chooser;

        protected AutoActionChooser() {
            m_chooser = new SendableChooser<>();

            m_chooser.setDefaultOption("None", automodes.NONE);
            for (automodes automode : automodes.values()) {
                if (automode != automodes.NONE) {
                    m_chooser.addOption(automode.toString().toLowerCase(), automode);
                }
            }

            SmartDashboard.putData("Auto chooser", m_chooser);
        }

        public Command getActionCommand() {
            switch (m_chooser.getSelected()) {
                // case GRID_0_TWO_CONE:
                //     return new GridZeroTwoConeAutoCommand();
                // case GRID_0_TWO_CONE_BALANCE:
                //     return new GridZeroTwoConeBalanceAutoCommand();
                // case GRID_1_TWO_CONE:
                //     return new GridOneTwoConeAutoCommand();
                // case GRID_1_TWO_CONE_BALANCE:
                //     return new GridOneTwoConeBalanceAutoCommand();
                // case GRID_2_TWO_CONE:
                //     return new GridTwoTwoConeAutoCommand();
                // case GRID_2_TWO_CONE_BALANCE:
                //     return new GridTwoTwoConeBalanceAutoCommand();
                case GRID_ZERO:
                    return AvailableTrajectoryCommands.GridZeroTwoConeAuto;
                case GRID_ONE:
                    return AvailableTrajectoryCommands.GridOneScoreOneAndBalance;
                case GRID_TWO:
                    return AvailableTrajectoryCommands.GridTwoTwoConeAuto;
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
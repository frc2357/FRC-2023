package com.team2357.frc2023.shuffleboard;

import java.nio.file.ProviderMismatchException;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.team2357.frc2023.commands.auto.gridtwo.GridTwoTwoConeAutoCommand;
import com.team2357.frc2023.commands.drive.AutoBalanceCommand;
import com.team2357.frc2023.commands.drive.ZeroDriveCommand;
import com.team2357.frc2023.commands.intake.IntakeDeployCommandGroup;
import com.team2357.frc2023.commands.intake.IntakeSolenoidExtendCommand;
import com.team2357.frc2023.commands.intake.IntakeStowCommandGroup;
import com.team2357.frc2023.commands.scoring.cone.ConeAutoScoreHighCommandGroup;
import com.team2357.frc2023.commands.scoring.cone.ConeAutoScoreMidCommandGroup;
import com.team2357.frc2023.trajectoryutil.AvailableTrajectories;
import com.team2357.frc2023.trajectoryutil.AvailableTrajectoryCommands;
import com.team2357.frc2023.trajectoryutil.TrajectoryUtil;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoCommandChooser {

    private InitialLocationChooser initialLocationChooser;
    private SecondaryLocationChooser secondaryLocationChooser;

    private enum initialLocations {
        NONE,
        GRID_0,
        GRID_1,
        GRID_2
    }

    private class InitialLocationChooser {
        protected SendableChooser<initialLocations> m_chooser;

        protected InitialLocationChooser(int index) {
            m_chooser = new SendableChooser<>();

            m_chooser.setDefaultOption("None", initialLocations.NONE);
            for (initialLocations s : initialLocations.values()) {
                if (s != initialLocations.NONE)
                    m_chooser.addOption(s.toString().toLowerCase(), s);
            }

            SmartDashboard.putData("Initial Location", m_chooser);
        }
    }

    private enum secondaryLocations {
        NONE,
        CHARGE_STATION,
        GAME_PIECE
    }

    private class SecondaryLocationChooser {
        protected SendableChooser<secondaryLocations> m_chooser;

        protected SecondaryLocationChooser(int index) {
            m_chooser = new SendableChooser<>();

            m_chooser.setDefaultOption("None", secondaryLocations.NONE);
            for (secondaryLocations s : secondaryLocations.values()) {
                if (s != secondaryLocations.NONE) {
                    m_chooser.addOption(s.toString().toLowerCase(), s);
                }
            }

            SmartDashboard.putData("Secondary Location", m_chooser);
        }
    }

    public AutoCommandChooser() {
        initialLocationChooser = new InitialLocationChooser(0);
        secondaryLocationChooser = new SecondaryLocationChooser(1);
    }

    public Command generateCommand() {
        initialLocations initialLocation = initialLocationChooser.m_chooser.getSelected();
        secondaryLocations secondaryLocation = secondaryLocationChooser.m_chooser.getSelected();

        switch (initialLocation) {
            case GRID_0:
                switch (secondaryLocation) {
                    case CHARGE_STATION:
                        return new ParallelCommandGroup(
                            // new ConeAutoScoreHighCommandGroup(),

                            // new WaitCommand(6)
                            //         .andThen(AvailableTrajectories.getTrajectory(PATH_POINTS.NODE_1, PATH_POINTS.CHARGE_STATION))
                            //         .andThen(new AutoBalanceCommand())

                            // new WaitCommand(7)
                            //         .andThen(new IntakeDeployCommandGroup())
                            //         .andThen(new WaitCommand(0.75))
                            //         .andThen(new IntakeStowCommandGroup()),

                            // new WaitCommand(10)
                            //         .andThen(AvailableTrajectories.getTrajectory(stage, null))
                        );
                    case GAME_PIECE:
                        return new ParallelCommandGroup(
                            // Scoring
                            // new WaitCommand(0)
                            //         .andThen(new ConeAutoScoreHighCommandGroup())
                            //         .andThen(new WaitCommand(6))
                            //         .andThen(new ConeAutoScoreHighCommandGroup()),

                            // // Trajectory
                            // new WaitCommand(6)
                            //         .andThen(AvailableTrajectories.getTrajectory(PATH_POINTS.NODE_1, PATH_POINTS.STAGE_1))
                            //         .andThen(new WaitCommand(0))
                            //         .andThen(AvailableTrajectories.getTrajectory(null, null)),

                            // // Intake
                            // new WaitCommand(7)
                            //         .andThen(new IntakeDeployCommandGroup())
                            //         .andThen(new WaitCommand(0.75))
                            //         .andThen(new IntakeStowCommandGroup())

                        );
                    default:
                        break;
                }
            case GRID_1:
                switch (secondaryLocation) {
                    case CHARGE_STATION:
                        break;
                    case GAME_PIECE:
                        break;
                    default:
                        break;
                }
            case GRID_2:
                switch (secondaryLocation) {
                    case CHARGE_STATION:
                        break;
                    case GAME_PIECE:
                        Command trajectory1 = AvailableTrajectories.getTrajectory(PATH_POINTS.NODE_9, PATH_POINTS.STAGE_4);
                        Command trajectory2 = AvailableTrajectories.getTrajectory(PATH_POINTS.STAGE_4, PATH_POINTS.NODE_7);
                        Command intakeStow = new IntakeStowCommandGroup();
                        CommandScheduler.getInstance().removeComposedCommand(trajectory1);
                        CommandScheduler.getInstance().removeComposedCommand(trajectory2);
                        CommandScheduler.getInstance().removeComposedCommand(intakeStow);

                        return new ParallelCommandGroup(
                            // new WaitCommand(0)
                            //         .andThen(new ConeAutoScoreHighCommandGroup()) // abt 7 seconds
                            //         .andThen(new IntakeDeployCommandGroup()
                            //             .withTimeout(3))
                            //         .andThen(intakeStow)
                            //         .andThen(new WaitCommand(2))
                            //         .andThen(new ConeAutoScoreHighCommandGroup()),

                            new WaitCommand(0) // 6
                                    .andThen(trajectory1) // abt 4 seconds
                                    // .andThen(new WaitCommand(2))
                                    // .andThen(trajectory2), // abt 4 seconds

                            // new WaitCommand(8)
                        );
                    default:
                        break;
                }
            default:
                break;
        }


        return new WaitCommand(0);
    }
}
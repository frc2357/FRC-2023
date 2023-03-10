package com.team2357.frc2023.subsystems;

import com.team2357.frc2023.Constants;

public class SubsystemFactory {
        public SwerveDriveSubsystem CreateSwerveDriveSubsystem() {

                SwerveDriveSubsystem subsystem = new SwerveDriveSubsystem(
                        Constants.CAN_ID.PIGEON_ID,
                        new int[] {
                                Constants.CAN_ID.FRONT_LEFT_MODULE_DRIVE_MOTOR_ID,
                                Constants.CAN_ID.FRONT_LEFT_MODULE_STEER_MOTOR_ID,
                                Constants.CAN_ID.FRONT_LEFT_MODULE_STEER_ENCODER_ID
                        },new int[] {
                                Constants.CAN_ID.FRONT_RIGHT_MODULE_DRIVE_MOTOR_ID,
                                Constants.CAN_ID.FRONT_RIGHT_MODULE_STEER_MOTOR_ID,
                                Constants.CAN_ID.FRONT_RIGHT_MODULE_STEER_ENCODER_ID
                        },
                        new int[] {
                                Constants.CAN_ID.BACK_LEFT_MODULE_DRIVE_MOTOR_ID,
                                Constants.CAN_ID.BACK_LEFT_MODULE_STEER_MOTOR_ID,
                                Constants.CAN_ID.BACK_LEFT_MODULE_STEER_ENCODER_ID
                        },
                        new int[] {
                                Constants.CAN_ID.BACK_RIGHT_MODULE_DRIVE_MOTOR_ID,
                                Constants.CAN_ID.BACK_RIGHT_MODULE_STEER_MOTOR_ID,
                                Constants.CAN_ID.BACK_RIGHT_MODULE_STEER_ENCODER_ID
                        },
                        Constants.CAN_ID.DRIVE_CANBUS,
                        Constants.DRIVE.SWERVE_MODULE_SHUFFLEBOARD_TAB_NAME
                );
                subsystem.configure(Constants.DRIVE.GET_SWERVE_DRIVE_CONFIG());

                return subsystem;
        }

        public Team364SwerveDriveSubsystem CreateTeam364SwerveDriveSubsystem() {
                Team364SwerveDriveSubsystem subsystem = new Team364SwerveDriveSubsystem(
                        Constants.CAN_ID.PIGEON_ID,
                        Constants.TEAM_364_SWERVE.FRONT_LEFT_MODULE_CONSTANTS,
                        Constants.TEAM_364_SWERVE.FRONT_RIGHT_MODULE_CONSTANTS,
                        Constants.TEAM_364_SWERVE.BACK_LEFT_MODULE_CONSTANTS,
                        Constants.TEAM_364_SWERVE.BACK_RIGHT_MODULE_CONSTANTS,
                )

                subsystem.configure(Constants.TEAM_364_SWERVE.GET_SWERVE_DRIVE_CONFIG());

                return subsystem;
        }

        public IntakeRollerSubsystem CreateIntakeRollerSubsystem() {
                IntakeRollerSubsystem subsystem = new IntakeRollerSubsystem(Constants.CAN_ID.MASTER_INTAKE_MOTOR_ID,
                                Constants.CAN_ID.FOLLOWER_INTAKE_MOTOR_ID);
                subsystem.configure(Constants.INTAKE_ROLLER.GET_INTAKE_CONFIG());
                return subsystem;
        }

        public WristSubsystem CreateWristSubsystem() {
                WristSubsystem subsystem = new WristSubsystem(Constants.PH_ID.WRIST_FORWARD_SOLENOID_CHANNEL,
                                Constants.PH_ID.WRIST_REVERSE_SOLENOID_CHANNEL);
                subsystem.configure(Constants.WRIST.GET_WRIST_CONFIG());
                return subsystem;
        }

        public ClawSubsystem CreateClawSubsystem() {
                ClawSubsystem subsystem = new ClawSubsystem(Constants.PH_ID.CLAW_FORWARD_SOLENOID_CHANNEL,
                                Constants.PH_ID.CLAW_REVERSE_SOLENOID_CHANNEL);
                subsystem.configure(Constants.CLAW.GET_CLAW_CONFIG());
                return subsystem;
        }

        public IntakeArmSubsystem CreateIntakeArmSubsystem() {
                IntakeArmSubsystem subsystem = new IntakeArmSubsystem(Constants.PH_ID.INTAKE_SOLENOID_FORWARD_CHANNEL,
                                Constants.PH_ID.INTAKE_SOLENOID_REVERSE_CHANNEL, Constants.CAN_ID.INTAKE_WINCH_MOTOR_ID);
                subsystem.configure(Constants.INTAKE_ARM.GET_INTAKE_ARM_CONFIG());
                return subsystem;
        }

        public ArmRotationSubsystem CreateArmRotationSubsystem() {
                ArmRotationSubsystem subsystem = new ArmRotationSubsystem(Constants.CAN_ID.ARM_ROTATION_MOTOR_ID);
                subsystem.configure(Constants.ARM_ROTATION.GET_ROTATION_CONFIG());
                return subsystem;
        }

        public ArmExtensionSubsystem CreateArmExtensionSubsystem() {
                ArmExtensionSubsystem subsystem = new ArmExtensionSubsystem(Constants.CAN_ID.ARM_EXTENSION_MOTOR_ID);
                subsystem.configure(Constants.ARM_EXTENSION.GET_EXTENSION_CONFIG());
                return subsystem;
        }
        
        public DualLimelightManagerSubsystem CreateDualLimelightManagerSubsystem(){
                DualLimelightManagerSubsystem manager = new DualLimelightManagerSubsystem(
                        Constants.LIMELIGHT.LEFT_LIMELIGHT_NAME, Constants.LIMELIGHT.RIGHT_LIMELIGHT_NAME,
                        Constants.LIMELIGHT.LEFT_LIMELIGHT_TX_SETPOINT, Constants.LIMELIGHT.RIGHT_LIMELIGHT_TX_SETPOINT);
                return manager;
        }
}

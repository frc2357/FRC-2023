package com.team2357.frc2023.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.WPI_Pigeon2;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.swervedrivespecialties.swervelib.Mk4iSwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SwerveModule;
import com.team2357.frc2023.Constants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class SubsystemFactory {
        public SwerveDriveSubsystem CreateSwerveDriveSubsystem() {
                // ShuffleboardTab tab = Shuffleboard.getTab(Constants.DRIVE.SWERVE_MODULE_SHUFFLEBOARD_TAB_NAME);

                // WPI_Pigeon2 pigeon = new WPI_Pigeon2(Constants.CAN_ID.PIGEON_ID, Constants.CAN_ID.DRIVE_CANBUS);

                // SwerveModule frontLeft = Mk4iSwerveModuleHelper.createFalcon500(
                //                 tab.getLayout("Front Left Module", BuiltInLayouts.kList)
                //                                 .withSize(2, 4)
                //                                 .withPosition(0, 0),
                //                 Mk4iSwerveModuleHelper.GearRatio.L2,
                //                 Constants.CAN_ID.FRONT_LEFT_MODULE_DRIVE_MOTOR_ID,
                //                 Constants.CAN_ID.FRONT_LEFT_MODULE_STEER_MOTOR_ID,
                //                 Constants.CAN_ID.FRONT_LEFT_MODULE_STEER_ENCODER_ID,
                //                 // the canbus the module is on
                //                 Constants.CAN_ID.DRIVE_CANBUS,
                //                 Constants.DRIVE.FRONT_LEFT_MODULE_STEER_OFFSET);

                // SwerveModule frontRight = Mk4iSwerveModuleHelper.createFalcon500(
                //                 tab.getLayout("Front Right Module", BuiltInLayouts.kList)
                //                                 .withSize(2, 4)
                //                                 .withPosition(2, 0),
                //                 Mk4iSwerveModuleHelper.GearRatio.L2,
                //                 Constants.CAN_ID.FRONT_RIGHT_MODULE_DRIVE_MOTOR_ID,
                //                 Constants.CAN_ID.FRONT_RIGHT_MODULE_STEER_MOTOR_ID,
                //                 Constants.CAN_ID.FRONT_RIGHT_MODULE_STEER_ENCODER_ID,
                //                 // the canbus the module is on
                //                 Constants.CAN_ID.DRIVE_CANBUS,
                //                 Constants.DRIVE.FRONT_RIGHT_MODULE_STEER_OFFSET);

                // SwerveModule backLeft = Mk4iSwerveModuleHelper.createFalcon500(
                //                 tab.getLayout("Back Left Module", BuiltInLayouts.kList)
                //                                 .withSize(2, 4)
                //                                 .withPosition(4, 0),
                //                 Mk4iSwerveModuleHelper.GearRatio.L2,
                //                 Constants.CAN_ID.BACK_LEFT_MODULE_DRIVE_MOTOR_ID,
                //                 Constants.CAN_ID.BACK_LEFT_MODULE_STEER_MOTOR_ID,
                //                 Constants.CAN_ID.BACK_LEFT_MODULE_STEER_ENCODER_ID,
                //                 // the canbus the module is on
                //                 Constants.CAN_ID.DRIVE_CANBUS,
                //                 Constants.DRIVE.BACK_LEFT_MODULE_STEER_OFFSET);

                // SwerveModule backRight = Mk4iSwerveModuleHelper.createFalcon500(
                //                 tab.getLayout("Back Right Module", BuiltInLayouts.kList)
                //                                 .withSize(2, 4)
                //                                 .withPosition(6, 0),
                //                 Mk4iSwerveModuleHelper.GearRatio.L2,
                //                 Constants.CAN_ID.BACK_RIGHT_MODULE_DRIVE_MOTOR_ID,
                //                 Constants.CAN_ID.BACK_RIGHT_MODULE_STEER_MOTOR_ID,
                //                 Constants.CAN_ID.BACK_RIGHT_MODULE_STEER_ENCODER_ID,
                //                 // the canbus the module is on
                //                 Constants.CAN_ID.DRIVE_CANBUS,
                //                 Constants.DRIVE.BACK_RIGHT_MODULE_STEER_OFFSET);

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

        public IntakeRollerSubsystem CreateIntakeRollerSubsystem() {
                IntakeRollerSubsystem subsystem = new IntakeRollerSubsystem(Constants.CAN_ID.MASTER_INTAKE_MOTOR,
                                Constants.CAN_ID.FOLLOWER_INTAKE_MOTOR);
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
                                Constants.PH_ID.INTAKE_SOLENOID_REVERSE_CHANNEL);
                subsystem.configure(Constants.INTAKE_ARM.GET_INTAKE_ARM_CONFIG());
                return subsystem;
        }

        public ArmRotationSubsystem CreateArmRotationSubsystem() {
                ArmRotationSubsystem subsystem = new ArmRotationSubsystem(Constants.CAN_ID.ROTATION_MOTOR);
                subsystem.configure(Constants.ARM_ROTATION.GET_ROTATION_CONFIG());
                return subsystem;
        }

        public ArmExtensionSubsystem CreateArmExtensionSubsystem() {
                ArmExtensionSubsystem subsystem = new ArmExtensionSubsystem(Constants.CAN_ID.ARM_EXTENSION_MOTOR);
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

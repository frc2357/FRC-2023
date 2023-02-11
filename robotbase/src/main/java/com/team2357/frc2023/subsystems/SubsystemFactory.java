package com.team2357.frc2023.subsystems;

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
                ShuffleboardTab tab = Shuffleboard.getTab("Drivetrain");

                WPI_Pigeon2 pigeon = new WPI_Pigeon2(Constants.CAN_ID.PIGEON_ID, Constants.CAN_ID.DRIVE_CANBUS);

                SwerveModule frontLeft = Mk4iSwerveModuleHelper.createFalcon500(
                                tab.getLayout("Front Left Module", BuiltInLayouts.kList)
                                                .withSize(2, 4)
                                                .withPosition(0, 0),
                                Mk4iSwerveModuleHelper.GearRatio.L2,
                                Constants.CAN_ID.FRONT_LEFT_MODULE_DRIVE_MOTOR_ID,
                                Constants.CAN_ID.FRONT_LEFT_MODULE_STEER_MOTOR_ID,
                                Constants.CAN_ID.FRONT_LEFT_MODULE_STEER_ENCODER_ID,
                                // the canbus the module is on
                                Constants.CAN_ID.DRIVE_CANBUS,
                                Constants.DRIVE.FRONT_LEFT_MODULE_STEER_OFFSET);

                SwerveModule frontRight = Mk4iSwerveModuleHelper.createFalcon500(
                                tab.getLayout("Front Right Module", BuiltInLayouts.kList)
                                                .withSize(2, 4)
                                                .withPosition(2, 0),
                                Mk4iSwerveModuleHelper.GearRatio.L2,
                                Constants.CAN_ID.FRONT_RIGHT_MODULE_DRIVE_MOTOR_ID,
                                Constants.CAN_ID.FRONT_RIGHT_MODULE_STEER_MOTOR_ID,
                                Constants.CAN_ID.FRONT_RIGHT_MODULE_STEER_ENCODER_ID,
                                // the canbus the module is on
                                Constants.CAN_ID.DRIVE_CANBUS,
                                Constants.DRIVE.FRONT_RIGHT_MODULE_STEER_OFFSET);

                SwerveModule backLeft = Mk4iSwerveModuleHelper.createFalcon500(
                                tab.getLayout("Back Left Module", BuiltInLayouts.kList)
                                                .withSize(2, 4)
                                                .withPosition(4, 0),
                                Mk4iSwerveModuleHelper.GearRatio.L2,
                                Constants.CAN_ID.BACK_LEFT_MODULE_DRIVE_MOTOR_ID,
                                Constants.CAN_ID.BACK_LEFT_MODULE_STEER_MOTOR_ID,
                                Constants.CAN_ID.BACK_LEFT_MODULE_STEER_ENCODER_ID,
                                // the canbus the module is on
                                Constants.CAN_ID.DRIVE_CANBUS,
                                Constants.DRIVE.BACK_LEFT_MODULE_STEER_OFFSET);

                SwerveModule backRight = Mk4iSwerveModuleHelper.createFalcon500(
                                tab.getLayout("Back Right Module", BuiltInLayouts.kList)
                                                .withSize(2, 4)
                                                .withPosition(6, 0),
                                Mk4iSwerveModuleHelper.GearRatio.L2,
                                Constants.CAN_ID.BACK_RIGHT_MODULE_DRIVE_MOTOR_ID,
                                Constants.CAN_ID.BACK_RIGHT_MODULE_STEER_MOTOR_ID,
                                Constants.CAN_ID.BACK_RIGHT_MODULE_STEER_ENCODER_ID,
                                // the canbus the module is on
                                Constants.CAN_ID.DRIVE_CANBUS,
                                Constants.DRIVE.BACK_RIGHT_MODULE_STEER_OFFSET);

                SwerveDriveSubsystem subsystem = new SwerveDriveSubsystem(pigeon, frontLeft, frontRight, backLeft,
                                backRight);
                subsystem.configure(Constants.DRIVE.GET_SWERVE_DRIVE_CONFIG());

                return subsystem;
        }

        public IntakeRollerSubsystem CreateIntakeRollerSubsystem() {
                IntakeRollerSubsystem subsystem = new IntakeRollerSubsystem();
                subsystem.configure(Constants.INTAKE_ROLLER.GET_INTAKE_CONFIG());
                return subsystem;
        }

        public WristSubsystem CreateWristSubsystem() {
                DoubleSolenoid wristSolenoid = new DoubleSolenoid(Constants.CAN_ID.PNEUMATICS_HUB_ID,
                                PneumaticsModuleType.REVPH, Constants.PH_ID.WRIST_SOLENOID_FORWARD_CHANNEL,
                                Constants.PH_ID.WRIST_SOLENOID_REVERSE_CHANNEL);
                WristSubsystem subsystem = new WristSubsystem(wristSolenoid);
                subsystem.configure(Constants.WRIST.GET_WRIST_CONFIG());
                return subsystem;
        }

        public ClawSubsystem CreateClawSubsystem() {
                DoubleSolenoid clawSolenoid = new DoubleSolenoid(Constants.CAN_ID.PNEUMATICS_HUB_ID,
                                PneumaticsModuleType.REVPH, Constants.PH_ID.CLAW_SOLENOID_FORWARD_CHANNEL,
                                Constants.PH_ID.CLAW_SOLENOID_REVERSE_CHANNEL);

                ClawSubsystem subsystem = new ClawSubsystem(clawSolenoid);
                subsystem.configure(Constants.CLAW.GET_CLAW_CONFIG());
                return subsystem;
        }

        public IntakeArmSubsystem CreateIntakeArmSubsystem() {
                DoubleSolenoid intakeSolenoid = new DoubleSolenoid(Constants.CAN_ID.PNEUMATICS_HUB_ID,
                                PneumaticsModuleType.REVPH, Constants.PH_ID.INTAKE_SOLENOID_FORWARD_CHANNEL,
                                Constants.PH_ID.INTAKE_SOLENOID_REVERSE_CHANNEL);
                IntakeArmSubsystem subsystem = new IntakeArmSubsystem(intakeSolenoid);
                subsystem.configure(Constants.INTAKE_ARM.GET_INTAKE_ARM_CONFIG());
                return subsystem;
        }

        public ElevatorSubsystem createElevatorSubsystem() {
                CANSparkMax rightMotor = new CANSparkMax(Constants.CAN_ID.RIGHT_ELEVATOR_MOTOR, MotorType.kBrushless);
                CANSparkMax leftMotor = new CANSparkMax(Constants.CAN_ID.LEFT_ELEVATOR_MOTOR, MotorType.kBrushless);

                ElevatorSubsystem subsystem = new ElevatorSubsystem(rightMotor, leftMotor);
                subsystem.configure(Constants.ELEVATOR.GET_ELEVATOR_CONFIG());
                return subsystem;
        }
}

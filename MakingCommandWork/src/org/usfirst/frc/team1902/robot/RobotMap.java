package org.usfirst.frc.team1902.robot;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    // For example to map the left and right motors, you could define the
    // following variables to use with your drivetrain subsystem.
	//PWM
    public static int leftMotorPin = 1;
    public static int rightMotorPin = 0;
    public static int intakeLeftPin = 2;
    public static int intakeRightPin = 3;
    public static int elevatorLiftPin = 4;
    
    //PCM
    public static int intakeSolenoidPin = 0;
    
    //DS
    public static int driveStickPort = 0;
    public static int auxStickPort = 1;
    
    // If you are using multiple modules, make sure to define both the port
    // number and the module. For example you with a rangefinder:
    // public static int rangefinderPort = 1;
    // public static int rangefinderModule = 1;
}

package org.usfirst.frc.team1902.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

import org.usfirst.frc.team1902.robot.commands.DriveArcadeCommand;
import org.usfirst.frc.team1902.robot.commands.ElevatorLiftCommand;
import org.usfirst.frc.team1902.robot.commands.ElevatorLowerCommand;
import org.usfirst.frc.team1902.robot.commands.IntakeClampCommand;
import org.usfirst.frc.team1902.robot.commands.IntakeReleaseCommand;
import org.usfirst.frc.team1902.robot.commands.IntakeRotateCommand;
import org.usfirst.frc.team1902.robot.commands.IntakeSpitCommand;
import org.usfirst.frc.team1902.robot.commands.IntakeStartCommand;
import org.usfirst.frc.team1902.robot.commands.IntakeStopCommand;
import org.usfirst.frc.team1902.robot.commands.DriveTankCommand;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
    //// CREATING BUTTONS
    // One type of button is a joystick button which is any button on a joystick.
    // You create one by telling it which joystick it's on and which button
    // number it is.
    // Joystick stick = new Joystick(port);
    // Button button = new JoystickButton(stick, buttonNumber);
	
	public Joystick driveStick = new Joystick(RobotMap.driveStickPort);
	public Joystick auxStick = new Joystick(RobotMap.auxStickPort);
	
	Button tankDriveButton = new JoystickButton(driveStick, 8);
	Button arcadeDriveButton = new JoystickButton(driveStick, 9);

	Button intakeStartButton = new JoystickButton(driveStick, 2);
	Button intakeSpitButton = new JoystickButton(driveStick, 3);
	Button intakeRotateCWButton = new JoystickButton(driveStick, 5);
	Button intakeRotateCCWButton = new JoystickButton(driveStick, 4);
	Button intakeReleaseButton = new JoystickButton(driveStick, 1);
	
	Button elevatorLiftButton = new JoystickButton(driveStick, 6);
	Button elevatorLowerButton = new JoystickButton(driveStick, 7);
    
    // There are a few additional built in buttons you can use. Additionally,
    // by subclassing Button you can create custom triggers and bind those to
    // commands the same as any other Button.
    
    //// TRIGGERING COMMANDS WITH BUTTONS
    // Once you have a button, it's trivial to bind it to a button in one of
    // three ways:
    
    // Start the command when the button is pressed and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenPressed(new ExampleCommand());
    
    // Run the command while the button is being held down and interrupt it once
    // the button is released.
    // button.whileHeld(new ExampleCommand());
    
    // Start the command when the button is released  and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenReleased(new ExampleCommand());
	
	public OI(){		
		tankDriveButton.whenPressed(new DriveTankCommand());
		arcadeDriveButton.whenPressed(new DriveArcadeCommand());
		
		intakeStartButton.whenPressed(new IntakeStartCommand());
		intakeSpitButton.whenPressed(new IntakeSpitCommand());
		intakeRotateCWButton.whenPressed(new IntakeRotateCommand(true));
		intakeRotateCCWButton.whenPressed(new IntakeRotateCommand(false));
		intakeReleaseButton.whenPressed(new IntakeReleaseCommand());
		
		intakeStartButton.whenReleased(new IntakeStopCommand());
		intakeSpitButton.whenReleased(new IntakeStopCommand());
		intakeRotateCWButton.whenReleased(new IntakeStopCommand());
		intakeRotateCCWButton.whenReleased(new IntakeStopCommand());
		intakeReleaseButton.whenReleased(new IntakeClampCommand());
		
		elevatorLiftButton.whileHeld(new ElevatorLiftCommand());
		elevatorLowerButton.whileHeld(new ElevatorLowerCommand());
	}
}


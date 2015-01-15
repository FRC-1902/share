package org.usfirst.frc.team1902.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

import org.usfirst.frc.team1902.robot.commands.ArcadeDrive;
import org.usfirst.frc.team1902.robot.commands.IntakeSpitCommand;
import org.usfirst.frc.team1902.robot.commands.IntakeStartCommand;
import org.usfirst.frc.team1902.robot.commands.SolenoidFlipCommand;
import org.usfirst.frc.team1902.robot.commands.TankDrive;

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
	
	Button solenoidFlipButton = new JoystickButton(driveStick, RobotMap.solenoidFlip);
	Button solenoidStutterButton = new JoystickButton(driveStick, RobotMap.solenoidStutter);
	
	Button tankDriveButton = new JoystickButton(driveStick, 8);
	Button arcadeDriveButton = new JoystickButton(driveStick, 9);

	Button intakeStartButton = new JoystickButton(driveStick, 4);
	Button intakeSpitButton = new JoystickButton(driveStick, 5);
    
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
		solenoidFlipButton.whenPressed(new SolenoidFlipCommand());
		solenoidStutterButton.whileHeld(new SolenoidFlipCommand());
		
		tankDriveButton.whenPressed(new TankDrive());
		arcadeDriveButton.whenPressed(new ArcadeDrive());
		
		intakeStartButton.whileHeld(new IntakeStartCommand());
		intakeSpitButton.whileHeld(new IntakeSpitCommand());
	}
}


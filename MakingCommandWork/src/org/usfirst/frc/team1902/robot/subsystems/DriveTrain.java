package org.usfirst.frc.team1902.robot.subsystems;

import org.usfirst.frc.team1902.robot.RobotMap;
import org.usfirst.frc.team1902.robot.commands.DriveArcadeCommand;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class DriveTrain extends Subsystem {
    
	Talon leftMotor = new Talon(RobotMap.leftMotorPin);
	Talon rightMotor = new Talon(RobotMap.rightMotorPin);
	
	public void forward(float speed)
	{
		leftMotor.set(speed);
		rightMotor.set(speed);
	}
	
	public void backwards(float speed)
	{
		leftMotor.set(speed);
		rightMotor.set(speed);
	}
	
	public void turn(float left, float right)
	{
		leftMotor.set(left);
		rightMotor.set(right);
	}
	
	public void arcade(Joystick stick)
	{
		double x = stick.getX();
		double y = stick.getY();
		leftMotor.set(y-x);
		rightMotor.set(y+x);
	}
	
	public void tank(Joystick leftStick, Joystick rightStick)
	{
		leftMotor.set(leftStick.getY());
		rightMotor.set(rightStick.getY());
	}
	
	
	public void stop()
	{
		leftMotor.set(0);
		rightMotor.set(0);
	}

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new DriveArcadeCommand());
    }
}


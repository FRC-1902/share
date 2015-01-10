package org.usfirst.frc.team1902.kitbot.subsystems;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class DriveTrain extends Subsystem {
    
	Talon leftMotor = new Talon (0);
	Talon rightMotor = new Talon (0);
	
	public void arcadeDrive(float x, float y)
	{
		leftMotor.set(x+y);
		rightMotor.set(x-y);
	}
	
	public void tankDrive(float left, float right)
	{
		leftMotor.set(left);
		rightMotor.set(right);
	}
	
	public void stop()
	{
		leftMotor.set(0);
		rightMotor.set(0);
	}
	
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}


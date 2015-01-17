package org.usfirst.frc.team1902.robot.subsystems;

import org.usfirst.frc.team1902.robot.Robot;
import org.usfirst.frc.team1902.robot.RobotMap;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Intake extends Subsystem {
    
	Talon intakeLeft = new Talon(RobotMap.intakeLeftPin);
	Talon intakeRight = new Talon(RobotMap.intakeRightPin);
	
	Solenoid solenoid = new Solenoid(RobotMap.intakeSolenoidPin);
	
    public void start()
    {
    	double power = Robot.oi.driveStick.getZ();
    	power = (power-1)/2;
    	intakeLeft.set(power);
    	intakeRight.set(-power);
    }
    
    public void spit()
    {
    	double power = Robot.oi.driveStick.getZ();
    	power = (power-1)/2;
    	
    	intakeLeft.set(-power);
    	intakeRight.set(power);
    }
    
    public void stop()
    {
    	intakeLeft.set(0);
    	intakeRight.set(0);
    }
    
    public void rotate(boolean isCW)
    {
    	double power = Robot.oi.driveStick.getZ();
    	power = (power-1)/2;
    	
    	if(isCW)
    	{
	    	intakeLeft.set(-power);
	    	intakeRight.set(-power);
    	}
    	else
    	{
    		intakeLeft.set(power);
	    	intakeRight.set(power);
    	}
    }
    
    public void clamp()
    {
    	solenoid.set(true);
    }
    
    public void release()
    {
    	solenoid.set(false);
    }
    
    public void toggleSolenoid()
    {
    	solenoid.set(!solenoid.get());
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new IntakeStopCommand());
    }
}


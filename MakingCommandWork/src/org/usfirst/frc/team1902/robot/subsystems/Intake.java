package org.usfirst.frc.team1902.robot.subsystems;

import org.usfirst.frc.team1902.robot.RobotMap;
import org.usfirst.frc.team1902.robot.commands.IntakeStopCommand;

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
    	intakeLeft.set(1);
    	intakeRight.set(1);
    }
    
    public void spit()
    {
    	intakeLeft.set(-1);
    	intakeRight.set(-1);
    }
    
    public void stop()
    {
    	intakeLeft.set(0);
    	intakeRight.set(0);
    }
    
    public void rotate(boolean isCW)
    {
    	if(isCW)
    	{
	    	intakeLeft.set(-0.5);
	    	intakeRight.set(0.5);
    	}
    	else
    	{
    		intakeLeft.set(0.5);
	    	intakeRight.set(-0.5);
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
        setDefaultCommand(new IntakeStopCommand());
    }
}


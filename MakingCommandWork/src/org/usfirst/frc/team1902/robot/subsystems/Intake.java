package org.usfirst.frc.team1902.robot.subsystems;

import org.usfirst.frc.team1902.robot.RobotMap;
import org.usfirst.frc.team1902.robot.commands.IntakeStopCommand;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Intake extends Subsystem {
    
	Talon intaker = new Talon(RobotMap.intakePin);
	
    public void start()
    {
    	intaker.set(1);
    }
    
    public void spit()
    {
    	intaker.set(-1);
    }
    
    public void stop()
    {
    	intaker.set(0);
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new IntakeStopCommand());
    }
}


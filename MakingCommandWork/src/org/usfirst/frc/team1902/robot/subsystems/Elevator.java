package org.usfirst.frc.team1902.robot.subsystems;

import org.usfirst.frc.team1902.robot.Robot;
import org.usfirst.frc.team1902.robot.RobotMap;
import org.usfirst.frc.team1902.robot.commands.ElevatorStopCommand;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Elevator extends Subsystem {
    
    Talon liftMotor = new Talon(RobotMap.elevatorLiftPin);
    
    public void lift()
    {
    	double power = Robot.oi.driveStick.getZ();
    	power = (power-1)/2;
    	
    	liftMotor.set(power);
    }
    
    public void lower()
    {
    	double power = Robot.oi.driveStick.getZ();
    	power = (power-1)/2;
    	
    	liftMotor.set(-power);
    }
    
    public void stop()
    {
    	liftMotor.set(0);
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new ElevatorStopCommand());
    }
}


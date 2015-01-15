package org.usfirst.frc.team1902.robot.subsystems;

import org.usfirst.frc.team1902.robot.RobotMap;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Pneumatics extends Subsystem {
    
    Compressor compressor = new Compressor();
    Solenoid solenoid = new Solenoid(RobotMap.solenoidPin);
    
    public void start()
    {
    	compressor.start();
    }
    
    public void stop()
    {
    	compressor.stop();
    }
    
    public void solenoidOn()
    {
    	solenoid.set(true);
    }
    
    public void solenoidOff()
    {
    	solenoid.set(false);
    }
    
    public void solenoidFlip()
    {
    	solenoid.set(!solenoid.get());
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}


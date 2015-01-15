package org.usfirst.frc.team1902.robot.subsystems;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Pneumatics extends Subsystem {
    
    Compressor compressor = new Compressor();
    
    public void start()
    {
    	compressor.start();
    }
    
    public void stop()
    {
    	compressor.stop();
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}


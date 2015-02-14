package org.usfirst.frc.traumaKit.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Talon;

public class Robot extends IterativeRobot {
    
	Talon leftDrive = new Talon(0);
	Talon rightTalon = new Talon(1);
	//REFACTOR to be descriptive
	Talon manipulator1 = new Talon(2);
	Talon manipulator2 = new Talon(3);
	
    public void robotInit() {
    	
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {

    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}

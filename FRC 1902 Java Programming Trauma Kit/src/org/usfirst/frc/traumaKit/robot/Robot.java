package org.usfirst.frc.traumaKit.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;

public class Robot extends IterativeRobot {
    
	Joystick driver = new Joystick(0);
	XboxController manipulator = new XboxController(1);
	
	Talon leftDrive = new Talon(0);
	Talon rightDrive = new Talon(1);
	//REFACTOR to be descriptive
	Talon motor1 = new Talon(2);
	Talon motor2 = new Talon(3);
	
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
        
    	 /* HOW TO USE YOUR XBOX CONTROLLER
    	 * 
    	 * Reading the left joystick: "manipulator.getX()" and "manipulator.getY()".  	 
    	 * Reading the right joystick: "manipulator.getX2()" and "manipulator.getY2()".
    	 * Reading the DPad = "manipulator.getDPad()".
    	 * Reading the triggers: "manipulator.getLeftTrigger()" and "manipulator.getRightTrigger()".
    	 */
    	
    	//Arcade Drive using the driver's joystick.
    	leftDrive.set(driver.getX() - driver.getY());
    	rightDrive.set(driver.getX() + driver.getY());
    	
    	if (manipulator.a.get()) { //If the "A" button on the Xbox controller is pressed
    		motor1.set(1);
    	} else {
    		motor1.set(0);
    	}
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}

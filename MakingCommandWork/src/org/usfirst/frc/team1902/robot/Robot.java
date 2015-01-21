
package org.usfirst.frc.team1902.robot;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team1902.robot.commands.AutoDriveBackwardCommand;
import org.usfirst.frc.team1902.robot.commands.AutoDriveForwardCommand;
import org.usfirst.frc.team1902.robot.commands.IntakeClampCommand;
import org.usfirst.frc.team1902.robot.subsystems.DriveTrain;
import org.usfirst.frc.team1902.robot.subsystems.Elevator;
import org.usfirst.frc.team1902.robot.subsystems.ExampleSubsystem;
import org.usfirst.frc.team1902.robot.subsystems.Intake;
import org.usfirst.frc.team1902.robot.subsystems.Pneumatics;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static final DriveTrain driveTrain = new DriveTrain();
	public static final Pneumatics pneumatics = new Pneumatics();
	public static final Elevator elevator = new Elevator();
	public static final Intake intake = new Intake();
	public static OI oi;

    Command autonomousCommand;
    public static DriverStation ds;
    BuiltInAccelerometer accel;
    
    SendableChooser autoChooser;
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    
    public void robotInit() {
		oi = new OI();
        ds = DriverStation.getInstance();
        accel = new BuiltInAccelerometer();
        
        autoChooser = new SendableChooser();
        autoChooser.addDefault("Forwards", new AutoDriveForwardCommand());
        autoChooser.addObject("Super Special Backwards", new AutoDriveBackwardCommand());
        SmartDashboard.putData("Auto Chooser", autoChooser);
    }
	
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

    public void autonomousInit() {
        // schedule the autonomous command (example)
    	autonomousCommand = (Command) autoChooser.getSelected();
        if (autonomousCommand != null) autonomousCommand.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    public void teleopInit() {
		// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) autonomousCommand.cancel();
        new IntakeClampCommand().start();
    }

    /**
     * This function is called when the disabled button is hit.
     * You can use it to reset subsystems before shutting down.
     */
    public void disabledInit(){

    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        
        SmartDashboard.putNumber("Accel X", accel.getX());
        SmartDashboard.putNumber("Accel Y", accel.getY());
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}

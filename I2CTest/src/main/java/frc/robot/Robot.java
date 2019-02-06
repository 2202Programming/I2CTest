/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private String serialResults;
  private SerialPort arduinoSerial;
  private int distance1;
  private int distance2;
  private long distanceRefresh;
  

  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    
    arduinoSerial = new SerialPort(9600, SerialPort.Port.kUSB);
    distanceRefresh = System.currentTimeMillis();

  }

  @Override
  public void teleopInit() {
    
  }

  @Override
  public void disabledInit() {
    
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    processSerial();
 
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public void operatorControl(){
  }

  public void processSerial() {
    String results = "";
    int sensor = 0;
    int distance = 0;
    
    //reduce buffer size to last 1000 bytes to prevent loop time overrun
    while (arduinoSerial.getBytesReceived()>1000) results = arduinoSerial.readString(); 
  
    //read buffer if available one char at a time
    while (arduinoSerial.getBytesReceived()>0) {
      results = arduinoSerial.readString(1);

        //E is end of statement, otherwise add to running string
        if (!results.contentEquals("E")) {
        serialResults = serialResults + results;
        }
        else {
          //Correct statement is minimum of 3 char long
          if(serialResults.length()<3) {
            serialResults="";
            System.out.println("Bad serial packet length, tossing.");
          }
          //Correct statement starts with S
          else if(serialResults.charAt(0) != 'S') {
            serialResults="";
            System.out.println("Bad serial packet start char, tossing.");
          }
          else {
              
            sensor = Character.getNumericValue(serialResults.charAt(1));
          
            if(serialResults.length()>2) {
              distance = Integer.parseInt(serialResults.substring(2));

              if(sensor==1) distance1 = distance;
              if(sensor==2) distance2 = distance;
            }
          
            serialResults="";
            distance = distance1-distance2;   
            
            Long refreshTime = System.currentTimeMillis() - distanceRefresh;
            distanceRefresh = System.currentTimeMillis();
            
            SmartDashboard.putNumber("Sensor #1 (mm)", distance1);
            SmartDashboard.putNumber("Sensor #2 (mm)", distance2);
            SmartDashboard.putNumber("Sensor diff (mm)", distance);
            long hertz=0;
            if (refreshTime>0) hertz = 1000/refreshTime;
            SmartDashboard.putNumber("Refresh Rate (Hz)", (int)hertz);
            
            
            

            //System.out.println("Sensor#1: " + distance1 + "mm, Sensor#2: " + distance2 + "mm, Diff=" + distance + "mm, Refresh:" + refresh + "Hz");
          }
        }
      
    }


  }

}
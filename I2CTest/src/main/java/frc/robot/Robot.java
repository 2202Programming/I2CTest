/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.I2C.Port;
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

  private I2C I2CBus;
/*
  private int tof_1_address = 82;
  private int RESULT_ADDRESS = 150;
  private int SYSTEM_MODE_START_ADDRESS = 135;
  private int START_COMMAND = 64;
  private int STOP_COMMAND = 0;
  private int SYSTEM_ID_ADDRESS = 271;
  */

  private int tof_1_address = 0x52;
  private int RESULT_ADDRESS = 0x0096;
  private int SYSTEM_MODE_START_ADDRESS = 0x0087;
  private int START_COMMAND = 0x40;
  private int STOP_COMMAND = 0x00;
  private int SYSTEM_ID_ADDRESS = 0x10F;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    
    //Initialize I2C for TOF1 and perform reality checks
    I2CBus = new I2C(Port.kOnboard, tof_1_address);
    if (I2CBus == null) System.out.println("TOF1 failed init - Null I2C.");
    else System.out.println("TOF1 passed init.");
    if (!I2CBus.addressOnly()) System.out.println("TOF1 failed init - address ping fail");
    else System.out.println("TOF1 passed address ping.");
    if (readID(I2CBus) == 61100) System.out.println("TOF1 passed ID check.");
    else System.out.println("TOF1 Chip ID failed (should be 61100) = " + readID(I2CBus));

  }

  @Override
  public void teleopInit() {

    I2C testBus;
    for(int i=0; i < 100; i++) {
      testBus = new I2C(Port.kOnboard, i);
      System.out.println("Address Check at " + i + ": " + testBus.addressOnly());
    }

    enableTOF(I2CBus);
  }

  @Override
  public void disabledInit() {
    disableTOF(I2CBus);
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
    Short results = readTOF(I2CBus);
    SmartDashboard.putNumber("TOF 1 Distance", results);
    if (results > 0) System.out.println("Distance = " + results);
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public void operatorControl(){
  }

  public Short readTOF(I2C bus) {
    
    byte[] dataBuffer = new byte[2];
    ByteBuffer compBuffer = ByteBuffer.wrap(dataBuffer);

    //Read in two-byte integer 
    bus.read(RESULT_ADDRESS, 2, dataBuffer);

    //convert to short
    compBuffer.order(ByteOrder.BIG_ENDIAN);

    return compBuffer.getShort();

  }

  public void enableTOF(I2C bus) {

    bus.write(SYSTEM_MODE_START_ADDRESS, START_COMMAND);
    System.out.println("TOF Enabled.");
  }

  public void disableTOF(I2C bus) {

    bus.write(SYSTEM_MODE_START_ADDRESS, STOP_COMMAND);
    System.out.println("TOF Disabled.");
  }


  public Short readID(I2C bus) {
    
    byte[] dataBuffer = new byte[2];
    ByteBuffer compBuffer = ByteBuffer.wrap(dataBuffer);

    //read two-byte integer, convert to short
    bus.read(SYSTEM_ID_ADDRESS, 2, dataBuffer);
    compBuffer.order(ByteOrder.BIG_ENDIAN);
          
    return compBuffer.getShort();
  }

}

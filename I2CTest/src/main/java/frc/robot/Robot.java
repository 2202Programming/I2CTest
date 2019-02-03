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

  private int tof_1_address = 41;
  private int RESULT_ADDRESS = 150;
  private int SYSTEM_MODE_START_ADDRESS = 135;
  private int START_COMMAND = 64;
  private int STOP_COMMAND = 0;
  private int SYSTEM_ID_ADDRESS = 271;
  

  //private int tof_1_address = 0x29;
  //private int RESULT_ADDRESS = 0x0096;
  //private int SYSTEM_MODE_START_ADDRESS = 0x0087;
  //private int START_COMMAND = 0x40;
  //private int STOP_COMMAND = 0x00;
  //private int SYSTEM_ID_ADDRESS = 0x010F;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */

  private Short[] SVL51L1X_DEFAULT_CONFIGURATION = {
    0x00, /* 0x2d : set bit 2 and 5 to 1 for fast plus mode (1MHz I2C), else don't touch */
    0x00, /* 0x2e : bit 0 if I2C pulled up at 1.8V, else set bit 0 to 1 (pull up at AVDD) */
    0x00, /* 0x2f : bit 0 if GPIO pulled up at 1.8V, else set bit 0 to 1 (pull up at AVDD) */
    0x01, /* 0x30 : set bit 4 to 0 for active high interrupt and 1 for active low (bits 3:0 must be 0x1), use SetInterruptPolarity() */
    0x02, /* 0x31 : bit 1 = interrupt depending on the polarity, use CheckForDataReady() */
    0x00, /* 0x32 : not user-modifiable */
    0x02, /* 0x33 : not user-modifiable */
    0x08, /* 0x34 : not user-modifiable */
    0x00, /* 0x35 : not user-modifiable */
    0x08, /* 0x36 : not user-modifiable */
    0x10, /* 0x37 : not user-modifiable */
    0x01, /* 0x38 : not user-modifiable */
    0x01, /* 0x39 : not user-modifiable */
    0x00, /* 0x3a : not user-modifiable */
    0x00, /* 0x3b : not user-modifiable */
    0x00, /* 0x3c : not user-modifiable */
    0x00, /* 0x3d : not user-modifiable */
    0xff, /* 0x3e : not user-modifiable */
    0x00, /* 0x3f : not user-modifiable */
    0x0F, /* 0x40 : not user-modifiable */
    0x00, /* 0x41 : not user-modifiable */
    0x00, /* 0x42 : not user-modifiable */
    0x00, /* 0x43 : not user-modifiable */
    0x00, /* 0x44 : not user-modifiable */
    0x00, /* 0x45 : not user-modifiable */
    0x20, /* 0x46 : interrupt configuration 0->level low detection, 1-> level high, 2-> Out of window, 3->In window, 0x20-> New sample ready , TBC */
    0x0b, /* 0x47 : not user-modifiable */
    0x00, /* 0x48 : not user-modifiable */
    0x00, /* 0x49 : not user-modifiable */
    0x02, /* 0x4a : not user-modifiable */
    0x0a, /* 0x4b : not user-modifiable */
    0x21, /* 0x4c : not user-modifiable */
    0x00, /* 0x4d : not user-modifiable */
    0x00, /* 0x4e : not user-modifiable */
    0x05, /* 0x4f : not user-modifiable */
    0x00, /* 0x50 : not user-modifiable */
    0x00, /* 0x51 : not user-modifiable */
    0x00, /* 0x52 : not user-modifiable */
    0x00, /* 0x53 : not user-modifiable */
    0xc8, /* 0x54 : not user-modifiable */
    0x00, /* 0x55 : not user-modifiable */
    0x00, /* 0x56 : not user-modifiable */
    0x38, /* 0x57 : not user-modifiable */
    0xff, /* 0x58 : not user-modifiable */
    0x01, /* 0x59 : not user-modifiable */
    0x00, /* 0x5a : not user-modifiable */
    0x08, /* 0x5b : not user-modifiable */
    0x00, /* 0x5c : not user-modifiable */
    0x00, /* 0x5d : not user-modifiable */
    0x01, /* 0x5e : not user-modifiable */
    0xdb, /* 0x5f : not user-modifiable */
    0x0f, /* 0x60 : not user-modifiable */
    0x01, /* 0x61 : not user-modifiable */
    0xf1, /* 0x62 : not user-modifiable */
    0x0d, /* 0x63 : not user-modifiable */
    0x01, /* 0x64 : Sigma threshold MSB (mm in 14.2 format for MSB+LSB), use SetSigmaThreshold(), default value 90 mm  */
    0x68, /* 0x65 : Sigma threshold LSB */
    0x00, /* 0x66 : Min count Rate MSB (MCPS in 9.7 format for MSB+LSB), use SetSignalThreshold() */
    0x80, /* 0x67 : Min count Rate LSB */
    0x08, /* 0x68 : not user-modifiable */
    0xb8, /* 0x69 : not user-modifiable */
    0x00, /* 0x6a : not user-modifiable */
    0x00, /* 0x6b : not user-modifiable */
    0x00, /* 0x6c : Intermeasurement period MSB, 32 bits register, use SetIntermeasurementInMs() */
    0x00, /* 0x6d : Intermeasurement period */
    0x0f, /* 0x6e : Intermeasurement period */
    0x89, /* 0x6f : Intermeasurement period LSB */
    0x00, /* 0x70 : not user-modifiable */
    0x00, /* 0x71 : not user-modifiable */
    0x00, /* 0x72 : distance threshold high MSB (in mm, MSB+LSB), use SetD:tanceThreshold() */
    0x00, /* 0x73 : distance threshold high LSB */
    0x00, /* 0x74 : distance threshold low MSB ( in mm, MSB+LSB), use SetD:tanceThreshold() */
    0x00, /* 0x75 : distance threshold low LSB */
    0x00, /* 0x76 : not user-modifiable */
    0x01, /* 0x77 : not user-modifiable */
    0x0f, /* 0x78 : not user-modifiable */
    0x0d, /* 0x79 : not user-modifiable */
    0x0e, /* 0x7a : not user-modifiable */
    0x0e, /* 0x7b : not user-modifiable */
    0x00, /* 0x7c : not user-modifiable */
    0x00, /* 0x7d : not user-modifiable */
    0x02, /* 0x7e : not user-modifiable */
    0xc7, /* 0x7f : ROI center, use SetROI() */
    0xff, /* 0x80 : XY ROI (X=Width, Y=Height), use SetROI() */
    0x9B, /* 0x81 : not user-modifiable */
    0x00, /* 0x82 : not user-modifiable */
    0x00, /* 0x83 : not user-modifiable */
    0x00, /* 0x84 : not user-modifiable */
    0x01, /* 0x85 : not user-modifiable */
    0x00, /* 0x86 : clear interrupt, use ClearInterrupt() */
    0x00  /* 0x87 : start ranging, use StartRanging() or StopRanging(), If you want an automatic start after VL53L1X_init() call, put 0x40 in location 0x87 */
    };

public void sensorInit() {

  I2C bus = new I2C(I2C.Port.kOnboard, 41);

  byte[] dataBuffer = new byte[1];
  byte[] dataBufferPre = new byte[1];
  Short Addr = 0x00;
  

	for (Addr = 0x2D; Addr <= 0x87; Addr++){
    bus.read(Addr, 1, dataBufferPre);
    bus.write(Addr, SVL51L1X_DEFAULT_CONFIGURATION[Addr - 0x2D]);
    bus.read(Addr, 1, dataBuffer);

    System.out.println("Address:" + Addr + " wrote:" + SVL51L1X_DEFAULT_CONFIGURATION[Addr - 0x2D] + " [Pre->Post]:" + dataBufferPre[0] + "->" + dataBuffer[0]);  
  }
    
  return;
}


  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    

   
    //Initialize I2C for TOF1 and perform reality checks
    //  I2CBus = new I2C(I2C.Port.kOnboard, tof_1_address);
    I2CBus = new I2C(I2C.Port.kMXP, 41);
    //I2CBus = new I2C(I2C.Port.kMXP, 82);
    
    byte[] dataBuffer = new byte[1];
    I2CBus.read(8, 1, dataBuffer);

    System.out.println("WHOAMI: " + dataBuffer[0]);




      //sensorInit();
      //enableTOF(I2CBus);

     /*
      for (int y=0; y<1000;y++){
      I2CBus.read(150, 1, dataBuffer);
      System.out.println(dataBuffer[0]);
      }
    */



    
    

    /*I2CBus = new I2C(I2C.Port.kOnboard, tof_1_address);

    if (I2CBus == null) System.out.println("TOF1 failed init - Null I2C.");
    else System.out.println("TOF1 passed init.");
    if (!I2CBus.addressOnly()) System.out.println("TOF1 failed init - address ping fail");
    else System.out.println("TOF1 passed address ping.");
    if (readID(I2CBus) == 61100) System.out.println("TOF1 passed ID check.");
    else System.out.println("TOF1 Chip ID failed (should be 61100) = " + readID(I2CBus));
    */

  }

  @Override
  public void teleopInit() {

    /*
    I2C testBus;
    for(int i=0; i < 100; i++) {
      testBus = new I2C(I2C.Port.kOnboard, i);
      System.out.println("Address Check at " + i + ": " + testBus.addressOnly());
    }
    */

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

    readID(bus);

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

    byte[] test = new byte[1];
    test[0]=0;

    bus.read(SYSTEM_ID_ADDRESS, 1, test);
    System.out.print(", " + Integer.toBinaryString((test[0] & 0xFF) + 0x100).substring(1));
    
    return compBuffer.getShort();
  }

}

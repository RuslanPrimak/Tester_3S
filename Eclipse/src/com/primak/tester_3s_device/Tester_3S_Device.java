package com.primak.tester_3s_device;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import com.primak.sound_alerter.ErrorSoundPlayer;

/**
 * @author ruslan_2
 *
 */
/**
 * @author ruslan_2
 *
 */
/**
 * @author ruslan_2
 *
 */
public class Tester_3S_Device {
	/* Ideas:
	 * It makes sense to read rules of behavior from file in next manner:
	 * 
	 * >command>action:parameter:parameter and so on. It works like: 
	 * "If command is received then perform action with parameters". 
	 * Parameters can be empty and action can have different amount of parameters
	 * 
	 * >token>action:parameter:parameter;action:parameter:parameter - there another
	 * version of behavior when on receiving token program will perform two actions
	 * with different parameters. Like previous example there are can be different
	 * amount of commands and parameters too.
	 * 
	 * */

	private final SerialPort serialPort;
	private final ActionPerformer actionsList;
	private final UserCmdReader ucReader;
	private final DeviceResponseParser respParser;
		
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Tester_3S_Device tester = new Tester_3S_Device();
		tester.setActions();
		tester.test();
		//System.out.println(SerialNumberLogger.generateDefaultName());
		/*try {
			SerialNumberLogger snLogger = new SerialNumberLogger(SerialNumberLogger.generateDefaultName());
			snLogger.writeSerialSetting("hello_1");
			snLogger.writeSerialSetting("hello_2");
			snLogger.writeSerialSetting("hello_3");
			snLogger.writeSerialSetting("hello_4");
			snLogger.writeSerialSetting("hello_5");
			snLogger.writeSerialSetting("hello_6");
			snLogger.writeSerialSetting("hello_7");
			snLogger.writeSerialSetting("hello_8");
			snLogger.writeSerialSetting("hello_9");
			snLogger.writeSerialSetting("hello_10");
			snLogger.writeSerialSetting("hello_11");
			snLogger.writeSerialSetting("hello_12");
			snLogger.writeSerialSetting("hello_13");
			snLogger.writeSerialSetting("hello_14");
			snLogger.writeSerialSetting("hello_15");
			snLogger.writeSerialSetting("hello_16");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	/**
	 * 
	 */
	private Tester_3S_Device() {
		actionsList = new ActionPerformer();
		serialPort = new SerialPort("COM28");
		respParser = new DeviceResponseParser(actionsList);
		ucReader = new UserCmdReader(actionsList);
	}
	
	
	/**
	 * @author ruslan_2
	 *
	 */
	private class PortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    // print response to stdout
                    String data = serialPort.readString(event.getEventValue());
                    System.out.print(data);
                    respParser.parse(data);
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }
    }
		
	
	/**
	 * 
	 */
	private void setActions() {				
		// add the most important action - how to stop our program :)
		actionsList.put(TestAction.AC_EXIT, new TestAction(){
			public boolean perform(String[] args) {
				ucReader.stop();
				try {
					if (serialPort.isOpened())					
						serialPort.closePort();					
				} catch (SerialPortException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
		});
		
		// add action for start testing
		actionsList.put(TestAction.AC_RUN, new TestAction(){
			public boolean perform(String[] args) {
				final int pause;
				
				if ((args != null) && (args.length > 0)) {
					pause = Integer.parseInt(args[0]);
				} else 
				{
					pause = 0;
				}
				
				// we need to take a break before running the test
				class PostponedRun implements Runnable{					
					
					PostponedRun() {						
						Thread t = new Thread(this);
						t.start();
					}
					
					public void run() {
						try {
							if (pause > 0) {
								Thread.sleep(pause);
							}
							
							String cmd = "\r\nat+test=3\r\n";
							if (serialPort.isOpened()) {
								try {
									serialPort.writeString(cmd);						
									System.out.println(cmd);
								} catch (SerialPortException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}								
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
									
				new PostponedRun();		
				return true;
			}
		});
		
		// add action for user's confirmation
		actionsList.put(TestAction.AC_CONFIRM, new TestAction(){
			@Override
			public boolean perform(String[] args) {
				BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
				
				System.out.println("Confirm " + args[0] + " action y/n?");
				String s;
				try {
					if ((s = stdin.readLine()) != null) {
						return s.equals("y");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
		});
		
		// add action for resolving GPS time
		actionsList.put(TestAction.AC_GPS, new TestAction(){
			public boolean perform(String[] args) {
				// advanced version with waiting correct gps time
				// *** not implemented yet
				
				// previous (simple) version
				String cmd = "\r\nat+time?\r\n";
				if (serialPort.isOpened()) {
					try {
						serialPort.writeString(cmd);						
						System.out.println(cmd);
					} catch (SerialPortException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				} else {
					return false;
				}				
			}
		});
		
		// add action for setting the serial number
		actionsList.put(TestAction.AC_SET_SN, new TestAction(){
			private final ErrorSoundPlayer eSound;
			private final Pattern ptrnSN;
			private final SerialNumberController cntrlSN; 
			
			// construction section
			{
				eSound = new ErrorSoundPlayer();
				ptrnSN = Pattern.compile("0*(\\d++)");
				cntrlSN = SerialNumberController.getSerialNumController();
			}
			
			public boolean perform(String[] args) {
				// this command will be performed if setting string is passed via args[0]
				if ((args == null) || (args.length == 0)) {
					eSound.play();
					System.out.println("AC_SET_SN expects parameter");
					return false;
				}
				
				// before setting serial it must be checked				
				Matcher m = ptrnSN.matcher(args[0]);
				if (m.find()) {
					int serial = Integer.valueOf(m.group(1));
					if (!cntrlSN.checkSerialNumber(serial)) {
						eSound.play();
						System.out.println("checkSerialNumber failed = " + serial);
						return false;
					} 
				} else {
					return false;
				}
				
				// after checking serial can be written to device
				String cmd = "\r\n" + args[0] + "\r\n";
				if (serialPort.isOpened()) {
					try {
						serialPort.writeString(cmd);						
						System.out.println(cmd);
					} catch (SerialPortException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				} else {
					return false;
				}				
			}
		});
		
		// Add action for logging the setting of serial number to device
		try {
			actionsList.put(TestAction.AC_LOG_SN, new TestAction(){
				private final ErrorSoundPlayer eSound;
				private final Pattern ptrnSN;
				private final SerialNumberLogger logSN;
				
				// construction section
				{
					eSound = new ErrorSoundPlayer();
					ptrnSN = Pattern.compile("0*(\\d++)(\\D*)(\\d++)");					
					logSN = SerialNumberLogger.getSerialNumberLogger();
				}
				
				public boolean perform(String[] args) {
					
					// this action demands the setting answer in parameter
					if ((args == null) || (args.length == 0)) {
						eSound.play();
						System.out.println("AC_LOG_SN expects parameter");
						return false;
					}
					
					// check if IMEI can be obtained from setting answer 
					Matcher m = ptrnSN.matcher(args[0]);
					if (!m.find() || m.group(3).isEmpty())  {						
						eSound.play();
						return false;
					}
					
					logSN.writeSerialSetting(args[0]);
					return true;
				}
			});
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// add action for clear device
		actionsList.put(TestAction.AC_CLEAR, new TestAction(){
			public boolean perform(String[] args) {
				String cmd = "\r\nat+tclear\r\n";
				if (serialPort.isOpened()) {
					try {
						serialPort.writeString(cmd);						
						System.out.println(cmd);
					} catch (SerialPortException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				} else {
					return false;
				}
			}
		});
		
		// add action for power off device
		actionsList.put(TestAction.AC_OFF, new TestAction(){
			public boolean perform(String[] args) {
				String cmd = "\r\nat+toff=1\r\n";
				if (serialPort.isOpened()) {
					try {
						serialPort.writeString(cmd);						
						System.out.println(cmd);
					} catch (SerialPortException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				} else {
					return false;
				}
			}
		});
		
		// add action for initialization current serial number 
		actionsList.put(TestAction.AC_INIT_SN, new TestAction(){
			private final ErrorSoundPlayer eSound;
			private final SerialNumberController cntrlSN;
			private final Pattern ptrnSN;
			
			// construction section
			{
				eSound = new ErrorSoundPlayer();
				cntrlSN = SerialNumberController.getSerialNumController();
				ptrnSN = Pattern.compile("(\\d++)");
			}
			
			public boolean perform(String[] args) {
				// this action demands the initialization value in parameter
				if ((args == null) || (args.length == 0)) {
					eSound.play();
					System.out.println("AC_INIT_SN expects parameter");
					return false;
				}
				
				// extract serial and pass it				
				Matcher m = ptrnSN.matcher(args[0]);
				if (m.find()) {
					int serial = Integer.valueOf(m.group(1));
					cntrlSN.setCurrentSerialNumber(serial);					
				} else {
					eSound.play();
					return false;
				}
				
				return true;
			}
		});
	}
	
	/**
	 * 
	 */
	private void test() {
		try {
            //Открываем порт
            serialPort.openPort();
            //Выставляем параметры
            serialPort.setParams(SerialPort.BAUDRATE_115200,
                                 SerialPort.DATABITS_8,
                                 SerialPort.STOPBITS_1,
                                 SerialPort.PARITY_NONE);
            //Включаем аппаратное управление потоком
            /*serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
                                          SerialPort.FLOWCONTROL_RTSCTS_OUT);*/
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            
            //Устанавливаем ивент лисенер и маску
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
        }
        catch (SerialPortException ex) {
        	if (ex.getExceptionType().equals(SerialPortException.TYPE_PORT_BUSY)) {
        		System.out.println("Port is busy");
        		new ErrorSoundPlayer().play();        		
        	}        	            
        }
        
        if (serialPort.isOpened()) {
        	System.out.println("Com port has been opened - waiting for device...");
        	
        } else {
        	
        }
	}

}

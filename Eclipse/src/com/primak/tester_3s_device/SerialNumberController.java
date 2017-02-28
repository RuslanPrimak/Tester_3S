package com.primak.tester_3s_device;

/**
 * @author ruslan_2
 *
 */
public class SerialNumberController {
	private int serialNumber = 0;
	private static SerialNumberController instance = null;
	
	private SerialNumberController() {
		
	}
	
	/**
	 * @return the singleton instance of the class 
	 */
	public static SerialNumberController getSerialNumController() {
		if (instance == null) {
			instance = new SerialNumberController();
		}
		
		return instance;
	}
	
	/**
	 * @param num - change current serial number
	 */
	public void setCurrentSerialNumber(int num) {
		serialNumber = num;
		System.out.println("Current SN=" + serialNumber);
	}
	
	/**
	 * @return current serial number with increment
	 */
	public int getSerialNumber() {
		return serialNumber;
	}
	
	/**
	 * @return serial number in formatted string 
	 */
	public String getFmtSerialNumber() {
		return String.format("%06d", getSerialNumber());
	}
	
	/**
	 * @param num - check if proposed serial is correct. Automatically increment serial
	 * for next getting or checking
	 * @return true if proposed serial is correct
	 */
	public boolean checkSerialNumber(int num) {
		if (num == serialNumber) {
			serialNumber++;
			return true;
		}
		return false;
	}
}

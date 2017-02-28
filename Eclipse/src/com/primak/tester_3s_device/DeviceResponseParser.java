package com.primak.tester_3s_device;

import java.util.regex.Pattern;

import com.primak.sound_alerter.ErrorSoundPlayer;
import com.primak.sound_alerter.SuccessSoundPlayer;
import com.primak.sound_alerter.WarningSoundPlayer;

public class DeviceResponseParser {
	/**
	 * DeviceResponseParser
	 * @param s
	 * @return
	 */
	private static Pattern testReboot = Pattern.compile("(?s).*REBOOT!.*");
	private static Pattern testPower = Pattern.compile("(?s).*DISCONNECT PWR.*");
	private static Pattern testGPSError = Pattern.compile("(?s).*GPS TIME ERROR.*");
	private static Pattern testTestError = Pattern.compile("(?s).*Complete with errors.*");
	private static Pattern testTestSuccess = Pattern.compile("(?s).*TEST: Complete.*");
	private static Pattern testSetSN = Pattern.compile("(?s).*TSNSET: SN: .*");
	private static Pattern testClear = Pattern.compile("(?s).*TCLEAR: COMPLETE.*");
	private static Pattern testTurnOff = Pattern.compile("(?s).*TOFF: EXT PWR.*");
	
	private final WarningSoundPlayer wSound;
	private final SuccessSoundPlayer sSound;
	private final ErrorSoundPlayer eSound;
	
	private final ActionPerformer actionList;
	
	private final SerialNumberController ctrlSN;
	
	/**
	 * @param actionList
	 */
	public DeviceResponseParser(ActionPerformer actionList) {
		this.actionList = actionList;
		wSound = new WarningSoundPlayer();
		sSound = new SuccessSoundPlayer();
		eSound = new ErrorSoundPlayer();
		ctrlSN = SerialNumberController.getSerialNumController();
	}
	
	/**
	 * @param s
	 * @return
	 */
	public boolean parse(String s) {
		if (testReboot.matcher(s).matches()) {
			// if device reboots we will start a new test
			sSound.play();
			// Automotive action need to take a pause after reboot 
			return actionList.perform(TestAction.AC_RUN, new String[]{"3000"});
		} else if (testPower.matcher(s).matches()) {
			// the case when user has to react and turn off/on power and control
			// the consumption level
			wSound.play();
			return true;
		} else if (testGPSError.matcher(s).matches()){
			// TODO - automation of obtaining correct GPS time or informing about
			// impossibility of doing it
			return false;
		} else if (testTestError.matcher(s).matches()){
			// The test failed - inform user with error sound
			eSound.play();
			return true;
		} else if (testTestSuccess.matcher(s).matches()){ 
			// The test succeed - inform user with success sound
			sSound.play();
			// And write serial to device
			return actionList.perform(TestAction.AC_SET_SN, 
				new String[]{"at+tsnset=" + ctrlSN.getSerialNumber()});
		} else if (testSetSN.matcher(s).matches()) {			
			// the setting of the serial number should be logged  
			if (actionList.perform(TestAction.AC_LOG_SN, new String[]{s})) 
				// after log succeed - run vanishing the device
				return actionList.perform(TestAction.AC_CLEAR);   
			else
				return false;
		} else if (testClear.matcher(s).matches()) {
			// After clearing the device we should turn off it
			sSound.play();
			return actionList.perform(TestAction.AC_OFF);
		} else if (testTurnOff.matcher(s).matches()){ 
			// If user forget to switch off the source of main power
			// it will be impossible to turn the device in sleep mode
			wSound.play();
			return true;
		} else {
			//actionList.perform(s);			
		}		
		return false;
	}
}

package com.primak.tester_3s_device;

public abstract class TestAction {
	/** TestAction is the abstract class which defines pattern of action
	 * which can be performed during testing process and reflects not only test
	 * action but any action like exit which can be performed by the program. 
	 * */
	
	// predefned names of actions
	public final static String AC_EXIT = "exit";
	public final static String AC_RUN = "run";
	public final static String AC_GPS = "gps";
	public final static String AC_CONFIRM = "confirm";
	public final static String AC_INIT_SN = "init_sn";
	public final static String AC_SET_SN = "set_sn";
	public final static String AC_LOG_SN = "log_sn";
	public final static String AC_CLEAR = "clear";		
	public final static String AC_OFF = "off";
	
	// What it should do
	public abstract boolean perform(String[] args);
}

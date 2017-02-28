package com.primak.tester_3s_device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * @author ruslan_2
 *
 */
public class UserCmdReader implements Runnable {
	/**
	 * UserCmdReader supports performing of user commands which are obtained
	 * through Standard input. All recognized commands are translated to
	 * ActionPerformer
	 * */

	private static Pattern testSetSN = Pattern.compile("(?si).*at+tsnset=.*");
	private static Pattern testInitSN = Pattern.compile("(?si).*" + TestAction.AC_INIT_SN + ".*");
	
	private final Thread t;
	private boolean isEnabled = true;
	private final ActionPerformer actionList;

	/**
	 * @param actionList
	 */
	public UserCmdReader(ActionPerformer actionList) {
		this.actionList = actionList;
		t = new Thread(this);
		t.start();
	}

	/**
	 * 
	 */
	public void stop() {
		isEnabled = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		System.out.println("Waiting for users commands...");
		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));
		try {
			String s;
			while (isEnabled) {
				s = stdin.readLine();
				if (s != null) {
					if (testSetSN.matcher(s).matches()) {						
						actionList.perform(TestAction.AC_SET_SN, new String[]{s});
					} else if (testInitSN.matcher(s).matches()) {
						actionList.perform(TestAction.AC_INIT_SN, new String[]{s});
					} else {
						actionList.perform(s);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Listening for user's commands has been stopped!");
	}
}

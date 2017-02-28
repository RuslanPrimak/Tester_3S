package com.primak.tester_3s_device;

import java.util.HashMap;

public class ActionPerformer extends HashMap<String, TestAction> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3111125809120319960L;

	/**
	 * perform(String name) - run appropriate action looked by name 
	 */	
	public boolean perform(String name) {
		TestAction ac = this.get(name);
		if (ac != null){			
			return ac.perform(null);
		} else {
			return false;
		}
	}
	
	public boolean perform(String name, String[] args) {
		TestAction ac = this.get(name);
		if (ac != null){			
			return ac.perform(args);
		} else {
			return false;
		}
	}
}

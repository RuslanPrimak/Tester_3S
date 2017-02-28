package com.primak.tester_3s_device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SerialWriterAction extends TestAction {
	private int serial;

	public SerialWriterAction(int serial) {
		this.serial = serial;
	}
	
	@Override
	public boolean perform(String[] args) {
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Write serial " + serial + " y/n?");
		String s;
		try {
			if ((s = stdin.readLine()) != null) {
				if (s.equals("y")) {
					
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}

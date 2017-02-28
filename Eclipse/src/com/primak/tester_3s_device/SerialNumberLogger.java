package com.primak.tester_3s_device;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SerialNumberLogger {
	private static SerialNumberLogger instance = null;
	
	private boolean logFinished = false;
	private String fileName;
	private FileOutputStream fos;
	private PrintStream ps;
	private final Pattern p;
	
	private SerialNumberLogger(String fileName) throws FileNotFoundException {
		this.fileName = fileName;
		fos = new FileOutputStream(this.fileName);
		ps = new PrintStream(fos, true);
		p = Pattern.compile("\\+TSNSET: SN: \\d{6}+ IMEI: \\d{15}+");
	}
	
	private static String generateDefaultName() {		;
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		dateFormatter.applyPattern("yyyyMMdd_HHmmss");
		return dateFormatter.format(new Date()) + ".snl";
	}
	
	public static SerialNumberLogger getSerialNumberLogger() throws FileNotFoundException {
		if (instance == null) {
			instance = new SerialNumberLogger(SerialNumberLogger.generateDefaultName());
		}
		return instance;
	}
	
	public void closeLogger() throws IOException {
		if (!logFinished) {
			ps.close();
			fos.close();
			logFinished = true;
		}
	}
	
	public boolean writeSerialSetting(String sn) {		
		if (!logFinished) {
			// test if passed string relates to pattern
			Matcher m = p.matcher(sn);
			if (m.find()) {
				ps.println(m.group(0));
				return true;
			} else {
				System.out.println("Wrong setting string for logging!");
			}
		}
		return false;
	}
	
	protected void finilize() throws IOException {
		closeLogger();
	}
}

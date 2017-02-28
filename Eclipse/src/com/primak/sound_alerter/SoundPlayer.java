package com.primak.sound_alerter;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

class SoundPlayer implements Runnable {
	private int delay;
	private String name;
	
	protected SoundPlayer(String name, int delay) {
		this.delay = delay;
		this.name = name;
	}
	
	public void run() {
		URL url = this.getClass().getClassLoader().getResource(name);
		AudioClip clip = Applet.newAudioClip(url);
		clip.play();
		if (delay > 0) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//System.out.println("Playing of " + name + " during " + delay + " msec complete!");
	}
	
	public void play() {
		Thread t = new Thread(this);
		t.start();
/*		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}

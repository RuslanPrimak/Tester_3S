package com.primak.sound_alerter;


public class TestClipPlaying {

	public static void main(String[] args) {		
		new ErrorSoundPlayer().play();
		new SuccessSoundPlayer().play();
		new WarningSoundPlayer().play();
	}
}

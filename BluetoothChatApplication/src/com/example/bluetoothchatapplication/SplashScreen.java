package com.example.bluetoothchatapplication;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SplashScreen extends Activity {

	Splash splash;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		 splash=new Splash();
		 splash.start();
	}
	
	//Making the page wait for 3 seconds.....
	class Splash extends Thread{
		public void run(){
			try {
				Thread.sleep(3000);
				Intent first_intent=new Intent(SplashScreen.this,First.class);
				startActivity(first_intent);
				finish(); //The Activity gets killed and will not appear again.
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

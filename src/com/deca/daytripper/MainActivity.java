package com.deca.daytripper;

import android.os.Bundle;

import android.app.TabActivity;
import android.content.Intent;




import android.widget.TabHost;


//hello
@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		final TabHost tabHost = getTabHost();
		tabHost.addTab(
				tabHost.newTabSpec("Map")
				.setIndicator("Map")
				.setContent(new Intent(this, mapActivity.class)));
		tabHost.addTab(
				tabHost.newTabSpec("Log")
				.setIndicator("Log")
				.setContent(new Intent(this,LogActivity.class)));
		tabHost.addTab(
				tabHost.newTabSpec("Setting")
				.setIndicator("Setting")
				.setContent(new Intent(this,SettingActivity.class)));
	}	
	
	
	
	
}


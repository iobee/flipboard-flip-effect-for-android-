package com.iobee.activity;

import com.iobee.ui.flipboardviewCamera;
import com.iobee.ui.flipboardviewSrc;

import android.app.Activity;
import android.os.Bundle;

public class flipboardActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(new flipboardviewSrc(this));
	}
	
}

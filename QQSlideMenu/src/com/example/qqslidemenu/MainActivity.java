package com.example.qqslidemenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity {
	private SlidingMenu sm_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        sm_menu = (SlidingMenu) findViewById(R.id.sm_menu);
    }
    
    public void toggleMenu(View view){
    	sm_menu.toggle();
    }

}

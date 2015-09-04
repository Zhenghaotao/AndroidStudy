package com.example.changecolortraceview;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
/**
 * 没有用viewpager
 * @author taotao
 *
 */
public class MainActivity extends Activity {
	
	private ColorTrackView mView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

	private void initView() {
		mView = (ColorTrackView) findViewById(R.id.ctv);
	}
	@SuppressLint("NewApi")
	public void startLeftChange(View view){
		mView.setDirection(0);
		ObjectAnimator.ofFloat(mView, "progress", 0, 1).setDuration(2000).start();
	}
	@SuppressLint("NewApi")
	public void startRightChange(View view){
		mView.setDirection(1);
		ObjectAnimator.ofFloat(mView, "progress", 0, 1).setDuration(2000).start();
	}

    
}

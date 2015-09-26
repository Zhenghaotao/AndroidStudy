package com.example.luckypan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	private ImageView iv_start;
	private LuckyPan lp_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

	private void initListener() {
		iv_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!lp_show.isStart()){
					lp_show.luckyStart(1);
					iv_start.setImageResource(R.drawable.stop);
				} else {
					if(!lp_show.isShouldEnd()){
						lp_show.luckyEnd();
						iv_start.setImageResource(R.drawable.start);
					}
				}
				
			}
		});
	}

	private void initView() {
		lp_show = (LuckyPan) findViewById(R.id.lp_show);
		iv_start = (ImageView) findViewById(R.id.iv_start);
	}
    
}

package com.example.showprogresstextview;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

public class MainActivity extends Activity {
	private int progress = 0;
	private TextView tv_progress;

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv_progress = (TextView) findViewById(R.id.tv_progress);
		// ///////////////////////////////**************************
		
		//规律：一个汉字的显示宽度相当于 4个 空格的显示宽度
		int nowPercent = 75;
        String konggeForHanzi="    ";
        String konggeForShuzi="  ";
        
        String test="";
        if(nowPercent>=1 && nowPercent<=9){
        	test="8%"+konggeForHanzi+konggeForHanzi+konggeForHanzi
	        		+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi;
	      
        }else if(nowPercent>=10 && nowPercent<=99){
        	test=+nowPercent+"%"+konggeForHanzi+konggeForHanzi+konggeForHanzi
        			+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi;
        	
        }else if(nowPercent==100){
        	test=nowPercent+"%"+konggeForHanzi+konggeForHanzi+konggeForHanzi
        			+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi+konggeForHanzi;
        	
        }
        int bstart=0;
        double aa=nowPercent/100.0*(test.length());
		int bb=(int) Math.round(aa);
        Log.i("ggtag", "aa = " +aa + ",,   bb = " + bb);
        SpannableStringBuilder style=new SpannableStringBuilder(test);
        style.setSpan(new BackgroundColorSpan(Color.GREEN),bstart,bb,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
        
        //若写上下面两句话的话，则会显示 “红色”之后显示“蓝色”是同时起作用
        //style.setSpan(new BackgroundColorSpan(Color.RED),1,3,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //style.setSpan(new BackgroundColorSpan(Color.BLUE),4,5,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_progress.setText(style);
        
	}

}

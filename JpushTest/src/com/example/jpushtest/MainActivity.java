package com.example.jpushtest;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        JPushInterface.setAlias(this, "cccc", new TagAliasCallback() {
			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
				Log.i(TAG,arg0 + " :  " +arg1 + "   :   " + arg2.toString());
			}
		});
        Set<String> tags = new HashSet<String>();
        tags.add("科技");
        tags.add("军事");
        tags.add("综艺");
        tags.add("娱乐");
        JPushInterface.setTags(this, tags, new TagAliasCallback() {
			
			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
				Log.i(TAG, "" + arg0 + " :   " + arg1 + "   :   " + arg2.toString());
			}
		});
        
    }


    
}

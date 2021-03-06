package com.example.actservicemsgcon;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.actservicemsgcon.MsgService.MsgBinder;

public class MainActivity extends Activity {
	private MsgService msgService;  
    private int progress = 0;
    private ProgressBar mProgressBar;
    private Button btn_download;
    @Override
    protected void onStart() {
    	super.onStart();
    	  Intent intent = new Intent(this,MsgService.class);
    	  startService(intent);
          bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
      
       
        mProgressBar= (ProgressBar) findViewById(R.id.bar);
        btn_download= (Button) findViewById(R.id.btn_download);
        
        btn_download.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				msgService.startDownload();
				//监听进度  
                listenProgress();  
			}
		});
    }
    /** 
     * 监听进度，每秒钟获取调用MsgService的getProgress()方法来获取进度，更新UI 
     */  
    public void listenProgress(){  
        new Thread(new Runnable() {  
              
            @Override  
            public void run() {  
                while(progress < MsgService.MAX_PROGRESS){  
                    progress = msgService.getProgress();  
                    mProgressBar.setProgress(progress);  
                    try {  
                        Thread.sleep(1000);  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }).start();  
    }  
    
    private ServiceConnection mConnection= new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MsgBinder binder = (MsgBinder) service;
			msgService = binder.getService();
			
		}
	};
	@Override
	public void onDestroy() {
		unbindService(mConnection);
		super.onDestroy();
	}
}

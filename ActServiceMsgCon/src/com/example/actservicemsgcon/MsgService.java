package com.example.actservicemsgcon;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
public class MsgService extends Service {
	
	/**
	 * 进度条的最大值
	 */
	public static final int MAX_PROGRESS = 100;
	private int process = 0;
	/**
	 * 增加 get()方法,供Activity调用
	 * @return 返回下载调用
	 */
	public int getProgress(){
		return process;
	}
	
	public void startDownload(){
		new Thread(new Runnable(){

			@Override
			public void run() {
				while(process <MAX_PROGRESS){
					process += 5;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		}).start();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return new MsgBinder();
	}
	
	
	public class MsgBinder extends Binder{
		/** 
         * 获取当前Service的实例 
         * @return 
         */  
        public MsgService getService(){  
            return MsgService.this;  
        }  
	}
	
	
	
	

}

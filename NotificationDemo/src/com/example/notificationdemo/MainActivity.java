package com.example.notificationdemo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	private int notification_id = 19172439;
	private NotificationManager nm;
	private Button btn_show;
	private Button btn_clear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		initView();
		initListener();

	}

	private void initListener() {
		btn_clear.setOnClickListener(this);
		btn_show.setOnClickListener(this);
	}

	private void initView() {
		btn_show = (Button) findViewById(R.id.btn_show);
		btn_clear = (Button) findViewById(R.id.btn_clear);
	}

	public void showNotification(int icon, String tickertext, String title,
			String content) {
		// Notification管理器
		Notification notification = new Notification(icon, tickertext,
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		// 后面的参数分别是显示在顶部通知栏的小图标，小图标旁的文字（短暂显示，自动消失）系统当前时间（不明白这个有什么用）
		notification.defaults = Notification.DEFAULT_ALL;
		// 这是设置通知是否同时播放声音或振动，声音为Notification.DEFAULT_SOUND
		// 振动为Notification.DEFAULT_VIBRATE;
		// Light为Notification.DEFAULT_LIGHTS，在我的Milestone上好像没什么反应
		// 全部为Notification.DEFAULT_ALL
		// 如果是振动或者全部，必须在AndroidManifest.xml加入振动权限
		Intent intent = new Intent(this,SecondActivity.class);
		PendingIntent pt = PendingIntent.getActivity(this, 0,intent, 0);
		// 点击通知后的动作，这里是转回main 这个Acticity
		notification.setLatestEventInfo(this, title, content, null);
		nm.notify(notification_id, notification);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_show:
			showNotification(R.drawable.ic_launcher,"图标边的文字","标题","内容");
			break;
		case R.id.btn_clear:
			nm.cancel(notification_id);
			break;
		}
	}

}

package com.example.recorder_chat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.recorder_chat.bean.Recorder;
import com.example.recorder_chat.view.AudioRecorderButton;
import com.example.recorder_chat.view.AudioRecorderButton.AudioFInishRecorderListener;

public class MainActivity extends Activity {
	private ListView lv;
	private RecorderAdapter mAdapter;
	private List<Recorder> mDatas = new ArrayList<Recorder>();
	private AudioRecorderButton arb;
	private View mAnimView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initDatas();
		initAdapter();
		initEvent();
	}

	private void initEvent() {
		arb.setOnAudioFInishRecorderListener(new AudioFInishRecorderListener() {

			@Override
			public void onFinish(float seconds, String filePath) {
				Recorder recorder = new Recorder(seconds, filePath);
				mDatas.add(recorder);
				mAdapter.notifyDataSetChanged();
				lv.setSelection(mDatas.size() - 1);
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(mAnimView != null){
					mAnimView.setBackgroundResource(R.drawable.adj);
					mAnimView = null;
				}
				
				// 播放动画
				mAnimView = view.findViewById(R.id.recodrder_anim);
				mAnimView.setBackgroundResource(R.drawable.play_anim);
				AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
				anim.start();
				//播放音频
				MediaManager.playSound(mDatas.get(position).getFilePath(),new MediaPlayer.OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						mAnimView.setBackgroundResource(R.drawable.adj);
					}
				});
			}
		});
	}
	
	@Override
	protected void onPause() {
		MediaManager.pause();
		super.onPause();
	}
	@Override
	protected void onResume() {
		MediaManager.resume();
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		MediaManager.release();
		super.onDestroy();
	}

	private void initAdapter() {
		mAdapter = new RecorderAdapter(this, mDatas);
		lv.setAdapter(mAdapter);
	}

	private void initDatas() {

	}

	private void initView() {
		lv = (ListView) findViewById(R.id.lv);
		arb = (AudioRecorderButton) findViewById(R.id.arb);
	}

}

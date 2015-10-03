package com.example.recorder_chat.view;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.media.MediaRecorder;

public class AudioManager {
	private MediaRecorder mediaRecorder;
	private String mDir;
	private String mCurrentFilePath;

	private static AudioManager mInstance;
	private AudioStateListener mListener;
	private boolean isPrepared;
	
	
	
	public AudioManager(String dir) {
		this.mDir = dir;
	}

	public static AudioManager getInstance(String dir) {
		if (mInstance == null) {
			synchronized (AudioManager.class) {
				if (mInstance == null) {
					mInstance = new AudioManager(dir);
				}
			}
		}
		return mInstance;
	}

	public void prepareAudio() {
		try {
			
			isPrepared = false;
			File dir = new File(mDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String fileName = generateFileName();
			File file = new File(dir, fileName);
			mCurrentFilePath = file.getAbsolutePath();
			mediaRecorder = new MediaRecorder();
			// 设置输出文件
			mediaRecorder.setOutputFile(mCurrentFilePath);
			// 设置MediaRecorder的音频源为麦克风
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置音频的格式
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			// 设置音频的编码为amr
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mediaRecorder.prepare();
			mediaRecorder.start();
			// 准备结束
			isPrepared = true;
			if (mListener != null) {
				mListener.wellPrepared();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 随机生成文件的名称
	 * 
	 * @return
	 */
	private String generateFileName() {
		return UUID.randomUUID().toString() + ".amr";
	}

	public int getVoiceLevel(int maxLevel) {
		if (isPrepared) {
			try {
				// mediaRecorder.getMaxAmplitude() : 1 ~ 32767
				if(mediaRecorder != null){
					return maxLevel * mediaRecorder.getMaxAmplitude() / 32768 + 1;
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
		return 1;
	}

	public void release() {
		mediaRecorder.stop();
		mediaRecorder.release();
		mediaRecorder = null;
	}

	public void cancel() {
		release();
		if(mCurrentFilePath != null){
			File file = new File(mCurrentFilePath);
			if(file.exists()){
				file.delete();
			}
			mCurrentFilePath = null;
		}
	}

	public void setOnAudioStateListener(AudioStateListener mAudioStateListener) {
		this.mListener = mAudioStateListener;
	}

	/**
	 * 回调准备完毕
	 */
	public interface AudioStateListener {
		void wellPrepared();
	}

	public String getCurrentFIlePath() {
		return mCurrentFilePath;
	}

}

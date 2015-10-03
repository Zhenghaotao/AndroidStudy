package com.example.recorder_chat.view;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.recorder_chat.R;
import com.example.recorder_chat.view.AudioManager.AudioStateListener;

public class AudioRecorderButton extends Button implements AudioStateListener {

	private static final int DISTANCE_Y_CANCEL = 3;

	private static final int STATE_NORMAL = 1;
	private static final int STATE_RECORDING = 2;
	private static final int STATE_WANT_TO_CANCEL = 3;

	private int mCurState = STATE_NORMAL;
	// 是否已经开始录音
	private volatile boolean isRecording = false;

	private DialogManager mDialogManager;

	private AudioManager mAudioManager;

	private float mTime;
	// 是否触发longclick
	private boolean mReady;

	public AudioRecorderButton(Context context) {
		this(context, null);
	}

	public AudioRecorderButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDialogManager = new DialogManager(getContext());
		String dir = Environment.getExternalStorageDirectory()
				+ "/recorder_audios";
		mAudioManager = AudioManager.getInstance(dir);
		mAudioManager.setOnAudioStateListener(this);

		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mReady = true;
				mAudioManager.prepareAudio();
				return false;
			}
		});
	}

	/**
	 * 录音完成后的回调
	 */
	public interface AudioFInishRecorderListener {
		void onFinish(float seconds, String filePath);
	}

	private AudioFInishRecorderListener mListener;

	public void setOnAudioFInishRecorderListener(
			AudioFInishRecorderListener listener) {
		this.mListener = listener;
	}

	/**
	 * 获取音量大小的Runnable
	 */
	private Runnable mGetVoiceLevelRunnable = new Runnable() {

		@Override
		public void run() {
			while (isRecording) {
				try {
					Thread.sleep(100);
					mTime += 0.1f;
					mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private static final int MSG_AUDIO_PREPARED = 0x110;
	private static final int MSG_VOICE_CHANGED = 0x111;
	private static final int MSG_DIALOG_DIMISS = 0x112;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_AUDIO_PREPARED:
				// 显示应该在audio end prepared以后
				mDialogManager.showRecordingDialog();
				isRecording = true;
				new Thread(mGetVoiceLevelRunnable).start();

				break;
			case MSG_VOICE_CHANGED:
				mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
				break;
			case MSG_DIALOG_DIMISS:
				mDialogManager.dimissDialog();
				break;
			}

		};
	};

	@Override
	public void wellPrepared() {
		mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			changeState(STATE_RECORDING);
			break;
		case MotionEvent.ACTION_MOVE:
			if (isRecording) {
				if (wantToCancel(x, y)) {
					changeState(STATE_WANT_TO_CANCEL);
				} else {
					changeState(STATE_RECORDING);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (!mReady) {
				reset();
				return super.onTouchEvent(event);
			}
			if (!isRecording || mTime < 0.6f) {
				mDialogManager.tooShort();
				mAudioManager.cancel();
				mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
				return super.onTouchEvent(event);
			} else if (mCurState == STATE_RECORDING) {// 正常录制结束
				mDialogManager.dimissDialog();
				// release
				mAudioManager.release();
				// callbackToAct
				if (mListener != null) {
					mListener.onFinish(mTime,
							mAudioManager.getCurrentFIlePath());
				}

			} else if (mCurState == STATE_WANT_TO_CANCEL) {
				mDialogManager.dimissDialog();
				mAudioManager.cancel();
			}
			reset();
			break;
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 恢复状态及标志位
	 */
	private void reset() {
		isRecording = false;
		changeState(STATE_NORMAL);
		mTime = 0;
		mReady = false;
	}

	/**
	 * 根据x,y的坐标,判断是否想要取消
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean wantToCancel(int x, int y) {
		if (x < 0 || x > getWidth()) {
			return true;
		}
		if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
			return true;
		}
		return false;
	}

	private void changeState(int state) {
		if (mCurState != state) {
			mCurState = state;
			switch (state) {
			case STATE_NORMAL:
				setBackgroundResource(R.drawable.btn_recoder_normal);
				setText(R.string.recoder_normal);
				break;
			case STATE_RECORDING:
				setBackgroundResource(R.drawable.btn_recoder_recoding);
				setText(R.string.recoder_recording);
				if (isRecording) {
					mDialogManager.recording();
				}
				break;
			case STATE_WANT_TO_CANCEL:
				setBackgroundResource(R.drawable.btn_recoder_recoding);
				setText(R.string.recoder_want_cancel);
				mDialogManager.wantToCancel();
				break;
			}
		}
	}

}

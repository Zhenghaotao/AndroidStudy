package com.example.guessmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.guessmusic.R;
import com.example.guessmusic.data.Const;
import com.example.guessmusic.model.IWordButtonClickListener;
import com.example.guessmusic.model.Song;
import com.example.guessmusic.model.WordButton;
import com.example.guessmusic.util.Util;
import com.example.guessmusic.view.MyGridView;

public class MainActivity extends Activity implements IWordButtonClickListener {

	private static final String TAG = "MainActivity";

	// 唱片相关动画
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;

	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;

	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	// Play相关按钮
	private ImageButton mBtnPlayStart;
	private ImageView mViewPan;
	private ImageView mViewPanBar;

	private MyGridView myGridView;

	// 文字容器
	private ArrayList<WordButton> mAllWord;

	// 已选择的容器
	private ArrayList<WordButton> mSelectedWord;

	// 已选择文字UI容器
	private LinearLayout mViewWordsContainer;

	// 标识动画是否正在进行
	private boolean mIsRunning = false;

	// 当前的歌曲
	private Song mCurrentSong;

	// 当前关的索引
	private int mCurrentStageIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 初始化动画
		initAnim();
		// 初始化控件
		initViews();
		// 初始化事件
		initListener();

	}

	/**
	 * 初始化事件
	 */
	private void initListener() {
		mBtnPlayStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Log.i(TAG, "play按钮被点击了");
				handlePlayButton();
			}
		});

		myGridView.setOnWordButtonClickListener(this);
	}

	private void handlePlayButton() {
		if (!mIsRunning) {
			mIsRunning = true;
			mViewPanBar.startAnimation(mBarInAnim);
			mBtnPlayStart.setVisibility(View.INVISIBLE);
			initCurrentStateData();
		} else {

		}
	}

	/**
	 * 初始化控件
	 */
	private void initViews() {
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		mViewPan = (ImageView) findViewById(R.id.iv1);
		mViewPanBar = (ImageView) findViewById(R.id.iv2);
		myGridView = (MyGridView) findViewById(R.id.gridview);
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);
	}

	/**
	 * 初始化动画
	 */
	private void initAnim() {
		mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPanLin = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		mPanAnim.setAnimationListener(new AnimationListener() {// 设置动画监听事件

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {// 动画完成之后
				mViewPanBar.startAnimation(mBarOutAnim);

			}
		});

		mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mBarInLin = new LinearInterpolator();
		mBarInAnim.setFillAfter(true);
		mBarInAnim.setInterpolator(mBarInLin);
		mBarInAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mViewPan.startAnimation(mPanAnim);

			}
		});

		mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
		mBarOutLin = new LinearInterpolator();
		mBarOutAnim.setFillAfter(true);
		mBarOutAnim.setInterpolator(mBarOutLin);

		mBarOutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mIsRunning = false;
				mBtnPlayStart.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	protected void onPause() {
		mViewPan.clearAnimation();
		super.onPause();
	}

	/**
	 * 读取当前关的歌曲信息
	 * 
	 * @param stateIndex
	 */
	private void getStateSongInfo(int stateIndex) {
		mCurrentSong = new Song();
		mCurrentSong
				.setmSongFileName(Const.SONG_INFO[stateIndex][Const.INDEX_FILE_NAME]);
		mCurrentSong
				.setmSongName(Const.SONG_INFO[stateIndex][Const.INDEX_SONG_NAME]);

	}

	/**
	 * 初始化当前关的数据
	 */
	private void initCurrentStateData() {
		if (mCurrentStageIndex >= Const.SONG_INFO.length) {
			Toast.makeText(this, "恭喜你全部通关了", Toast.LENGTH_SHORT).show();
			return;
		}

		// 读取当前关的数据
		getStateSongInfo(mCurrentStageIndex);
		mCurrentStageIndex++;

		// 初始化选择容器
		mSelectedWord = initWordSelect();
		LayoutParams params = new LayoutParams(70, 70);
		mViewWordsContainer.removeAllViews();
		for (int i = 0; i < mSelectedWord.size(); i++) {
			mViewWordsContainer.addView(mSelectedWord.get(i).getmViewButton(),
					params);
		}
		// 获得数据
		mAllWord = initAllWord();

		// 更新数据- MyGridView
		myGridView.updateData(mAllWord);

	}

	/**
	 * 初始化选字数据
	 */
	private ArrayList<WordButton> initAllWord() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		// 获得所有待选文字
		String[] words = generateWords();

		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			WordButton button = new WordButton();

			button.setmWordString(words[i]);

			data.add(button);
		}

		return data;
	}

	/**
	 * 
	 * @return
	 */
	private String[] generateWords() {
		Random random = new Random();

		String[] words = new String[MyGridView.COUNTS_WORDS];

		// 存入歌名
		for (int i = 0; i < mCurrentSong.getmNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}

		// 获取随机文字并存入数组
		for (int i = mCurrentSong.getmNameLength(); i < MyGridView.COUNTS_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}

		// 打乱文字顺序：首先从所有元素中随机选取一个与第一个元素进行交换，
		// 然后在第二个之后选择一个元素与第二个交换，知道最后一个元素。
		// 这样能够确保每个元素在每个位置的概率都是1/n。
		for (int i = MyGridView.COUNTS_WORDS - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);

			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
		}

		return words;
	}

	/**
	 * 生成随机汉字
	 * 
	 * @return
	 */
	private char getRandomChar() {
		String str = "";
		int hightPos;
		int lowPos;

		Random random = new Random();

		hightPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = (161 + Math.abs(random.nextInt(93)));

		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(hightPos)).byteValue();
		b[1] = (Integer.valueOf(lowPos)).byteValue();

		try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return str.charAt(0);
	}

	/**
	 * 初始化已选择文字框
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initWordSelect() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		for (int i = 0; i < mCurrentSong.getmNameLength(); i++) {
			View v = Util.getView(R.layout.self_ui_gridview_item,
					MainActivity.this);
			WordButton holder = new WordButton();
			holder.setmViewButton((Button) v.findViewById(R.id.btn_item));
			holder.getmViewButton().setTextColor(Color.WHITE);
			holder.getmViewButton().setText("");
			holder.setmIsVisiable(false);

			holder.getmViewButton().setBackgroundResource(
					R.drawable.game_wordblank);

			data.add(holder);

		}
		return data;

	}

	@Override
	public void onWordButtonClick(WordButton wordButton) {
	}

}

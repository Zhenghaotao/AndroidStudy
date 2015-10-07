package com.example.guessmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.TextView;
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

	// 答案状态: 正确，错误，不完整
	private final static int STATUS_ANSWER_RIGHT = 1;
	private final static int STATUS_ANSWER_WRONG = 2;
	private final static int STATUS_ANSWER_LCAK = 3;

	private final static int SPASH_TIMES = 6;

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
	private LinearLayout mPassView;

	// 金币显示的控件
	private TextView mViewCurrentCoins;

	// 删除控件
	private ImageButton btn_delete_word;

	// 提示控件
	private ImageButton btn_tip_answer;

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

	// 当前金币数量
	private int mCurrentCoins = Const.TOTAL_COINS;

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

		mPassView = (LinearLayout) findViewById(R.id.pass_view);

		mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
		mViewCurrentCoins.setText(mCurrentCoins + "");

		btn_delete_word = (ImageButton) findViewById(R.id.btn_delete_word);
		btn_tip_answer = (ImageButton) findViewById(R.id.btn_tip_answer);
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
	 * 检查答案
	 * 
	 * @return
	 */
	private int checkTheAnswer() {
		StringBuffer sb = new StringBuffer();
		// 检查长度
		for (int i = 0; i < mSelectedWord.size(); i++) {
			// 如果有空，说明答案不完整
			if (mSelectedWord.get(i).getmWordString().length() == 0) {
				return STATUS_ANSWER_LCAK;
			}
			sb.append(mSelectedWord.get(i).getmWordString());
		}
		// 答案完整，继续检查完整性

		return (sb.toString().equals(mCurrentSong.getmSongName()) ? STATUS_ANSWER_RIGHT
				: STATUS_ANSWER_WRONG);
	}

	/**
	 * 文字闪烁
	 */
	private void sparkTheWords() {
		// 定时器相关
		TimerTask task = new TimerTask() {
			boolean mChange = false;
			int mSparedTimes = 0;

			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (++mSparedTimes > SPASH_TIMES) {
							return;
						}
						// 执行闪烁逻辑: 交替显示红色和白色文字
						for (int i = 0; i < mSelectedWord.size(); i++) {
							mSelectedWord
									.get(i)
									.getmViewButton()
									.setTextColor(
											mChange ? Color.RED : Color.WHITE);
						}
						mChange = !mChange;
					}
				});
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1, 150);
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
		
		handleDeleteWord();
		handleTipAnswer();
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

	private void clearTheAnswer(WordButton wordButton) {
		wordButton.setmWordString("");
		wordButton.getmViewButton().setText("");
		wordButton.setmIsVisiable(false);
		// 设置待选框
		setButtonVisiable(mAllWord.get(wordButton.getmIndex()), View.VISIBLE);
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
			final WordButton holder = new WordButton();
			holder.setmViewButton((Button) v.findViewById(R.id.btn_item));
			holder.getmViewButton().setTextColor(Color.WHITE);
			holder.getmViewButton().setText("");
			holder.setmIsVisiable(false);

			holder.getmViewButton().setBackgroundResource(
					R.drawable.game_wordblank);
			holder.getmViewButton().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					clearTheAnswer(holder);
				}
			});
			data.add(holder);
		}
		return data;
	}

	/**
	 * 设置答案
	 * 
	 * @param wordButton
	 */
	private void setSelectWord(WordButton wordButton) {
		for (int i = 0; i < mSelectedWord.size(); i++) {
			if (mSelectedWord.get(i).getmWordString().length() == 0) {
				// 设置内容及可见性
				mSelectedWord.get(i).getmViewButton()
						.setText(wordButton.getmWordString());
				mSelectedWord.get(i).setmIsVisiable(true);
				mSelectedWord.get(i)
						.setmWordString(wordButton.getmWordString());
				// 记录索引
				mSelectedWord.get(i).setmIndex(wordButton.getmIndex());
				Log.i(TAG, "索引:  " + wordButton.getmIndex());
				// 设置待选框可见性
				setButtonVisiable(wordButton, View.INVISIBLE);
				break;
			}
		}
	}
	

	/**
	 * 设置待选文字框是否可见
	 * 
	 * @param button
	 * @param visibility
	 */
	private void setButtonVisiable(WordButton button, int visibility) {
		button.getmViewButton().setVisibility(visibility);
		button.setmIsVisiable((visibility == View.VISIBLE ? true : false));
	}

	@Override
	public void onWordButtonClick(WordButton wordButton) {
		setSelectWord(wordButton);
		// 对答案状态的检测
		int checkResult = checkTheAnswer();
		switch (checkResult) {
		case STATUS_ANSWER_RIGHT:
			// 答案正确，获取相应的奖励
			handlePassEvent();
			mCurrentStageIndex++;
			// initCurrentStateData();
			break;
		case STATUS_ANSWER_WRONG:
			sparkTheWords();
			// 闪烁
			Toast.makeText(this, "答案错误", 0).show();
			break;
		case STATUS_ANSWER_LCAK:
			// 设置文字颜色为白色(Normal)
			for (int i = 0; i < mSelectedWord.size(); i++) {
				mSelectedWord.get(i).getmViewButton().setTextColor(Color.WHITE);
			}
			break;
		}
	}

	/**
	 * 处理过关界面以及事件
	 */
	private void handlePassEvent() {
		mPassView.setVisibility(View.VISIBLE);
	}
	/**
	 * 自动选择一个答案
	 */
	private void tipAnswer(){
		boolean tipWord = false;
		for(int i = 0;  i < mSelectedWord.size();i++){
			if(mSelectedWord.get(i).getmWordString().equals("")){
				WordButton wordButton = findIsAnswerWord(i);
				onWordButtonClick(wordButton);
				tipWord = true;
				// 减少金币数量
				if(!handleCoins(getTipCoins())){
					// 金币数量不够，显示对话框
					return;
				}
				break;
			}
		}
		// 没有找到可以填充的答案
		if(!tipWord){
			//闪烁文字提示用户
			sparkTheWords();
		}
	}
	
	
	/**
	 * 找到一个答案
	 * @param index 当前需要填入答案框的索引
	 * @return
	 */
	private WordButton findIsAnswerWord(int index){
		WordButton buf = null;
		for(int  i = 0; i < mAllWord.size(); i++){
			buf = mAllWord.get(i);
			if(buf.getmWordString().equals(mCurrentSong.getNameCharacters()[index] + "")){
				return buf;
			}
		}
		return null;
	}

	/**
	 * 删除文字
	 */
	private void deleteOneWord() {
		// 减少金币
		if ( mCurrentSong.getmNameLength() == getVisibleCount()||!handleCoins(getDeleteWordCoins())) {
			// 金币不够，显示提示对话框
			return;
		}

		// 将这个索引对应的wordButton设置为不可见
		setButtonVisiable(findNotAnswer(), View.INVISIBLE);

	}
	
	private int getVisibleCount(){
		int count = 0;
		for(int i = 0; i < mAllWord.size();i++){
			if(mAllWord.get(i).ismIsVisiable()){
				count++;
			}
		}
		return count;
	}

	/**
	 * 找到一个不是答案的文件,并且当前是可见的
	 * @return
	 */
	private WordButton findNotAnswer() {
		Random random = new Random();
		WordButton buf = null;
		while (true) {
			int index = random.nextInt(MyGridView.COUNTS_WORDS);
			buf = mAllWord.get(index);
			if(buf.ismIsVisiable() && !isTheAnswerWord(buf)){
				return buf;
			}
		}
	}
	/**
	 * 判断某个文字是否为答案
	 * @param word
	 * @return
	 */
	private boolean isTheAnswerWord(WordButton word){
		boolean result = false;
		for(int i = 0; i < mCurrentSong.getmNameLength();i++){
			if(word.getmWordString().equals(mCurrentSong.getNameCharacters()[i] + "")){
				result = true;
				
			}
		}
		
		return result;
	}
	

	/**
	 * 增加或者减少指定数量的金币
	 * 
	 * @param data
	 * @return true 增加/减少成功， false失败
	 */
	private boolean handleCoins(int data) {
		// 判断当前的金币数量是否可被减少
		if (mCurrentCoins + data >= 0) {
			mCurrentCoins += data;
			mViewCurrentCoins.setText(mCurrentCoins + "");
			return true;
		}
		// 金币不够
		return false;
	}

	/**
	 * 获取删除的金币数量
	 * 
	 * @return
	 */
	private int getDeleteWordCoins() {
		return -getResources().getInteger(R.integer.pay_delete_word);
	}

	/**
	 * 过关获取的金币数量
	 * 
	 * @return
	 */
	private int getTipCoins() {
		return -getResources().getInteger(R.integer.pay_tip_answer);
	}

	/**
	 * 处理删除待选文字事件
	 */
	private void handleDeleteWord() {
		btn_delete_word.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteOneWord();
			}
		});
	}

	/**
	 * 处理提示事件
	 */
	private void handleTipAnswer() {
		btn_tip_answer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tipAnswer();
			}
		});
	}

}

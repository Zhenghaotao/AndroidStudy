package com.example.guessmusic.ui;

import java.util.ArrayList;

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

import com.example.guessmusic.R;
import com.example.guessmusic.model.WordButton;
import com.example.guessmusic.util.Util;
import com.example.guessmusic.view.MyGridView;

public class MainActivity extends Activity {

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
	
	//文字容器
	private ArrayList<WordButton> mAllWord;
	
	//已选择的容器
	private ArrayList<WordButton> mSelectedWord;
	
	//已选择文字UI容器
	private LinearLayout mViewWordsContainer;
	
	
	//标识动画是否正在进行
	private boolean mIsRunning = false;

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
	}

	private void handlePlayButton() {
		if(!mIsRunning){
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
		mPanAnim.setAnimationListener(new AnimationListener() {//设置动画监听事件

			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {//动画完成之后
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
	 * 初始化当前关的数据
	 */
	private void initCurrentStateData(){
		
		//初始化选择容器
		mSelectedWord = initWordSelect();
		LayoutParams params = new LayoutParams(70, 70);
		for(int i = 0; i < mSelectedWord.size(); i++){
			mViewWordsContainer.addView(mSelectedWord.get(i).getmViewButton(),params);
		}
		//获得数据
		mAllWord = initAllWord();
		
		//更新数据- MyGridView
		myGridView.updateData(mAllWord);
		
	}
	/**
	 * 初始化选字数据
	 */
	private ArrayList<WordButton> initAllWord(){
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		//获得所有待选文字
		// ................
		for(int i = 0; i <  MyGridView.COUNTS_WORDS; i++){
			WordButton button = new WordButton();
			button.setmWordString("好");
			data.add(button);
		}
		return data;
	}
	/**
	 * 初始化已选择文字框
	 * @return
	 */
	private ArrayList<WordButton> initWordSelect(){
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		for(int i = 0; i < 4; i++){
			View v = Util.getView(R.layout.self_ui_gridview_item, MainActivity.this);
			WordButton holder = new WordButton();
			holder.setmViewButton((Button)v.findViewById(R.id.btn_item));
			holder.getmViewButton().setTextColor(Color.WHITE);
			holder.getmViewButton().setText("");
			holder.setmIsVisiable(false);
			
			holder.getmViewButton().setBackgroundResource(R.drawable.game_wordblank);
			
			data.add(holder);
			
		}
		return data;
		
	}
	
}

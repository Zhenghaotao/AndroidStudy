package com.example.guessmusic.model;

import android.widget.Button;

/**
 * 文字按钮
 */
public class WordButton {
	private int mIndex;
	private boolean mIsVisiable;
	private String mWordString;
	
	
	private Button mViewButton;
	
	public WordButton() {
		mIsVisiable = true;
		mWordString = "";
	}

	public int getmIndex() {
		return mIndex;
	}

	public void setmIndex(int mIndex) {
		this.mIndex = mIndex;
	}

	public boolean ismIsVisiable() {
		return mIsVisiable;
	}

	public void setmIsVisiable(boolean mIsVisiable) {
		this.mIsVisiable = mIsVisiable;
	}

	public String getmWordString() {
		return mWordString;
	}

	public void setmWordString(String mWordString) {
		this.mWordString = mWordString;
	}

	public Button getmViewButton() {
		return mViewButton;
	}

	public void setmViewButton(Button mViewButton) {
		this.mViewButton = mViewButton;
	}
	
}

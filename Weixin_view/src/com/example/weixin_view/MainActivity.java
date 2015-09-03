package com.example.weixin_view;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.jauker.widget.BadgeView;

public class MainActivity extends FragmentActivity {

	private ViewPager pager;

	private ChatMainFragment chatMainFragment;
	private FriendMainFragment friendMainFragment;
	private ContactMainFragment contactMainFragment;
	private List<Fragment> mDatas;
	private FragmentPagerAdapter adapter;

	private TextView mChatTextView;
	private TextView mFriendTextView;
	private TextView mContactTextView;
	private BadgeView mBadgeView;

	private LinearLayout ll_chat;
	private LinearLayout ll_search;
	private LinearLayout ll_contact;

	private ImageView iv_tabline;
	private int cursorWidth;
	private int mCurrentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initCurWidth();
		initView();
		initData();
		initAdapter();
	}

	private void initCurWidth() {
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		cursorWidth = outMetrics.widthPixels / 3;
	}

	private void initAdapter() {
		adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return mDatas.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return mDatas.get(arg0);
			}
		};
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				resetTextView();
				switch (position) {
				case 0:
					if (mBadgeView != null) {
						ll_chat.removeView(mBadgeView);
					} else {
						mBadgeView = new BadgeView(MainActivity.this);
						mBadgeView.setBadgeCount(7);
						ll_chat.addView(mBadgeView);
					}
					mChatTextView.setTextColor(Color.parseColor("#008000"));
					break;
				case 1:
					mFriendTextView.setTextColor(Color.parseColor("#008000"));
					break;
				case 2:
					mContactTextView.setTextColor(Color.parseColor("#008000"));
					break;
				}
				mCurrentIndex = position;
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPx) {
				Log.i("Main", position + ",  " + positionOffset + ",   "
						+ positionOffsetPx);
				LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) iv_tabline
						.getLayoutParams();
				if(mCurrentIndex == 0 && position == 0){//0->1
					lp.leftMargin = (int) (positionOffset * cursorWidth +  mCurrentIndex * cursorWidth);
				} else if(mCurrentIndex == 1 && position == 0){//1->0
					lp.leftMargin = (int) (mCurrentIndex * cursorWidth + (positionOffset - 1) * cursorWidth);
				} else if(mCurrentIndex == 1 && position == 1){// 1- > 2
					lp.leftMargin = (int) (mCurrentIndex * cursorWidth + positionOffset * cursorWidth);
				} else if(mCurrentIndex == 2 && position == 1){//2-> 1
					lp.leftMargin = (int) (mCurrentIndex * cursorWidth + (positionOffset -1 )* cursorWidth);
				}
				
				iv_tabline.setLayoutParams(lp);

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	protected void resetTextView() {
		mChatTextView.setTextColor(Color.BLACK);
		mFriendTextView.setTextColor(Color.BLACK);
		mContactTextView.setTextColor(Color.BLACK);

	}

	private void initData() {
		chatMainFragment = new ChatMainFragment();
		friendMainFragment = new FriendMainFragment();
		contactMainFragment = new ContactMainFragment();
		mDatas = new ArrayList<Fragment>();
		mDatas.add(chatMainFragment);
		mDatas.add(friendMainFragment);
		mDatas.add(contactMainFragment);
	}

	private void initView() {
		pager = (ViewPager) findViewById(R.id.pager);
		mChatTextView = (TextView) findViewById(R.id.tv_chat);
		mFriendTextView = (TextView) findViewById(R.id.tv_search);
		mContactTextView = (TextView) findViewById(R.id.tv_contact);

		iv_tabline = (ImageView) findViewById(R.id.iv_tabline);
		ViewGroup.LayoutParams lp = iv_tabline.getLayoutParams();
		lp.width = cursorWidth;
		iv_tabline.setLayoutParams(lp);

		ll_chat = (LinearLayout) findViewById(R.id.ll_chat);
		ll_search = (LinearLayout) findViewById(R.id.ll_search);
		ll_contact = (LinearLayout) findViewById(R.id.ll_contact);
	}

}

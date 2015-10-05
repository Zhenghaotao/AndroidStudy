package com.example.guessmusic.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.example.guessmusic.R;
import com.example.guessmusic.model.IWordButtonClickListener;
import com.example.guessmusic.model.WordButton;
import com.example.guessmusic.util.Util;

public class MyGridView extends GridView {
	// 单词数量
	public final static int COUNTS_WORDS = 24;
	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();

	private MyGridAdapter mAdapter;

	private Animation mScaleAnimation;
	
	private IWordButtonClickListener mWordButtonClickListener;

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAdapter = new MyGridAdapter();
		setAdapter(mAdapter);
	}

	public void updateData(ArrayList<WordButton> list) {
		mArrayList = list;
		// 重新设置数据源
		setAdapter(mAdapter);

	}

	class MyGridAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return mArrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final WordButton holder;

			// 加载动画
			if (convertView == null) {
				convertView = Util.getView(R.layout.self_ui_gridview_item,
						parent, getContext());
				holder = mArrayList.get(position);

				mScaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
				//设置动画的延迟时间
				mScaleAnimation.setStartOffset(position * 100);
				holder.setmIndex(position);
				holder.setmViewButton((Button) convertView
						.findViewById(R.id.btn_item));
				
				holder.getmViewButton().startAnimation(mScaleAnimation);
				convertView.setTag(holder);
				holder.getmViewButton().setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(mWordButtonClickListener != null){
							mWordButtonClickListener.onWordButtonClick(holder);
						}
					}
				});
				
			} else {
				holder = (WordButton) convertView.getTag();
			}
			
			holder.getmViewButton().setText(holder.getmWordString());
			return convertView;
		}

	}
	/**
	 * 注册监听接口
	 */
	public void setOnWordButtonClickListener(IWordButtonClickListener listener){
		this.mWordButtonClickListener = listener;
	}
	

}

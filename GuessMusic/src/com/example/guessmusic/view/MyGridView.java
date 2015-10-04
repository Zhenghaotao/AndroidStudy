package com.example.guessmusic.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.example.guessmusic.R;
import com.example.guessmusic.model.WordButton;
import com.example.guessmusic.util.Util;

public class MyGridView extends GridView {
	//单词数量
	public final static int COUNTS_WORDS = 24;
	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();

	private MyGridAdapter mAdapter;


	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAdapter = new MyGridAdapter();
		setAdapter(mAdapter);
	}

	
	public void updateData(ArrayList<WordButton> list){
		mArrayList = list;
		//重新设置数据源
		mAdapter.notifyDataSetChanged();
		
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
			WordButton holder;

			if (convertView == null) {
				convertView = Util.getView(R.layout.self_ui_gridview_item,
						parent, getContext());
				holder = mArrayList.get(position);
				holder.setmIndex(position);
				holder.setmViewButton((Button) convertView
						.findViewById(R.id.btn_item));
				convertView.setTag(holder);
			} else {
				holder = (WordButton) convertView.getTag();
			}
			holder.getmViewButton().setText(holder.getmWordString());

			return convertView;
		}

	}

}

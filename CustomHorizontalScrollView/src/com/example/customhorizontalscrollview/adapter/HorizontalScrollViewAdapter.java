package com.example.customhorizontalscrollview.adapter;

import java.util.List;

import com.example.customhorizontalscrollview.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HorizontalScrollViewAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<Integer> mDatas;

	public HorizontalScrollViewAdapter(Context mContext, List<Integer> mDatas) {
		this.mContext = mContext;
		mInflater = LayoutInflater.from(mContext);
		this.mDatas = mDatas;
	}

	public int getCount() {
		return mDatas.size();
	}

	public Object getItem(int position) {
		return mDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View currentView, ViewGroup parent) {
		ViewHolder holder = null;
		if (currentView == null) {
			holder = new ViewHolder();
			currentView = mInflater.inflate(R.layout.gallery_item, parent,//注意有parent，外面的linearLayout
					false);
			holder.mImg = (ImageView) currentView
					.findViewById(R.id.iv_item_image);
			holder.mText = (TextView) currentView
					.findViewById(R.id.tv_item_text);
			currentView.setTag(holder);
		} else {
			holder = (ViewHolder) currentView.getTag();
		}
		holder.mImg.setImageResource(mDatas.get(position));
		holder.mText.setText("Image  Infomation ");

		return currentView;
	}

	class ViewHolder {
		ImageView mImg;
		TextView mText;
	}

}

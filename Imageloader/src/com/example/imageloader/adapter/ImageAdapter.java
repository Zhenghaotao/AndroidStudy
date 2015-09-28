package com.example.imageloader.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.imageloader.R;
import com.example.imageloader.util.Imageloader;
import com.example.imageloader.util.Imageloader.Type;

public class ImageAdapter extends BaseAdapter {

	private static Set<String> mSelectedImg = new HashSet<String>();
	
	private LayoutInflater mInflater;
	private List<String> mImgPaths;
	private String dirPath;

	public ImageAdapter(Context context, List<String> mImgPaths,
			String dirPath) {
		mInflater = LayoutInflater.from(context);
		this.mImgPaths = mImgPaths;
		this.dirPath = dirPath;
	}

	@Override
	public int getCount() {
		return mImgPaths.size();
	}

	@Override
	public Object getItem(int position) {
		return mImgPaths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder ;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_gridview, parent,
					false);
			holder.iv_item_img = (ImageView) convertView
					.findViewById(R.id.iv_item_img);
			holder.ib_item_select = (ImageButton) convertView
					.findViewById(R.id.ib_item_select);

			convertView.setTag(holder);
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}
		holder.iv_item_img.setImageResource(R.drawable.pictures_no);
		holder.ib_item_select
				.setImageResource(R.drawable.picture_unselected);
		holder.ib_item_select.setColorFilter(Color.parseColor("#77000000"));

		Imageloader.getInstance(3, Type.LIFO)
				.loadImage(dirPath + "/" + mImgPaths.get(position),
						holder.iv_item_img);
		
		
		final String filePath = dirPath + "/" + mImgPaths.get(position);
		holder.iv_item_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//已经被选择
				if(mSelectedImg.contains(filePath)){
					mSelectedImg.remove(filePath);
					holder.ib_item_select.setColorFilter(Color.parseColor("#77000000"));
					holder.ib_item_select.setImageResource(R.drawable.pictures_selected);
				} else { //未被选择
					mSelectedImg.add(filePath);
					holder.ib_item_select.setColorFilter(null);
					holder.ib_item_select.setImageResource(R.drawable.picture_unselected);
				}
//				notifyDataSetChanged();
			}
		});
		if(mSelectedImg.contains(filePath)){
			holder.ib_item_select.setColorFilter(Color.parseColor("#77000000"));
			holder.ib_item_select.setImageResource(R.drawable.pictures_selected);
		} else {
			holder.ib_item_select.setColorFilter(null);
			holder.ib_item_select.setImageResource(R.drawable.picture_unselected);
		}
		

		return convertView;
	}

	class ViewHolder {
		ImageView iv_item_img;
		ImageButton ib_item_select;
	}

}
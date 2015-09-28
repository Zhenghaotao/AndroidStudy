package com.example.imageloader.view;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.imageloader.R;
import com.example.imageloader.bean.FolderBean;
import com.example.imageloader.util.Imageloader;
import com.example.imageloader.util.Imageloader.Type;

public class ListImageDirPopupWindow extends PopupWindow {

	private int mWidth;
	private int mHeight;
	private View mConvertView;
	private ListView mListView;
	private List<FolderBean> mDatas;
	
	
	public interface OnDirSelecedListener{
		void onSelectd(FolderBean folderBean);
	}
	
	private OnDirSelecedListener mListener;
	
	

	public void setOnDirSelecedListener(OnDirSelecedListener mListener) {
		this.mListener = mListener;
	}

	public ListImageDirPopupWindow(Context context, List<FolderBean> mDatas) {
		this.mDatas = mDatas;

		calWidthAndHeight(context);
		mConvertView = LayoutInflater.from(context).inflate(
				R.layout.popup_main, null);

		setContentView(mConvertView);
		setWidth(mWidth);
		setHeight(mHeight);

		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());

		setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					dismiss();
					return true;
				}

				return false;
			}
		});
		initViews(context);
		initEvent();

	}

	private void initEvent() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(mListener != null){
					FolderBean folderBean = mDatas.get(position);
					mListener.onSelectd(folderBean);
				}
			}
		});
	}

	private void initViews(Context context) {
		mListView = (ListView) mConvertView.findViewById(R.id.lv_dir);
		mListView.setAdapter(new ListDirAdapter(context, mDatas));
	}

	/**
	 * 计算popwindow的宽度和高度
	 */
	private void calWidthAndHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);

		mWidth = outMetrics.widthPixels;
		mHeight = (int) (outMetrics.heightPixels * 0.7);

	}

	private class ListDirAdapter extends ArrayAdapter<FolderBean> {

		private LayoutInflater mInflater;
		private List<FolderBean> mDatas;

		public ListDirAdapter(Context context,
				List<FolderBean> mDatas) {
			super(context, 0, mDatas);
			mInflater = LayoutInflater.from(context);

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_popup_main,
						parent, false);
				holder.mImg = (ImageView) convertView
						.findViewById(R.id.dir_item_image);
				holder.mDirName = (TextView) convertView
						.findViewById(R.id.tv_dir_name);
				holder.mDirCount = (TextView) convertView
						.findViewById(R.id.tv_dir_cout);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			FolderBean bean = getItem(position);
			//重置
			holder.mImg.setImageResource(R.drawable.pictures_no);
			
			Imageloader.getInstance(3, Type.FIFO).loadImage(
					bean.getFirstImgPath(), holder.mImg);
			holder.mDirName.setText(bean.getName());
			holder.mDirCount.setText(bean.getCount() + "");

			return convertView;
		}

		class ViewHolder {
			ImageView mImg;
			TextView mDirName;
			TextView mDirCount;
		}

	}

}

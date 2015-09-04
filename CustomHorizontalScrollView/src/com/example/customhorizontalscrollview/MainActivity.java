package com.example.customhorizontalscrollview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.customhorizontalscrollview.adapter.CustomHorizontalScrollView;
import com.example.customhorizontalscrollview.adapter.CustomHorizontalScrollView.CurrentImageChangeListener;
import com.example.customhorizontalscrollview.adapter.CustomHorizontalScrollView.OnItemClickListener;
import com.example.customhorizontalscrollview.adapter.HorizontalScrollViewAdapter;

public class MainActivity extends Activity {
	private List<Integer> mDatas = new ArrayList<Integer>(Arrays.asList(
			R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d,
			R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h,
			R.drawable.i, R.drawable.j,R.drawable.l));
	private LayoutInflater mInflater;
	private CustomHorizontalScrollView chs_gallery;
	private ImageView iv_content;
	private HorizontalScrollViewAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mInflater = LayoutInflater.from(this);
		initData();
		initView();

		initListener();
		// 设置适配器
		chs_gallery.initDatas(mAdapter);

	}

	private void initListener() {
		chs_gallery
				.setCurrentImageChangeListener(new CurrentImageChangeListener() {

					@Override
					public void onCurrentImgChanged(int position,
							View viewIndicator) {
						iv_content.setImageResource(mDatas.get(position));
						viewIndicator.setBackgroundColor(Color
								.parseColor("#aa024da4"));
					}
				});
		chs_gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onClick(View view, int position) {
				iv_content.setImageResource(mDatas.get(position));
				view.setBackgroundColor(Color.parseColor("#AA024DA4"));
			}
		});
	}

	private void initView() {
		chs_gallery = (CustomHorizontalScrollView) findViewById(R.id.chs_gallery);
		iv_content = (ImageView) findViewById(R.id.iv_content);
	}

	private void initData() {
		mAdapter = new HorizontalScrollViewAdapter(this, mDatas);
	}

}

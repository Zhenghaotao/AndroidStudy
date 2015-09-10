package com.example.imagedeal;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PrimaryColor extends Activity implements OnSeekBarChangeListener {
	private ImageView iv_show;
	private SeekBar sb_hue;
	private SeekBar sb_saturation;
	private SeekBar sb_lum;
	private static int MAX_VALUE = 255;
	private static int MID_VALUE = 127;
	private float mHue;
	private float mSaturation;
	private float mLum;
	private Bitmap bm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.primary_color);
		initBitmap();
		initView();
		initListener();

	}

	private void initBitmap() {
		bm = BitmapFactory.decodeResource(getResources(), R.drawable.j);
	}

	private void initListener() {
		sb_hue.setOnSeekBarChangeListener(this);
		sb_saturation.setOnSeekBarChangeListener(this);
		sb_lum.setOnSeekBarChangeListener(this);
	}

	private void initView() {
		iv_show = (ImageView) findViewById(R.id.iv_show);
		iv_show.setImageBitmap(bm);
		
		sb_hue = (SeekBar) findViewById(R.id.sb_hue);
		sb_saturation = (SeekBar) findViewById(R.id.sb_saturation);
		sb_lum = (SeekBar) findViewById(R.id.sb_lum);

		sb_hue.setMax(MAX_VALUE);
		sb_saturation.setMax(MAX_VALUE);
		sb_lum.setMax(MAX_VALUE);

		sb_hue.setProgress(MID_VALUE);
		sb_saturation.setProgress(MID_VALUE);
		sb_lum.setProgress(MID_VALUE);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.sb_hue://色调
			mHue = (progress - MID_VALUE) * 1.0f / MID_VALUE * 180;
			break;
		case R.id.sb_saturation://饱和度
			mSaturation = progress * 1.0f / MID_VALUE;
			break;
		case R.id.sb_lum://亮度
			mLum = progress * 1.0f / MID_VALUE;
			break;

		}
		iv_show.setImageBitmap(ImageHelper.handleImageEffect(bm, mHue, mSaturation, mLum));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}
}

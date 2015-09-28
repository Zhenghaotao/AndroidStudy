package com.example.imageloader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imageloader.adapter.ImageAdapter;
import com.example.imageloader.bean.FolderBean;
import com.example.imageloader.view.ListImageDirPopupWindow;
import com.example.imageloader.view.ListImageDirPopupWindow.OnDirSelecedListener;

public class MainActivity extends Activity {

	private GridView mGridView;
	private List<String> mImgs;
	private ImageAdapter adapter;

	private RelativeLayout rl_bottom;
	private TextView tv_dirname;
	private TextView tv_count;

	private File mCurrentDir;
	private int mMaxCount;

	private List<FolderBean> mFolderBeans = new ArrayList<FolderBean>();

	private ProgressDialog mProgressDialog;

	private static final int DATA_LOADED = 0x110;

	private ListImageDirPopupWindow mDirPopupWindow;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == DATA_LOADED) {
				mProgressDialog.dismiss();
				// 绑定数据到View中
				data2View();

				initDirPopupWindow();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initDatas();
		initEvent();
	}

	protected void initDirPopupWindow() {
		mDirPopupWindow = new ListImageDirPopupWindow(this, mFolderBeans);

		mDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				lightOn();
			}
		});
		mDirPopupWindow.setOnDirSelecedListener(new OnDirSelecedListener() {

			@Override
			public void onSelectd(FolderBean folderBean) {
				mCurrentDir = new File(folderBean.getDir());
				mImgs = Arrays.asList(mCurrentDir.list(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String filename) {
						if (filename.endsWith(".jpg")
								|| filename.endsWith(".jpeg")
								|| filename.endsWith(".png")) {
							return true;
						}
						return false;
					}
				}));
				adapter = new ImageAdapter(MainActivity.this, mImgs,
						mCurrentDir.getAbsolutePath());
				mGridView.setAdapter(adapter);
				tv_count.setText(mImgs.size() + "");
				tv_dirname.setText(folderBean.getName());

				mDirPopupWindow.dismiss();
			}
		});
	}

	protected void data2View() {
		if (mCurrentDir == null) {
			Toast.makeText(this, "未扫描任何图片", Toast.LENGTH_SHORT).show();
			return;
		}
		mImgs = Arrays.asList(mCurrentDir.list());
		adapter = new ImageAdapter(this, mImgs, mCurrentDir.getAbsolutePath());
		mGridView.setAdapter(adapter);

		tv_count.setText(mMaxCount + "张");
		tv_dirname.setText(mCurrentDir.getName());
	}

	private void initEvent() {
		rl_bottom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDirPopupWindow.setAnimationStyle(R.style.popupwindow_anim);
				mDirPopupWindow.showAsDropDown(rl_bottom, 0, 0);
				lightOff();
			}
		});

	}

	/**
	 * 内容区域变亮
	 */
	protected void lightOn() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);
	}

	/**
	 * 内容区域变暗
	 */
	protected void lightOff() {

		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.3f;
		getWindow().setAttributes(lp);
	}

	/**
	 * 利用ContentProvider扫描手机中的所有图片
	 */
	private void initDatas() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "当前存储卡不可用", Toast.LENGTH_LONG).show();
			return;
		}
		mProgressDialog = ProgressDialog.show(this, null, "正在加载");
		new Thread() {
			public void run() {
				Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver cr = MainActivity.this.getContentResolver();
				Cursor cursor = cr.query(mImgUri, null,
						MediaStore.Images.Media.MIME_TYPE + " = ? or "
								+ MediaStore.Images.Media.MIME_TYPE + " = ? ",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);
				Set<String> mdirPath = new HashSet<String>();

				while (cursor.moveToNext()) {
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					File parentFile = new File(path).getParentFile();
					if (parentFile == null) {
						continue;
					}
					String dirPath = parentFile.getAbsolutePath();
					FolderBean folderBean = null;
					if (mdirPath.contains(dirPath)) {
						continue;
					} else {
						mdirPath.add(dirPath);
						folderBean = new FolderBean();
						folderBean.setDir(dirPath);
						folderBean.setFirstImgPath(path);
					}
					if (parentFile.list() == null) {
						continue;
					}
					int picSize = parentFile.list(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".jpeg")
									|| filename.endsWith(".png")) {
								return true;
							}
							return false;
						}
					}).length;
					folderBean.setCount(picSize);
					mFolderBeans.add(folderBean);
					if (picSize > mMaxCount) {
						mMaxCount = picSize;
						mCurrentDir = parentFile;
					}
				}
				cursor.close();
				// 扫描完成
				mdirPath = null;
				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(DATA_LOADED);
			};
		}.start();
	}
	private void initView() {
		mGridView = (GridView) findViewById(R.id.gv_show);
		rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
		tv_dirname = (TextView) findViewById(R.id.tv_dirname);
		tv_count = (TextView) findViewById(R.id.tv_count);
	}

}

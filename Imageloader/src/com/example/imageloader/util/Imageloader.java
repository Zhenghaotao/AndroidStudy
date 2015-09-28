package com.example.imageloader.util;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * 图片加载类
 */
public class Imageloader {
	private static Imageloader mInstance;
	/**
	 * 图片缓存的核心对象
	 */
	private LruCache<String, Bitmap> mLruChache;

	/**
	 * 线程池
	 */
	private ExecutorService mThreadPool;
	private static final int DEFAULT_THREAD_COUNT = 1;
	/**
	 * 队列的调度方式
	 */
	private Type mType = Type.LIFO;
	/**
	 * 任务队列
	 */
	private LinkedList<Runnable> mTaskQueue;
	/**
	 * 后台轮询线程
	 */
	private Thread mPoolThread;
	private Handler mPoolTHreadHandler;
	/**
	 * UI线程中的handler
	 */
	private Handler mUIHandler;

	public enum Type {
		FIFO, LIFO;
	}

	private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);

	private Semaphore mSemaphoreThreadPool;

	private Imageloader(int threadCount, Type type) {
		init(threadCount, type);
	}

	/**
	 * 通过反射获取imageView的某个属性值
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldVale(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = field.getInt(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return value;
	}

	/**
	 * 初始化
	 * 
	 * @param threadCount
	 * @param type
	 */
	private void init(int threadCount, Type type) {
		// 后台轮询线程
		mPoolThread = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				mPoolTHreadHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						// 线程池去取出一个任务进行执行
						mThreadPool.execute(getTask());
						try {
							mSemaphoreThreadPool.acquire();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				// 释放信号量
				mSemaphorePoolThreadHandler.release();
				Looper.loop();
			}
		};
		mPoolThread.start();
		// 获取我们应用的最大内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMemory = maxMemory / 8;

		mLruChache = new LruCache<String, Bitmap>(cacheMemory) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<Runnable>();
		mType = type;
		mSemaphoreThreadPool = new Semaphore(threadCount);
	}

	public static Imageloader getInstance(int threadCount,Type t) {
		if (mInstance == null) {
			synchronized (Imageloader.class) {
				if (mInstance == null) {
					mInstance = new Imageloader(threadCount,t);
				}
			}
		}
		return mInstance;
	}

	public void loadImage(final String path, final ImageView imageView) {
		imageView.setTag(path);
		if (mUIHandler == null) {
			mUIHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// 获取得到图片,为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					Bitmap bm = holder.bitmap;
					ImageView iv = holder.imageView;
					String path = holder.path;
					// 将path与getTag存储路径进行比较
					if (iv.getTag().toString().equals(path)) {
						iv.setImageBitmap(bm);
					}
				}
			};
		}
		// 根据path在缓存中获取bitmap
		Bitmap bm = getBtiampFromLurCache(path);
		if (bm != null) {
			refreshBitmap(path, imageView, bm);
		} else {
			addTask(new Runnable() {
				@Override
				public void run() {
					// 加载图片

					// 图片压缩
					// 1.获得图片需要显示的大小
					ImageSize imageSize = getImageViewSize(imageView);
					// 2. 压缩图片
					Bitmap bm = decodeSampledBitmapFromPath(path,
							imageSize.width, imageSize.height);
					// 3.图片加入到缓存
					addBitmapToLruCache(path, bm);

					refreshBitmap(path, imageView, bm);
					mSemaphoreThreadPool.release();
				}

			});
		}
	}

	private void refreshBitmap(final String path, final ImageView imageView,
			Bitmap bm) {
		Message message = Message.obtain();
		ImgBeanHolder holder = new ImgBeanHolder();
		holder.bitmap = bm;
		holder.path = path;
		holder.imageView = imageView;
		message.obj = holder;
		mUIHandler.sendMessage(message);
	}

	/**
	 * 将图片加入到缓存
	 * 
	 * @param path
	 * @param bm
	 */
	protected void addBitmapToLruCache(String path, Bitmap bm) {
		if (getBtiampFromLurCache(path) == null) {
			if (bm != null) {
				mLruChache.put(path, bm);
			}
		}
	}

	/**
	 * 根据图片需要显示的宽和高对比图片进行压缩
	 * 
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	protected Bitmap decodeSampledBitmapFromPath(String path, int width,
			int height) {
		// 获取图片的宽和高,并不把图片加载内存中
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = caculateInSampleSize(options, width, height);
		// 使用获得到的InSampleSize再次解析图片
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}

	/**
	 * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
	 * 
	 * @param options
	 * @param width
	 * @param height
	 * @return
	 */
	private int caculateInSampleSize(Options options, int reqWidth,
			int reqHeight) {
		int width = options.outWidth;
		int height = options.outHeight;

		int inSampleSize = 1;
		if (width > reqWidth || height > reqHeight) {
			int widthRadio = Math.round(width * 1.0f / reqWidth);
			int heightRadio = Math.round(height * 1.0f / reqHeight);
			inSampleSize = Math.max(widthRadio, heightRadio);
		}
		return inSampleSize;
	}

	/**
	 * 根据ImageView获取适当压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	protected ImageSize getImageViewSize(ImageView imageView) {
		ImageSize imageSize = new ImageSize();
		DisplayMetrics displayMetrics = imageView.getContext().getResources()
				.getDisplayMetrics();

		LayoutParams lp = imageView.getLayoutParams();
		int width = imageView.getWidth();// 获取imageview的实际宽度
		getImageViewFieldVale(imageView, "mMaxWidth");
		if (width <= 0) {
			width = lp.width; // 获取imageView在layout中声明的宽度
		}
		if (width <= 0) {
//			width = imageView.getMaxWidth();// 检测最大值
			width = getImageViewFieldVale(imageView, "mMaxWidth");// 检测最大值
		}
		if (width <= 0) {
			width = displayMetrics.widthPixels;
		}
		int height = imageView.getHeight();// 获取imageview的实际宽度
		
		if (height <= 0) {
			height = lp.height; // 获取imageView在layout中声明的宽度
		}
		if (height <= 0) {
//			height = imageView.getMaxHeight();// 检测最大值
			height = getImageViewFieldVale(imageView, "mMaxHeight");// 检测最大值
		}
		if (height <= 0) {
			height = displayMetrics.heightPixels;
		}
		imageSize.width = width;
		imageSize.height = height;

		return imageSize;
	}

	/**
	 * 从队列中取出一个线程
	 * 
	 * @return
	 */
	private Runnable getTask() {
		if (mType == Type.FIFO) {
			return mTaskQueue.removeFirst();
		} else if (mType == Type.LIFO) {
			return mTaskQueue.removeLast();
		}
		return null;
	}

	private synchronized void addTask(Runnable runnable) {
		mTaskQueue.add(runnable);
		try {
			if (mPoolTHreadHandler == null) {
				mSemaphorePoolThreadHandler.acquire();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mPoolTHreadHandler.sendEmptyMessage(0x110);
	}

	private Bitmap getBtiampFromLurCache(String path) {
		return mLruChache.get(path);
	}

	private class ImageSize {
		int width;
		int height;
	}

	private class ImgBeanHolder {
		Bitmap bitmap;
		ImageView imageView;
		String path;
	}
}

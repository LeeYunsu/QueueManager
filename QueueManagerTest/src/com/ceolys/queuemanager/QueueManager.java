package com.ceolys.queuemanager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.Handler;

public class QueueManager<T> {
	// Data max size
	public final static int BUFFER_MAX_SIZE = 5;
	public final static int DEFAULT_DATACHANGED_PORIOD_MS = 10;

	// Wait added Item after max_size
	public final static int TYPE_ADD_WAIT = 0x00000001;
	// No wait added Item after max_size
	public final static int TYPE_ADD_NOT_WAIT = 0x00000002;

	// Data Objects
	private ArrayBlockingQueue<T> mArrayQueue;

	// Data Object for TYPE_ADD_WAIT
	private LinkedBlockingQueue<T> mLinkedQueue;

	// Data Thread for TYPE_ADD_WAIT
	LinkedQueueThread linkedQueueThread;

	// Data callback interface
	private OnDataChangedListener<T> onDataChangedListener;

	// Data check Timer
	private Timer mTimer;
	private TimerTask mTimerTask;
	private Handler mHandler;

	// Data add type
	private int mType = TYPE_ADD_NOT_WAIT;

	// LastData for TYPE_DUPLICATE
	private T lastData;

	public interface OnDataChangedListener<V> {
		void onDataChanged(V data);
	}

	public QueueManager() {
		this(TYPE_ADD_NOT_WAIT);
	}

	public QueueManager(int type) {
		mArrayQueue = new ArrayBlockingQueue<T>(BUFFER_MAX_SIZE);
		mLinkedQueue = new LinkedBlockingQueue<T>();
		linkedQueueThread = new LinkedQueueThread();

		mHandler = new Handler();
		mType = type;
	}

	public void setType(int type) {
		this.mType = type;
	}

	public void clear() {
		mArrayQueue.clear();
	}

	public T getDataItem() {
		return mArrayQueue.poll();
	}

	public boolean isDataExist() {
		return mArrayQueue.peek() != null;
	}

	public void addDataItem(final T data) {

		if (Utils.equlsFlag(mType, TYPE_ADD_NOT_WAIT)) {
			boolean success = mArrayQueue.offer(data);
		}
		else {
			if (mLinkedQueue.size() > 0) {
				mLinkedQueue.offer(data);
			}
			else {
				boolean success = mArrayQueue.offer(data);
				if (!success) {
					mLinkedQueue.offer(data);

					if (!linkedQueueThread.mStarted) {
						linkedQueueThread.start();
					}
					else {
						linkedQueueThread.onResume();
					}
				}
			}
		}

	}

	public void setOnDataChangedListener(final OnDataChangedListener<T> onDataChangedListener) {
		setOnDataChangedListener(onDataChangedListener, DEFAULT_DATACHANGED_PORIOD_MS);
	}

	/**
	 * @param onDataChangedListener
	 * @param period
	 *            -union : (ms)
	 */
	public void setOnDataChangedListener(final OnDataChangedListener onDataChangedListener, int period) {
		this.onDataChangedListener = onDataChangedListener;
		// ≈∏¿Ã∏”
		mTimer = new Timer();
		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				if (isDataExist()) {
					final T item = getDataItem();
					if (item != null) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								onDataChangedListener.onDataChanged(item);
							}
						});
					}
				}
			}
		};
		mTimer.schedule(mTimerTask, 0, period);
	}

	/**
	 * DataChangedListener use finished must remove setOnDataChangedListener
	 */
	public void removeOnDataChangedListener() {
		if (mTimer != null) {
			mTimer.cancel();
		}

		if (linkedQueueThread.mStarted) {
			linkedQueueThread.mFinished = true;
		}
	}

	class LinkedQueueThread extends Thread {
		private Object mPauseLock;
		private boolean mPaused;
		private boolean mFinished;
		private boolean mStarted;

		public LinkedQueueThread() {
			mPauseLock = new Object();
			mPaused = false;
			mFinished = false;
		}

		@Override
		public synchronized void start() {
			mStarted = true;
			super.start();
		}

		public void run() {
			while (!mFinished) {
				synchronized (mPauseLock) {
					while (mPaused) {
						try {
							mPauseLock.wait();
						}
						catch (InterruptedException e) {
						}
					}
				}
				T data = mLinkedQueue.peek();
				if (data != null) {
					boolean isAdded = mArrayQueue.offer(data);
					if (isAdded) {
						mLinkedQueue.remove(data);
						if (mLinkedQueue.isEmpty()) {
							onPause();
						}
					}
				}
				try {
					Thread.sleep(DEFAULT_DATACHANGED_PORIOD_MS);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void onPause() {
			if (!mPaused) {
				synchronized (mPauseLock) {
					mPaused = true;
				}
			}
		}

		public void onResume() {
			if (mPaused) {
				synchronized (mPauseLock) {
					mPaused = false;
					mPauseLock.notifyAll();
				}
			}
		}

	}
}

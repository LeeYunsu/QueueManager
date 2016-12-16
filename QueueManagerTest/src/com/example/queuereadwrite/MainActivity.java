package com.example.queuereadwrite;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ceolys.queuemanager.QueueManager;
import com.ceolys.queuemanager.QueueManager.OnDataChangedListener;
import com.example.queuedatareadwrite.R;

public class MainActivity extends Activity {
	public final static String TAG = "DataManager";

	QueueManager<byte[]> dataManagerByteArr;
	QueueManager<String> dataManagerStr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dataManagerByteArr = new QueueManager<byte[]>(QueueManager.TYPE_ADD_WAIT);

		dataManagerByteArr.setOnDataChangedListener(new OnDataChangedListener<byte[]>() {

			@Override
			public void onDataChanged(byte[] data) {
				Log.v(TAG, "onDataChanged! byte data size == " + data.length);
			}
		}, 500);

		dataManagerByteArr.addDataItem(new byte[] { 1 });
		dataManagerByteArr.addDataItem(new byte[] { 1, 2 });
		dataManagerByteArr.addDataItem(new byte[] { 1, 2, 3 });
		dataManagerByteArr.addDataItem(new byte[] { 1, 2, 3, 4 });
		dataManagerByteArr.addDataItem(new byte[] { 1, 2, 3, 4, 5 });
		dataManagerByteArr.addDataItem(new byte[] { 1, 2, 3, 4, 5, 6 });
		dataManagerByteArr.addDataItem(new byte[] { 1, 2, 3, 4, 5, 6, 7 });
		dataManagerByteArr.addDataItem(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 });
		dataManagerByteArr.addDataItem(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		dataManagerByteArr.addDataItem(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
		dataManagerByteArr.addDataItem(new byte[] { 1 });
		dataManagerByteArr.addDataItem(new byte[] { 1 });
		dataManagerByteArr.addDataItem(new byte[] { 1 });
		dataManagerByteArr.addDataItem(new byte[] { 1 });
		dataManagerByteArr.addDataItem(new byte[] { 1 });
		dataManagerByteArr.addDataItem(new byte[] { 1 });

		
		dataManagerStr = new QueueManager<String>(QueueManager.TYPE_ADD_NOT_WAIT);
		dataManagerStr.setOnDataChangedListener(new OnDataChangedListener<String>() {

			@Override
			public void onDataChanged(String data) {
				Log.i(TAG, "onDataChanged! String data == " + data);
			}
		}, 100);
		dataManagerStr.addDataItem("h");
		dataManagerStr.addDataItem("e");
		dataManagerStr.addDataItem("l");
		dataManagerStr.addDataItem("l");
		dataManagerStr.addDataItem("o");
		dataManagerStr.addDataItem("w");
		dataManagerStr.addDataItem("o");
		dataManagerStr.addDataItem("r");
		dataManagerStr.addDataItem("l");
		dataManagerStr.addDataItem("d");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		dataManagerByteArr.removeOnDataChangedListener();
	}
}

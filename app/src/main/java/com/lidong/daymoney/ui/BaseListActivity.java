package com.lidong.daymoney.ui;

import com.lidong.daymoney.R;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class BaseListActivity extends ListActivity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_layout);
	}
	
	protected Button btnLeft, btnRight;
	protected TextView tvTitle;
	protected BaseAdapter adapter;

	/**
	 * ��ʼ���ؼ�
	 */
	protected void initControl()
	{
		tvTitle = (TextView) findViewById(R.id.tv_title);
		btnLeft = (Button) findViewById(R.id.btn_left);
		btnRight = (Button) findViewById(R.id.btn_right);
	}
}

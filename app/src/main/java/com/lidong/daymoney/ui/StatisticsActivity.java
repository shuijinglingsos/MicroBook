package com.lidong.daymoney.ui;

import com.lidong.daymoney.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class StatisticsActivity extends BaseListActivity implements OnClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initControl();
	}
	
	@Override
	protected void initControl() {
		super.initControl();
		
		this.tvTitle.setText("2014��");
		this.btnLeft.setText(R.string.back);
		this.btnRight.setVisibility(View.GONE);
	}

	public void onClick(View v) {
		
		
	}
}

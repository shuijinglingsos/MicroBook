package com.lidong.daymoney.ui;

import java.util.Calendar;
import java.util.Date;

import com.lidong.daymoney.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class InputDateActivity extends Activity {
	
	public static final String arg_date="date";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_date);
		
		btnBack = (Button) findViewById(R.id.btn_back);
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnOK = (Button) findViewById(R.id.btn_save);
		btnOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				save();
			}
		});

		datePicker=(DatePicker)findViewById(R.id.datePicker1);
		timePicker=(TimePicker)findViewById(R.id.timePicker1);
		timePicker.setIs24HourView(true);
		
		Date date=(Date)getIntent().getSerializableExtra(arg_date);
		
		if (date != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);

			datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH), null);
			timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
		}
	}

	private Button btnBack, btnOK;
	private DatePicker datePicker;
	private TimePicker timePicker;
	
	public void save()
	{
		Calendar date=Calendar.getInstance();
		date.set(Calendar.YEAR, datePicker.getYear());
		date.set(Calendar.MONTH, datePicker.getMonth());
		date.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
		date.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
		date.set(Calendar.MINUTE, timePicker.getCurrentMinute());
		
		//BaseTransactionEditor.transaction.date=date.getTime();
		
		Intent data=new Intent();
		data.putExtra(arg_date, date.getTime());
		
		setResult(Activity.RESULT_OK,data);
		finish();
	}
}

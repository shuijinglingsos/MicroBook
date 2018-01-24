package com.lidong.daymoney.ui;

import com.lidong.daymoney.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class InputRemarkActivity extends Activity {
	
	public static final String arg_remark="remark";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_remark);
		
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

		txtName = (EditText) findViewById(R.id.txt_name);
		
		/*if(BaseTransactionEditor.activity_transaction.remark!=null &&
				!BaseTransactionEditor.activity_transaction.remark.equals(""))
			txtName.setText(BaseTransactionEditor.activity_transaction.remark);*/
		
		String remark=getIntent().getStringExtra(arg_remark);
		if(remark!=null)
			txtName.setText(remark);		
	}

	private Button btnBack, btnOK;
	private EditText txtName;
	
	/**
	 * ���� 
	 */
	private void save()
	{
		String s=txtName.getText().toString().trim();
		
		//BaseTransactionEditor.transaction.remark=s;
		
		Intent data=new Intent();
		data.putExtra(arg_remark, s);
		
		setResult(Activity.RESULT_OK,data);
		finish();
	}
}

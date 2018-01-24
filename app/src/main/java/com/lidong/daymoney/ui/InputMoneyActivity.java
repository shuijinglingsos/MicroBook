package com.lidong.daymoney.ui;

import com.lidong.daymoney.Global;
import com.lidong.daymoney.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InputMoneyActivity extends Activity {
	
	public static final String arg_money="money";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_money);
		
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
		
		double money=getIntent().getDoubleExtra(arg_money, 0);
		if(money>0)
			txtName.setText(Global.df1.format(money));
		
		//if(BaseTransactionEditor.transaction.money!=0)
		//	txtName.setText(Global.df1.format(BaseTransactionEditor.transaction.money));
		
		btn1=(Button) findViewById(R.id.btn_1);
		btn2=(Button) findViewById(R.id.btn_2);
		btn3=(Button) findViewById(R.id.btn_3);
		btn4=(Button) findViewById(R.id.btn_4);
		btn5=(Button) findViewById(R.id.btn_5);
		btn6=(Button) findViewById(R.id.btn_6);
		btn7=(Button) findViewById(R.id.btn_7);
		btn8=(Button) findViewById(R.id.btn_8);
		btn9=(Button) findViewById(R.id.btn_9);
		btn0=(Button) findViewById(R.id.btn_0);
		btnClear=(Button) findViewById(R.id.btn_clear);
		btnEqu=(Button) findViewById(R.id.btn_equ);
		btnSub=(Button) findViewById(R.id.btn_sub);
		btnAdd=(Button) findViewById(R.id.btn_add);
		btnPoint=(Button) findViewById(R.id.btn_point);
		btnClearOne=(Button)findViewById(R.id.btn_clear_one);
		
		btn1.setOnClickListener(calBtnListener);
		btn2.setOnClickListener(calBtnListener);
		btn3.setOnClickListener(calBtnListener);
		btn4.setOnClickListener(calBtnListener);
		btn5.setOnClickListener(calBtnListener);
		btn6.setOnClickListener(calBtnListener);
		btn7.setOnClickListener(calBtnListener);
		btn8.setOnClickListener(calBtnListener);
		btn9.setOnClickListener(calBtnListener);
		btn0.setOnClickListener(calBtnListener);
		btnClear.setOnClickListener(calBtnListener);
		btnEqu.setOnClickListener(calBtnListener);
		btnSub.setOnClickListener(calBtnListener);
		btnAdd.setOnClickListener(calBtnListener);
		btnPoint.setOnClickListener(calBtnListener);
		btnClearOne.setOnClickListener(calBtnListener);
	}

	private Button btnBack, btnOK;
	private EditText txtName;
	
	private Button btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btn0;
	private Button btnClear,btnEqu,btnSub,btnAdd,btnPoint,btnClearOne;
	private String tempStr="", number1="",operation="";
	
	/**
	 * ���� 
	 */
	private void save()
	{
		String s=txtName.getText().toString().trim();
		if(!s.equals("")&&Double.parseDouble(s)>0)
		{
			//BaseTransactionEditor.transaction.money=Double.parseDouble(s);
			
			Intent data=new Intent();
			data.putExtra(arg_money, Double.parseDouble(s));
			
			setResult(Activity.RESULT_OK,data);
			finish();
		}
		else
		{
			Toast.makeText(this, "�����������", Toast.LENGTH_SHORT).show();
		}		
	}
	
	private OnClickListener calBtnListener= new OnClickListener() {
		public void onClick(View v) {
			switch(v.getId())
			{
			case R.id.btn_1:
			case R.id.btn_2:
			case R.id.btn_3:
			case R.id.btn_4:
			case R.id.btn_5:
			case R.id.btn_6:
			case R.id.btn_7:
			case R.id.btn_8:
			case R.id.btn_9:
			case R.id.btn_0:
				
				if(operation.equals(""))
					number1="";
				
				if(tempStr.equals("0"))
					tempStr="";
				tempStr+=((Button)v).getText().toString();
				txtName.setText(tempStr);
				break;
				
			case R.id.btn_add:
			case R.id.btn_sub:
				if(!number1.equals("")&&!tempStr.equals("")&&!operation.equals(""))
				{
					double result=0;
					if(operation.equals("+"))					
						result= Double.parseDouble(number1)+Double.parseDouble(tempStr);
					if(operation.equals("-"))					
						result= Double.parseDouble(number1)-Double.parseDouble(tempStr);
					
					number1=Global.df1.format(result);						
					txtName.setText(number1);
				}
				else if(!tempStr.equals(""))
					number1=tempStr;
				
				tempStr="";
				operation=((Button)v).getText().toString();
				
				break;
				
			case R.id.btn_equ:
				
				if(!number1.equals("")&&!tempStr.equals("")&&!operation.equals(""))
				{
					double result=0;
					if(operation.equals("+"))	
						result= Double.parseDouble(number1)+Double.parseDouble(tempStr);
					if(operation.equals("-"))					
						result= Double.parseDouble(number1)-Double.parseDouble(tempStr);
					
					number1=Global.df1.format(result);						
					txtName.setText(number1);
					tempStr="";
				}
				
				operation="";
				
				break;
				
			case R.id.btn_point:
				
				if (!tempStr.contains(".")) {
					if (tempStr.equals(""))
						tempStr += "0.";
					else
						tempStr += ".";
					txtName.setText(tempStr);
				}
				break;
				
			case R.id.btn_clear_one:
				
				if(!tempStr.equals(""))
				{
					tempStr= tempStr.subSequence(0, tempStr.length()-1).toString();
					txtName.setText(tempStr);
				}	
				break;
				
			case R.id.btn_clear:
				
				tempStr="";
				number1="";
				operation="";
				txtName.setText(tempStr);
				
				break;
			
			}
		}
	};

}

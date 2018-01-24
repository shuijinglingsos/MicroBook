package com.lidong.daymoney.ui;

import java.util.Date;

import com.lidong.daymoney.R;
import com.lidong.daymoney.Transaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class BaseTransactionEditor extends AppCompatActivity {

	protected static final int request_money = 0;
	protected static final int request_date = 1;
	protected static final int request_remark = 2;

	protected ListView mListView;
	protected BaseAdapter adapter;

	protected Transaction transaction;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction_editor);
	}

	protected void initControl() {
		mListView = (ListView) findViewById(R.id.listView);
		mListView.setEmptyView(findViewById(R.id.tv_empty));
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {

			switch (requestCode) {
				case request_money:
					if (transaction != null)
						transaction.money = data.getDoubleExtra(InputMoneyActivity.arg_money, 0);
					break;

				case request_date:
					if (transaction != null)
						transaction.date = (Date) data.getSerializableExtra(InputDateActivity.arg_date);
					break;

				case request_remark:
					if (transaction != null)
						transaction.remark = data.getStringExtra(InputRemarkActivity.arg_remark);
					break;
			}

			if (adapter != null)
				adapter.notifyDataSetChanged();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}

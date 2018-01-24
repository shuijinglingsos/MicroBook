package com.lidong.daymoney.ui;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.lidong.daymoney.Account;
import com.lidong.daymoney.AccountProvider;
import com.lidong.daymoney.Global;
import com.lidong.daymoney.R;
import com.lidong.daymoney.Transaction;
import com.lidong.daymoney.TransactionProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TransferEditor extends BaseTransactionEditor {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		initControl();

		op = getIntent().getIntExtra("op", -1);

		if (op == 0)
			setTitle(R.string.title_transfer);
		else
			setTitle(R.string.title_transfer_modify);

		mListView.setOnItemClickListener(listener);

		initDate();
	}

	private int op;

	private String[] names;
	private int from_account_index, to_account_index;

	private List<Account> listAccount;

	/**
	 * 初始化数据
	 */
	private void initDate() {
		listAccount = new AccountProvider(this).getAll();
		names = getResources().getStringArray(R.array.transfer_files);

		if (op == 0)
			transaction = new Transaction();
		else {

			try {
				long id = getIntent().getLongExtra("id", -1);
				transaction = new TransactionProvider(this).get(id);

				for (int i = 0; i < listAccount.size(); i++) {
					if (transaction.account_id_1 == listAccount.get(i).id) {
						from_account_index = i;
					}
					if (transaction.account_id_2 == listAccount.get(i).id) {
						to_account_index = i;
					}
				}


			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		adapter = new InputListAdapter(this);
		mListView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_transaction_editor, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;

			case R.id.menu_save:
				save();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * listview item 点击事件
	 */
	private OnItemClickListener listener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {

			Intent intent;

			switch (arg2) {

				case 0:
					intent = new Intent(TransferEditor.this, InputMoneyActivity.class);
					intent.putExtra(InputMoneyActivity.arg_money, transaction.money);
					startActivityForResult(intent, request_money);
					break;

				case 1:
					selectAccount("from");
					break;

				case 2:
					selectAccount("to");
					break;

				case 3:
					intent = new Intent(TransferEditor.this, InputDateActivity.class);
					intent.putExtra(InputDateActivity.arg_date, transaction.date);
					startActivityForResult(intent, request_date);
					break;

				case 4:
					intent = new Intent(TransferEditor.this, InputRemarkActivity.class);
					intent.putExtra(InputRemarkActivity.arg_remark, transaction.remark);
					startActivityForResult(intent, request_remark);
					break;
			}

		}
	};

	/**
	 * 选择账户
	 *
	 * @param type
	 */
	private void selectAccount(final String type) {
		if (listAccount.size() == 0) {
			Toast.makeText(this, R.string.no_account_toast, Toast.LENGTH_LONG).show();
			return;
		}

		String[] items = new String[listAccount.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = listAccount.get(i).name;
		}

		int index = type.equals("from") ? from_account_index : to_account_index;
		int resId = type.equals("from") ? R.string.select_from_account : R.string.select_to_account;

		new AlertDialog.Builder(this)
				.setTitle(resId)
				.setSingleChoiceItems(items, index,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int which) {

								if (type.equals("from"))
									from_account_index = which;
								else
									to_account_index = which;

								adapter.notifyDataSetChanged();
								dialog.dismiss();
							}
						}).setNegativeButton(R.string.cancel, null).show();
	}

	/**
	 * 保存
	 */
	private void save() {
		if (from_account_index == -1 || to_account_index == -1) {
			Toast.makeText(this, R.string.transfer_error_1, Toast.LENGTH_SHORT).show();
			return;
		}

		if (from_account_index == to_account_index) {
			Toast.makeText(this, R.string.transfer_error_2, Toast.LENGTH_SHORT).show();
			return;
		}

		if (transaction.money == 0) {
			Toast.makeText(this, R.string.transfer_error_3, Toast.LENGTH_SHORT).show();
			return;
		}

		transaction.account_id_1 = listAccount.get(from_account_index).id;
		transaction.account_id_2 = listAccount.get(to_account_index).id;

		if (op == 0) {
			//添加
			transaction.t_type = 2;
			long id = new TransactionProvider(this).insert(transaction);

			if (id > 0) {

				Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT)
						.show();

				Intent i = new Intent();
				i.putExtra("read", true);
				setResult(Activity.RESULT_OK, i);
				finish();
			} else {
				Toast.makeText(this, R.string.save_failure, Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			//修改
			try {
				// 修改保存
				int result = new TransactionProvider(this).update(transaction);

				if (result > 0) {
					Toast.makeText(this, R.string.save_success,
							Toast.LENGTH_SHORT).show();
					Intent i = new Intent();
					i.putExtra("read", true);
					setResult(Activity.RESULT_OK, i);
					finish();
				} else {
					Toast.makeText(this, R.string.save_failure,
							Toast.LENGTH_SHORT).show();
				}

			} catch (ParseException e) {
				e.printStackTrace();
				Toast.makeText(this, R.string.save_failure,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	private class InputListAdapter extends BaseAdapter {
		private Context _context;
		private LayoutInflater _inflater;

		public InputListAdapter(Context c) {
			_context = c;
			_inflater = LayoutInflater.from(_context);
		}

		public int getCount() {
			return names.length;
		}

		public Object getItem(int position) {
			return names[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			//if (null == convertView)
			convertView = _inflater.inflate(R.layout.input_list_item, null);

			((TextView) convertView.findViewById(R.id.tv_name)).setText(names[position]);

			TextView tv = ((TextView) convertView.findViewById(R.id.tv_value));

			switch (position) {
				case 0:
					tv.setText(Global.df.format(transaction.money));

					int color = getResources().getColor(R.color.transfer_money);
					((TextView) convertView.findViewById(R.id.tv_value)).setTextColor(color);
					convertView.setBackgroundResource(R.drawable.list_item_top_style);

					break;

				case 1:
					if (listAccount.size() > 0)
						tv.setText(listAccount.get(from_account_index).name);
					else {
						from_account_index = -1;
						tv.setText(R.string.no_account);
					}

					convertView.setBackgroundResource(R.drawable.list_item_style);
					break;

				case 2:
					if (listAccount.size() > 0)
						tv.setText(listAccount.get(to_account_index).name);
					else {
						to_account_index = -1;
						tv.setText(R.string.no_account);
					}

					convertView.setBackgroundResource(R.drawable.list_item_style);

					break;

				case 3:
					if (transaction.date == null)
						transaction.date = new Date();
					tv.setText(Global.dateTimeFormatter.format(transaction.date));

					convertView.setBackgroundResource(R.drawable.list_item_last_style);
					break;

				case 4:
					if (transaction.remark == null || transaction.remark.equals(""))
						tv.setText(R.string.transaction_remark_input);
					else
						tv.setText(transaction.remark);
					convertView.setBackgroundResource(R.drawable.list_item_one_style);
					break;
			}

			return convertView;
		}
	}
}

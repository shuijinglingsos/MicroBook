package com.lidong.daymoney.ui;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.lidong.daymoney.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TransactionEditor extends BaseTransactionEditor {

	private int type, op;
	private String[] names;
	private int category_index, account_index;

	private List<Category> listCategory;
	private List<Account> listAccount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		initControl();

		type = getIntent().getIntExtra("type", -1);
		op = getIntent().getIntExtra("op", -1);

		if (type == 0) {
			if (op == 0)
				setTitle(R.string.title_expend_add);
			else
				setTitle(R.string.title_expend_modify);
		} else {
			if (op == 0)
				setTitle(R.string.title_income_add);
			else
				setTitle(R.string.title_income_modify);
		}

		mListView.setOnItemClickListener(listener);
		initDate();
	}

	/**
	 * 初始化数据
	 */
	private void initDate() {
		listCategory = new CategoryProvider(this).getAllByType(type);
		listAccount = new AccountProvider(this).getAll();

		names = getResources().getStringArray(R.array.transaction_files);

		if (op == 0) //添加
		{
			transaction = new Transaction();
			SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
			long category_id, account_id;

			if (type == 0)
				category_id = spf.getLong(Global.EXPEND_CATEGORY_DEFAULT, -1);
			else
				category_id = spf.getLong(Global.INCOME_CATEGORY_DEFAULT, -1);
			category_index = getIndex_CategoryList(category_id);

			account_id = spf.getLong(Global.ACCOUNT_DEFAULT, -1);
			account_index = getIndex_AccountList(account_id);
		} else   //修改
		{
			try {
				long id = getIntent().getLongExtra("id", -1);
				transaction = new TransactionProvider(this).get(id);

				category_index = getIndex_CategoryList(transaction.category_id);
				account_index = getIndex_AccountList(transaction.account_id_1);

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
					intent = new Intent(TransactionEditor.this, InputMoneyActivity.class);
					intent.putExtra(InputMoneyActivity.arg_money, transaction.money);
					startActivityForResult(intent, request_money);
					break;

				case 1:
					selectCategory();
					break;

				case 2:
					selectAccount();
					break;

				case 3:
					intent = new Intent(TransactionEditor.this, InputDateActivity.class);
					intent.putExtra(InputDateActivity.arg_date, transaction.date);
					startActivityForResult(intent, request_date);
					break;

				case 4:
					intent = new Intent(TransactionEditor.this, InputRemarkActivity.class);
					intent.putExtra(InputRemarkActivity.arg_remark, transaction.remark);
					startActivityForResult(intent, request_remark);
					break;
			}

		}
	};

	/**
	 * 保存
	 */
	private void save() {
		if (category_index == -1 || account_index == -1) {
			Toast.makeText(this, R.string.no_select_item, Toast.LENGTH_SHORT).show();
			return;
		}

		transaction.category_id = listCategory.get(category_index).id;
		transaction.account_id_1 = listAccount.get(account_index).id;

		if (op == 0) {
			//添加保存
			transaction.t_type = type;
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
			}
		}
	}

	/**
	 * 选择分类
	 */
	private void selectCategory() {
		if (listCategory.size() == 0) {
			Toast.makeText(this, R.string.no_category_toast, Toast.LENGTH_LONG).show();
			return;
		}

		String[] items = new String[listCategory.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = listCategory.get(i).name;
		}

		new AlertDialog.Builder(this)
				.setTitle(R.string.select_category)
				.setSingleChoiceItems(items, category_index,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int which) {
								category_index = which;
								adapter.notifyDataSetChanged();
								dialog.dismiss();
							}
						}).setNegativeButton(R.string.cancel, null).show();
	}

	/**
	 * 选择账户
	 */
	private void selectAccount() {
		if (listAccount.size() == 0) {
			Toast.makeText(this, R.string.no_account_toast, Toast.LENGTH_LONG).show();
			return;
		}

		String[] items = new String[listAccount.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = listAccount.get(i).name;
		}

		new AlertDialog.Builder(this)
				.setTitle(R.string.select_account)
				.setSingleChoiceItems(items, account_index,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int which) {
								account_index = which;
								adapter.notifyDataSetChanged();
								dialog.dismiss();
							}
						}).setNegativeButton(R.string.cancel, null).show();
	}

	/**
	 * 获取指定id分类在列表中的索引
	 *
	 * @param id id
	 * @return 在列表中的索引，不在列表中则索引0
	 */
	private int getIndex_CategoryList(long id) {
		for (int i = 0; i < listCategory.size(); i++) {
			if (listCategory.get(i).id == id) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 获取指定id账户在列表中的索引
	 *
	 * @param id id
	 * @return 在列表中的索引，不在列表中则索引0
	 */
	private int getIndex_AccountList(long id) {
		for (int i = 0; i < listAccount.size(); i++) {
			if (listAccount.get(i).id == id) {
				return i;
			}
		}
		return 0;
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

			//convertView.findViewById(R.id.imageView1).setVisibility(View.GONE);

			switch (position) {
				case 0:
					tv.setText(Global.df.format(transaction.money));

					int color = getResources().getColor(type == 0 ? R.color.expend_money : R.color.income_money);
					((TextView) convertView.findViewById(R.id.tv_value)).setTextColor(color);

					convertView.setBackgroundResource(R.drawable.list_item_top_style);

					break;

				case 1:
					if (listCategory.size() > 0)
						tv.setText(listCategory.get(category_index).name);
					else {
						category_index = -1;
						tv.setText(R.string.no_category);
					}

					convertView.setBackgroundResource(R.drawable.list_item_style);
					break;

				case 2:
					if (listAccount.size() > 0)
						tv.setText(listAccount.get(account_index).name);
					else {
						account_index = -1;
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

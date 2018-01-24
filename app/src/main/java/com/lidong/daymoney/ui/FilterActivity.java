package com.lidong.daymoney.ui;

import java.util.ArrayList;
import java.util.List;

import com.lidong.daymoney.Account;
import com.lidong.daymoney.AccountProvider;
import com.lidong.daymoney.Category;
import com.lidong.daymoney.CategoryProvider;
import com.lidong.daymoney.R;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 筛选对话框
 * @author Lidong
 *
 */
public class FilterActivity extends AppCompatActivity {

	private String[] names;

	private List<Category> list_income_category;
	private List<Category> list_expend_category;
	private List<Account> list_account;

	private String remark = "";
	private boolean[] income_selected, expend_selected, account_selected;
	private String[] income_text, expend_text, account_text;

	private ListView mListView;
	protected BaseAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		setTitle(R.string.title_filter);

		mListView = (ListView) findViewById(R.id.listView);
		mListView.setEmptyView(findViewById(R.id.tv_empty));
		mListView.setOnItemClickListener(listener);

		names = getResources().getStringArray(R.array.filter_files);
		initData();

		adapter = new InputListAdapter(this);
		mListView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_filter, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				close();
				break;

			case R.id.menu_ok:
				filter();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		close();
	}

	/**
	 * 关闭
	 */
	private void close() {
		finish();
		overridePendingTransition(R.anim.push_empty_out, R.anim.dialog_buttom_out);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {

			switch (requestCode) {
				case 0:
					remark = data.getStringExtra(InputRemarkActivity.arg_remark);
					break;
			}

			if (adapter != null)
				adapter.notifyDataSetChanged();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * listview item 点击事件
	 */
	private OnItemClickListener listener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {

			final MultiChoiceDialog d;

			switch (arg2) {

				case 0:

					d = new MultiChoiceDialog(FilterActivity.this, R.string.select_income_category, income_text, income_selected);
					d.setOKButton(new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							income_selected = d.getCheckedArray();
							adapter.notifyDataSetChanged();
						}
					});
					d.show();

					break;

				case 1:

					d = new MultiChoiceDialog(FilterActivity.this, R.string.select_expend_category, expend_text, expend_selected);
					d.setOKButton(new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							expend_selected = d.getCheckedArray();
							adapter.notifyDataSetChanged();
						}
					});
					d.show();

					break;

				case 2:

					d = new MultiChoiceDialog(FilterActivity.this, R.string.select_account, account_text, account_selected);
					d.setOKButton(new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							account_selected = d.getCheckedArray();
							adapter.notifyDataSetChanged();
						}
					});
					d.show();

					break;

				case 3:

					Intent intent = new Intent(FilterActivity.this, InputRemarkActivity.class);
					intent.putExtra(InputRemarkActivity.arg_remark, remark);
					startActivityForResult(intent, 0);

					break;

			}

		}
	};

	/**
	 * 初始化数据
	 */
	private void initData() {
		list_income_category = new CategoryProvider(this).getAllByType(1);
		list_expend_category = new CategoryProvider(this).getAllByType(0);
		list_account = new AccountProvider(this).getAll();

		income_text = new String[list_income_category.size() + 1];
		income_text[0] = "全部";
		for (int i = 0; i < list_income_category.size(); i++) {
			income_text[i + 1] = list_income_category.get(i).name;
		}

		expend_text = new String[list_expend_category.size() + 1];
		expend_text[0] = "全部";
		for (int i = 0; i < list_expend_category.size(); i++) {
			expend_text[i + 1] = list_expend_category.get(i).name;
		}

		account_text = new String[list_account.size() + 1];
		account_text[0] = "全部";
		for (int i = 0; i < list_account.size(); i++) {
			account_text[i + 1] = list_account.get(i).name;
		}


		income_selected = getIntent().getBooleanArrayExtra("income");
		if (income_selected == null) {
			income_selected = new boolean[income_text.length];
			setAllBoolean(income_selected, true);
		}

		expend_selected = getIntent().getBooleanArrayExtra("expend");
		if (expend_selected == null) {
			expend_selected = new boolean[expend_text.length];
			setAllBoolean(expend_selected, true);
		}

		account_selected = getIntent().getBooleanArrayExtra("account");
		if (account_selected == null) {
			account_selected = new boolean[account_text.length];
			setAllBoolean(account_selected, true);
		}

		remark = getIntent().getStringExtra("remark");
	}

	private void setAllBoolean(boolean[] array, boolean value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = value;
		}
	}

	private void filter() {
		List<Long> category_id = null, account_id = null;

		if (!income_selected[0] || !expend_selected[0]) {
			category_id = new ArrayList<Long>();

			for (int i = 1; i < income_selected.length; i++) {
				if (income_selected[i])
					category_id.add(list_income_category.get(i - 1).id);
			}

			for (int i = 1; i < expend_selected.length; i++) {
				if (expend_selected[i])
					category_id.add(list_expend_category.get(i - 1).id);
			}

			if (category_id.size() == 0) {
				Toast.makeText(this, "收入/支出分类至少选择一个", Toast.LENGTH_SHORT)
						.show();
				return;
			}
		}

		//账户
		if (!account_selected[0]) {
			account_id = new ArrayList<Long>();
			for (int i = 1; i < account_selected.length; i++) {
				if (account_selected[i])
					account_id.add(list_account.get(i - 1).id);
			}

			if (account_id.size() == 0) {
				Toast.makeText(this, "账户至少选择一个", Toast.LENGTH_SHORT).show();
				return;
			}
		}

		String category_where = null;
		if (category_id != null) {
			String temp = category_id.toString().replace('[', '(').replace(']', ')');
			category_where = "(category_id in " + temp + " or category_id is null)";
		}

		String account_where = null;
		if (account_id != null) {
			String temp = account_id.toString().replace('[', '(').replace(']', ')');
			account_where = "(account_id_1 in " + temp + " or account_id_2 in " + temp + ")";
		}

		String result = "";

		if (category_where != null)
			result = category_where;

		if (account_where != null) {
			if (!result.equals(""))
				result += " and " + account_where;
			else
				result = account_where;
		}

		if (!remark.equals("")) {
			if (!result.equals(""))
				result += " and ";

			result += "(remark like '%" + remark + "%')";
		}
		Log.v("sql", result);

		Intent data = new Intent();
		data.putExtra("where", result);
		data.putExtra("income", income_selected);
		data.putExtra("expend", expend_selected);
		data.putExtra("account", account_selected);
		data.putExtra("remark", remark);

		setResult(RESULT_OK, data);

		close();
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
			convertView = _inflater.inflate(R.layout.input_list_item, parent, false);

			((TextView) convertView.findViewById(R.id.tv_name)).setText(names[position]);

			TextView tv = ((TextView) convertView.findViewById(R.id.tv_value));

			//convertView.findViewById(R.id.imageView1).setVisibility(View.GONE);

			String temp = "";
			List<String> listString;

			switch (position) {
				case 0:

					if (income_selected[0]) {
						temp = "全部";
					} else {
						listString = new ArrayList<String>();
						for (int i = 1; i < income_selected.length; i++) {
							if (income_selected[i])
								listString.add(list_income_category.get(i - 1).name);
						}
						if (listString.size() == 0)
							temp = "未选";
						else
							temp = listString.toString().replace("[", "").replace("]", "");
					}

					tv.setText(temp);

					convertView.setBackgroundResource(R.drawable.list_item_top_style);

					break;

				case 1:

					if (expend_selected[0]) {
						temp = "全部";
					} else {
						listString = new ArrayList<String>();
						for (int i = 1; i < expend_selected.length; i++) {
							if (expend_selected[i])
								listString.add(list_expend_category.get(i - 1).name);
						}
						if (listString.size() == 0)
							temp = "未选";
						else
							temp = listString.toString().replace("[", "").replace("]", "");
					}

					tv.setText(temp);

					convertView.setBackgroundResource(R.drawable.list_item_style);
					break;

				case 2:

					if (account_selected[0]) {
						temp = "全部";
					} else {
						listString = new ArrayList<String>();
						for (int i = 1; i < account_selected.length; i++) {
							if (account_selected[i])
								listString.add(list_account.get(i - 1).name);
						}
						if (listString.size() == 0)
							temp = "未选";
						else
							temp = listString.toString().replace("[", "").replace("]", "");
					}

					tv.setText(temp);


					convertView.setBackgroundResource(R.drawable.list_item_last_style);

					break;

				case 3:
					if (remark.equals(""))
						tv.setText(R.string.transaction_remark_input);
					else
						tv.setText(remark);

					convertView.setBackgroundResource(R.drawable.list_item_one_style);

					break;
			}

			return convertView;
		}
	}
}

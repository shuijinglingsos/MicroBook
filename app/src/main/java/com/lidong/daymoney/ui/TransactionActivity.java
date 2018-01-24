package com.lidong.daymoney.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lidong.daymoney.Account;
import com.lidong.daymoney.AccountProvider;
import com.lidong.daymoney.Global;
import com.lidong.daymoney.R;
import com.lidong.daymoney.Transaction;
import com.lidong.daymoney.TransactionProvider;
import com.lidong.daymoney.TransactionSum;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TransactionActivity extends AppCompatActivity {

	public static final int SHOW_ALL = 0;
	public static final int SHOW_YEAR = 1;
	public static final int SHOW_MONTH = 2;
	public static final int SHOW_WEEK = 3;
	public static final int SHOW_DAY = 4;

	private int show_type;
	private View layout_sum, layout_btns;
	private TextView tv_date, tv_expend, tv_income;
	private Button btnforward, btnbackward;
	private ListView mListView;
	private BaseAdapter adapter;
	private Date start, end;
	private int year, month;
	private boolean modify_flag = false;
	private Map<Long, String> accountMap;

	private TransactionProvider db = new TransactionProvider(this);
	private List<Transaction> listTransaction;
	private List<TransactionSum> listTransactionSum;

	private String sql_where;
	private String remark = "";
	private boolean[] income_selected, expend_selected, account_selected;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		show_type = getIntent().getIntExtra("show_type", -1);

		init();

		try {
			sql_where = getIntent().getStringExtra("where");
			this.setDate();
			this.showTitle();
			this.showSum();
			this.showListData();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void init() {
		tv_date = (TextView) this.findViewById(R.id.tv_date);
		tv_expend = (TextView) this.findViewById(R.id.tv_expend);
		tv_income = (TextView) this.findViewById(R.id.tv_income);

		mListView = (ListView) findViewById(R.id.listView);
		mListView.setEmptyView(findViewById(R.id.tv_empty));

		mListView.setOnItemClickListener(listItemClick);

		layout_sum = this.findViewById(R.id.layout_sum);
		layout_btns = this.findViewById(R.id.btns_layout);

		btnforward = (Button) this.findViewById(R.id.btn_forward);
		btnforward.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				forward();
			}
		});

		btnbackward = (Button) this.findViewById(R.id.btn_backward);
		btnbackward.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				backward();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_transaction, menu);

		switch (show_type) {
			case SHOW_MONTH:
			case SHOW_WEEK:
			case SHOW_DAY:
				menu.findItem(R.id.menu_filter).setVisible(false);
				break;
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				close();
				break;

			case R.id.menu_new:
				recode_one();
				break;

			case R.id.menu_filter:
				filter();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 载入上下文菜单
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenu.ContextMenuInfo menuInfo) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.transaction_list_context, menu);
	}

	// 上下文菜单点击事件
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		final int index = info.position;

		switch (item.getItemId()) {
			case R.id.context_edit: // 编辑
				showEditor(index);
				break;

			case R.id.context_delete: // 删除

				new AlertDialog.Builder(this)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setTitle(listTransaction.get(index).ex_categroy_name)
						.setMessage(R.string.delete_affirm)
						.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								delete(index);
							}
						})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();

				break;

			case R.id.context_share:  //分享
				share(index);
				break;
		}

		return true;
	}

	// @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {

			switch (requestCode) {
				case 0:
					if (data.getBooleanExtra("read", false)) {
						try {
							showSum();
							showListData();
							adapter.notifyDataSetChanged();
							modify_flag = true;
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					break;

				case 1:
					try {
						sql_where = data.getStringExtra("where");
						income_selected = data.getBooleanArrayExtra("income");
						expend_selected = data.getBooleanArrayExtra("expend");
						account_selected = data.getBooleanArrayExtra("account");
						remark = data.getStringExtra("remark");
						showTitle();
						showListData();
						adapter.notifyDataSetChanged();
					} catch (ParseException e) {
						e.printStackTrace();
					}
					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		close();
	}

	/**
	 * 点击列表项事件
	 */
	public OnItemClickListener listItemClick = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {

			Intent i;
			TransactionSum temp;

			switch (show_type) {
				case SHOW_ALL:
					temp = listTransactionSum.get(arg2);
					i = new Intent(TransactionActivity.this, TransactionActivity.class);
					i.putExtra("show_type", TransactionActivity.SHOW_YEAR);
					i.putExtra("year", Integer.parseInt(temp.date));
					i.putExtra("where", sql_where);
					startActivityForResult(i, 0);
					break;

				case SHOW_YEAR:
					temp = listTransactionSum.get(arg2);
					i = new Intent(TransactionActivity.this, TransactionActivity.class);
					i.putExtra("show_type", TransactionActivity.SHOW_MONTH);
					i.putExtra("year", year);
					i.putExtra("month", Integer.parseInt(temp.date));
					i.putExtra("where", sql_where);
					startActivityForResult(i, 0);
					break;

				case SHOW_MONTH:
				case SHOW_WEEK:
				case SHOW_DAY:
					showEditor(arg2);
					break;
			}

		}
	};

	/**
	 * 设置日期
	 *
	 * @throws ParseException e
	 */
	private void setDate() throws ParseException {
		Calendar date;

		switch (show_type) {
			case SHOW_ALL:

				break;

			case SHOW_YEAR:

				year = getIntent().getIntExtra("year", -1);

				date = Calendar.getInstance();
				date.set(year, 1 - 1, 1, 0, 0, 0);
				start = date.getTime();
				date.add(Calendar.YEAR, 1);
				end = date.getTime();

				break;

			case SHOW_MONTH:

				year = getIntent().getIntExtra("year", -1);
				month = getIntent().getIntExtra("month", -1);

				date = Calendar.getInstance();
				date.set(year, month - 1, 1, 0, 0, 0);
				start = date.getTime();
				date.add(Calendar.MONTH, 1);
				end = date.getTime();

				break;

			case SHOW_WEEK:
			case SHOW_DAY:

				start = Global.dateTimeFormatter.parse(getIntent().getStringExtra("start"));
				end = Global.dateTimeFormatter.parse(getIntent().getStringExtra("end"));

				break;
		}
	}

	/**
	 * 显示标题
	 */
	private void showTitle() {
		switch (show_type) {
			case SHOW_ALL:
				setTitle(R.string.title_running);
				break;

			case SHOW_YEAR:
				if (sql_where != null && !sql_where.equals(""))
					setTitle(year + "年" + getResources().getString(R.string.filted));
				else
					setTitle(year + "年");
				break;

			case SHOW_MONTH:
				if (sql_where != null && !sql_where.equals(""))
					setTitle(year + "年" + month + "月" + getResources().getString(R.string.filted));
				else
					setTitle(year + "年" + month + "月");
				break;

			case SHOW_WEEK:
				setTitle(R.string.title_running_week);
				break;

			case SHOW_DAY:
				setTitle(Global.dateFormatter.format(start));
				break;
		}
	}

	/**
	 * 显示汇总
	 */
	private void showSum() {
		Calendar c;

		switch (show_type) {
			case SHOW_ALL:
				layout_sum.setVisibility(View.GONE);
				layout_btns.setVisibility(View.GONE);
				return;

			case SHOW_YEAR:

				c = Calendar.getInstance();
				c.setTime(end);
				c.add(Calendar.DAY_OF_YEAR, -1);
				tv_date.setText(Global.dateFormatter.format(start) + " - " + Global.dateFormatter.format(c.getTime()));

				break;

			case SHOW_MONTH:
			case SHOW_WEEK:

//				btnfilter.setVisibility(View.GONE);

				c = Calendar.getInstance();
				c.setTime(end);
				c.add(Calendar.DAY_OF_MONTH, -1);
				tv_date.setText(Global.dateFormatter.format(start) + " - " + Global.dateFormatter.format(c.getTime()));

				break;

			case SHOW_DAY:

//				btnfilter.setVisibility(View.GONE);

				tv_date.setText(Global.dateFormatter3.format(start));
				break;
		}

		double[] sum = db.getSum(start, end);
		tv_expend.setText(Global.df.format(sum[0]));
		tv_income.setText(Global.df.format(sum[1]));
	}

	/**
	 * 显示列表数据
	 *
	 * @throws ParseException e
	 */
	private void showListData() throws ParseException {
		switch (show_type) {
			case SHOW_ALL:
				listTransactionSum = db.getSumByAll();
				break;

			case SHOW_YEAR:
				listTransactionSum = db.getSumByYear(year);
				break;

			case SHOW_MONTH:
			case SHOW_WEEK:

				listTransaction = new ArrayList<Transaction>();
				List<Transaction> tempList = db.get(start, end, sql_where);

				getAcouuntList();

				String date = "";
				for (int i = 0; i < tempList.size(); i++) {
					String temp = Global.dateFormatter.format(tempList.get(i).date);
					if (!temp.equals(date)) {
						Transaction t = new Transaction();
						t.id = -1;
						t.date = tempList.get(i).date;

						listTransaction.add(t);
						date = temp;
					}
					listTransaction.add(tempList.get(i));
				}

				this.registerForContextMenu(mListView);

				break;

			case SHOW_DAY:
				getAcouuntList();
				listTransaction = db.get(start, end, sql_where);
				this.registerForContextMenu(mListView);
				break;
		}

		updateListView();
	}

	/**
	 * 更新列表数据
	 */
	private void updateListView() {
		if (adapter == null) {
			switch (show_type) {
				case SHOW_ALL:
				case SHOW_YEAR:
					adapter = new TransactionSumListAdapter(this);
					break;

				case SHOW_MONTH:
				case SHOW_WEEK:
				case SHOW_DAY:
					adapter = new TransactionListAdapter(this);
					break;
			}

			mListView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}


	/**
	 * 删除
	 *
	 * @param index 索引
	 */
	private void delete(int index) {
		if (new TransactionProvider(this).delete(listTransaction.get(index)) > 0) {
			try {
				showSum();
				showListData();
				adapter.notifyDataSetChanged();

				modify_flag = true;

				Toast.makeText(this, R.string.delete_success,
						Toast.LENGTH_SHORT).show();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		} else {
			Toast.makeText(this, R.string.delete_failure,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 显示编辑器
	 *
	 * @param index 索引
	 */
	private void showEditor(int index) {
		Transaction temp = listTransaction.get(index);

		if (temp.t_type == 3) {
			Toast.makeText(this, R.string.account_balance_no_edit, Toast.LENGTH_SHORT).show();
			return;
		}

		Intent data = null;
		if (temp.t_type == 0 || temp.t_type == 1) {
			data = new Intent(this, TransactionEditor.class);
			data.putExtra("type", temp.t_type);
			data.putExtra("op", 1);
			data.putExtra("id", temp.id);
		}
		if (temp.t_type == 2) {
			data = new Intent(this, TransferEditor.class);
			data.putExtra("op", 1);
			data.putExtra("id", temp.id);
		}

		startActivityForResult(data, 0);
	}

	/**
	 * 分享
	 */
	private void share(int index) {
		Transaction t = listTransaction.get(index);

		String text;
		text = Global.dateFormatter2.format(t.date) + " ";
		switch (t.t_type) {
			case 0:
				text += t.ex_categroy_name + " 花费" + t.money + "元";
				break;

			case 1:
				text += t.ex_categroy_name + " 收入" + t.money + "元";
				break;

			case 2:
				text += accountMap.get(t.account_id_1) + " -> " + accountMap.get(t.account_id_2) + " 转账" + t.money + "元";
				break;

			case 3:
				if (t.money < 0)
					text += accountMap.get(t.account_id_1) + " 账户余额 减少" + -t.money + "元";
				else
					text += accountMap.get(t.account_id_1) + " 账户余额 增加" + t.money + "元";
				break;

		}
		if (t.remark != null && !t.remark.equals(""))
			text += " \n" + t.remark;

		text += "\n[来自:" + getResources().getString(R.string.app_name) + "]";

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");  //纯文本
		intent.putExtra(Intent.EXTRA_SUBJECT, R.string.menu_share);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(Intent.createChooser(intent, "分享"));
	}

	/**
	 * 向前
	 */
	private void forward() {
		Calendar date;

		switch (show_type) {
			case SHOW_ALL:

				break;

			case SHOW_YEAR:
				year--;
				date = Calendar.getInstance();
				date.set(year, 0, 1, 0, 0, 0);
				start = date.getTime();
				date.add(Calendar.YEAR, 1);
				end = date.getTime();
				break;

			case SHOW_MONTH:
				if (month == 1) {
					year--;
					month = 12;
				} else
					month--;

				date = Calendar.getInstance();
				date.set(year, month - 1, 1, 0, 0, 0);
				start = date.getTime();
				date.add(Calendar.MONTH, 1);
				end = date.getTime();

				break;
			case SHOW_WEEK:

				date = Calendar.getInstance();

				date.setTime(start);
				date.add(Calendar.DAY_OF_MONTH, -7);
				start = date.getTime();

				date.setTime(end);
				date.add(Calendar.DAY_OF_MONTH, -7);
				end = date.getTime();

				break;
			case SHOW_DAY:

				date = Calendar.getInstance();

				date.setTime(start);
				date.add(Calendar.DAY_OF_MONTH, -1);
				start = date.getTime();

				date.setTime(end);
				date.add(Calendar.DAY_OF_MONTH, -1);
				end = date.getTime();

				break;
		}

		try {
			showTitle();
			showSum();
			showListData();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 向后
	 */
	private void backward() {

		Calendar date;

		switch (show_type) {
			case SHOW_ALL:

				break;

			case SHOW_YEAR:
				year++;
				date = Calendar.getInstance();
				date.set(year, 0, 1, 0, 0, 0);
				start = date.getTime();
				date.add(Calendar.YEAR, 1);
				end = date.getTime();
				break;

			case SHOW_MONTH:

				if (month == 12) {
					year++;
					month = 1;
				} else
					month++;

				date = Calendar.getInstance();
				date.set(year, month - 1, 1, 0, 0, 0);
				start = date.getTime();
				date.add(Calendar.MONTH, 1);
				end = date.getTime();

				break;

			case SHOW_WEEK:

				date = Calendar.getInstance();

				date.setTime(start);
				date.add(Calendar.DAY_OF_MONTH, +7);
				start = date.getTime();

				date.setTime(end);
				date.add(Calendar.DAY_OF_MONTH, +7);
				end = date.getTime();

				break;

			case SHOW_DAY:

				date = Calendar.getInstance();

				date.setTime(start);
				date.add(Calendar.DAY_OF_MONTH, +1);
				start = date.getTime();

				date.setTime(end);
				date.add(Calendar.DAY_OF_MONTH, +1);
				end = date.getTime();

				break;
		}

		try {
			showTitle();
			showSum();
			showListData();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 记一笔
	 */
	private void recode_one() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.recode_one)
				.setItems(R.array.recode_one, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {

						Intent i;
						switch (arg1) {
							case 0:
								i = new Intent(TransactionActivity.this, TransactionEditor.class);
								i.putExtra("type", 0);
								i.putExtra("op", 0);
								startActivityForResult(i, 0);
								break;

							case 1:
								i = new Intent(TransactionActivity.this, TransactionEditor.class);
								i.putExtra("type", 1);
								i.putExtra("op", 0);
								startActivityForResult(i, 0);
								break;

							case 2:
								i = new Intent(TransactionActivity.this, TransferEditor.class);
								i.putExtra("op", 0);
								startActivityForResult(i, 0);
								break;
						}

					}
				})
				.setNegativeButton(R.string.cancel, null).show();
	}

	/**
	 * 筛选
	 */
	private void filter() {
		Intent i = new Intent(this, FilterActivity.class);
		i.putExtra("income", income_selected);
		i.putExtra("expend", expend_selected);
		i.putExtra("account", account_selected);
		i.putExtra("remark", remark);

		startActivityForResult(i, 1);

		overridePendingTransition(R.anim.dialog_buttom_in, R.anim.push_empty_out);
	}

	/**
	 * 获取账户列表
	 */
	private void getAcouuntList() {
		ArrayList<Account> list = new AccountProvider(this).getAll();
		accountMap = new HashMap<>();
		for (Account a : list) {
			accountMap.put(a.id, a.name);
		}
	}

	/**
	 * 关闭窗口
	 */
	private void close() {
		Intent data = new Intent();
		data.putExtra("read", modify_flag);
		setResult(Activity.RESULT_OK, data);
		finish();
	}

	private class TransactionListAdapter extends BaseAdapter {
		private Context _context;
		private LayoutInflater _inflater;

		private static final int TYPE_ITEM = 0;
		private static final int TYPE_SEPARATOR = 1;
		private static final int TYPE_MAX_COUNT = 2;

		public TransactionListAdapter(Context c) {
			_context = c;
			_inflater = LayoutInflater.from(_context);
		}

		public int getCount() {
			return listTransaction.size();
		}

		public Object getItem(int arg0) {
			return listTransaction.get(arg0);
		}

		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return listTransaction.get(position).id == -1 ? TYPE_SEPARATOR : TYPE_ITEM;
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_MAX_COUNT;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}


		// * 是分隔符返回false 不是分隔符返回true (non-Javadoc)
		// *
		// * @see android.widget.BaseAdapter#isEnabled(int)
		//
		@Override
		public boolean isEnabled(int position) {
			return listTransaction.get(position).id != -1;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			Transaction temp = listTransaction.get(position);

			switch (getItemViewType(position)) {
				case TYPE_SEPARATOR:

					if (convertView == null) {
						convertView = _inflater.inflate(R.layout.list_separator, null);
					}

					((TextView) convertView.findViewById(R.id.textView1)).setText(Global.dateFormatter3
							.format(temp.date).substring(8));

					break;

				case TYPE_ITEM:

					if (convertView == null) {
						convertView = _inflater.inflate(R.layout.transaction_item, parent, false);
					}

					TextView tv;

					//类别
					tv = (TextView) convertView.findViewById(R.id.tv_category);
					if (temp.t_type == 0 || temp.t_type == 1) {
						tv.setText(temp.ex_categroy_name);
					}
					if (temp.t_type == 2) {
						tv.setText(accountMap.get(temp.account_id_1) + " -> " + accountMap.get(temp.account_id_2));
					}
					if (temp.t_type == 3) {
						tv.setText(accountMap.get(temp.account_id_1));
					}

					//备注
					tv = (TextView) convertView.findViewById(R.id.tv_remark);
					if (temp.t_type == 3)  //变更余额
					{
						tv.setText(R.string.account_balance_modify);
					} else if (temp.remark == null || temp.remark.equals(""))
						tv.setText(Global.dateFormatter2.format(temp.date));
					else {
//					if(temp.remark.length()<=13)
						tv.setText(temp.remark);
//					else
//						tv.setText(temp.remark.substring(0, 14).replace("\n", " ")+"...");
					}

					//金额
					tv = (TextView) convertView.findViewById(R.id.tv_money);
					if (temp.t_type == 0) {
						tv.setText("- " + Global.df.format(temp.money));
						tv.setTextColor(getResources().getColor(
								R.color.expend_money));
					}
					if (temp.t_type == 1) {
						tv.setText("+ " + Global.df.format(temp.money));
						tv.setTextColor(getResources().getColor(
								R.color.income_money));
					}
					if (temp.t_type == 2) {
						tv.setText(Global.df.format(temp.money));
						tv.setTextColor(getResources().getColor(
								R.color.transfer_money));
					}
					if (temp.t_type == 3) {
						if (temp.money < 0)
							tv.setText("- " + Global.df.format(-temp.money));
						else
							tv.setText("+ " + Global.df.format(temp.money));
						tv.setTextColor(getResources().getColor(
								R.color.balance_change_money));
					}

					//箭头
					if (temp.t_type == 3) {
						convertView.findViewById(R.id.imageView1).setVisibility(View.INVISIBLE);
					} else
						convertView.findViewById(R.id.imageView1).setVisibility(View.VISIBLE);

					break;
			}

			return convertView;
		}
	}

	private class TransactionSumListAdapter extends BaseAdapter {
		private Context _context;
		private LayoutInflater _inflater;

		public TransactionSumListAdapter(Context c) {
			_context = c;
			_inflater = LayoutInflater.from(_context);
		}

		public int getCount() {
			return listTransactionSum.size();
		}

		public Object getItem(int arg0) {
			return listTransactionSum.get(arg0);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = _inflater.inflate(R.layout.transaction_sum_item, parent,false);
			}

			TransactionSum temp = listTransactionSum.get(position);

			TextView tv;

			tv = (TextView) convertView.findViewById(R.id.tv_name);
			if (show_type == TransactionActivity.SHOW_ALL)
				tv.setText(temp.date + "年");
			if (show_type == TransactionActivity.SHOW_YEAR)
				tv.setText(temp.date + "月");

			tv = (TextView) convertView.findViewById(R.id.tv_expend);
			tv.setText("- " + Global.df.format(temp.expend));

			tv = (TextView) convertView.findViewById(R.id.tv_income);
			tv.setText("+ " + Global.df.format(temp.income));

			return convertView;
		}
	}

}

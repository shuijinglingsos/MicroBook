package com.lidong.daymoney.ui;

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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 账户界面
 */
public class AccountActivity extends AppCompatActivity {

	private ListView mListView;

	private List<Account> listAccount;
	private double sum;
	protected BaseAdapter adapter;

	private SharedPreferences spf;
	private String default_text;
	private long default_id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		setTitle(R.string.title_account);
		initControl();

		spf= PreferenceManager.getDefaultSharedPreferences(this);
		default_text=getResources().getString(R.string.default_text);
		default_id=spf.getLong(Global.ACCOUNT_DEFAULT, -1);

		getData();
		showData();
	}

	protected void initControl() {
		mListView = (ListView) findViewById(R.id.listView);
		mListView.setEmptyView(findViewById(R.id.tv_empty));
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				showEditor(1, arg2);
			}
		});
		this.registerForContextMenu(mListView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_account, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home:
				finish();
				break;

			case R.id.menu_new:
				showEditor(0, -1);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 载入上下文菜单
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenu.ContextMenuInfo menuInfo) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		if (info.position > 0) {
			MenuInflater inflater = this.getMenuInflater();
			inflater.inflate(R.menu.activity_account_list_context, menu);
		}
	}

	// 上下文菜单点击事件
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int index = info.position;

		switch (item.getItemId()) {
			case R.id.context_edit: // 编辑
				showEditor(1, index);
				break;

			case R.id.context_delete: // 删除
				delete(index);
				break;

			case R.id.context_set_default: //设为默认
				setDefault(index);
				break;
		}

		return true;
	}

	// @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			getData();
			adapter.notifyDataSetChanged();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 获取数据
	 */
	public void getData() {
		AccountProvider provider=new AccountProvider(this);

		sum=provider.getSum();
		listAccount = provider.getAll();
	}

	/**
	 * 显示数据
	 */
	public void showData() {
		adapter = new AccountListAdapter(this);
		mListView.setAdapter(adapter);
	}

	/**
	 * 删除账户
	 *
	 * @param index
	 *            选定的索引
	 */
	public void delete(final int index) {
		final Account c = listAccount.get(index-1);

		int count = new TransactionProvider(this)
				.getCount(Transaction.ACCOUNT_ID_1 + "=" + c.id + " or "+
						Transaction.ACCOUNT_ID_2 + "=" + c.id);

		if (count > 0) {
			new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info)
					.setTitle(R.string.dialog_title_hint)
					.setMessage(R.string.account_hint)
					.setPositiveButton(R.string.ok, null).show();
		} else {

			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle(c.name)
					.setMessage(R.string.delete_affirm)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							if (new AccountProvider(AccountActivity.this).delete(c.id) > 0) {
								listAccount.remove(index-1);
								Toast.makeText(AccountActivity.this, R.string.delete_success,Toast.LENGTH_SHORT).show();
								getData();
								adapter.notifyDataSetChanged();
							} else {
								Toast.makeText(AccountActivity.this, R.string.delete_failure,Toast.LENGTH_SHORT).show();
							}
						}})
					.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();
		}
	}

	/**
	 * 显示编辑器
	 *
	 * @param op
	 *            0是添加 1修改
	 * @param index
	 *            修改时的项目索引,添加时不用
	 */
	public void showEditor(int op, int index) {
		if(index==0)
			return;

		Intent i = new Intent(this, AccountEditorActivity.class);
		i.putExtra("op", op);
		if (op == 1)
			i.putExtra("id", listAccount.get(index-1).id);
		startActivityForResult(i, 0);
	}

	/**
	 * 设为默认账户
	 * @param index
	 */
	private void setDefault(int index)
	{
		long id=listAccount.get(index-1).id;

		Editor editor= spf.edit();
		editor.putLong(Global.ACCOUNT_DEFAULT, id);
		editor.commit();

		default_id=id;
		adapter.notifyDataSetChanged();
	}

	private class AccountListAdapter extends BaseAdapter {
		private Context _context;
		private LayoutInflater _inflater;

		public AccountListAdapter(Context c) {
			_context = c;
			_inflater = LayoutInflater.from(_context);
		}

		public int getCount() {
			return listAccount.size()+1;
		}

		public Object getItem(int position) {
			if(position==0)
				return sum;
			return listAccount.get(position-1);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			if (null == convertView)
				convertView = _inflater.inflate(R.layout.account_item, null);

			if (position == 0) {

				int resId=sum<0?R.color.expend_money:R.color.income_money;

				TextView tv = (TextView) convertView.findViewById(R.id.tv_name);
				tv.setText(R.string.account_sum);
				tv.setTextColor(getResources().getColor(resId));

				tv = (TextView) convertView.findViewById(R.id.tv_balance);
				tv.setText(Global.df.format(sum));
				tv.setTextColor(getResources().getColor(resId));

				convertView.findViewById(R.id.imageView1).setVisibility(View.INVISIBLE);
			} else {

				Account account = listAccount.get(position-1);

				TextView tv;

				tv = (TextView) convertView.findViewById(R.id.tv_name);
				tv.setTextColor(getResources().getColor(R.color.black));
				if (account.id == default_id)
					tv.setText(account.name + default_text);
				else
					tv.setText(account.name);

				tv = (TextView) convertView.findViewById(R.id.tv_balance);
				tv.setTextColor(getResources().getColor(R.color.black));
				tv.setText(Global.df.format(account.balance));

				convertView.findViewById(R.id.imageView1).setVisibility(View.VISIBLE);
			}
			return convertView;
		}
	}

}

package com.lidong.daymoney.ui;

import java.util.List;

import com.lidong.daymoney.Category;
import com.lidong.daymoney.CategoryProvider;
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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 收入/支出分类
 */
public class CategoryActivity extends AppCompatActivity {

	/**
	 * 0是支出 1是收入
	 */
	private int category;
	public static List<Category> listCategory;

	private SharedPreferences spf;
	private String default_text;
	private long default_id;

	private ListView mListView;
	private BaseAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		init();
		category = getIntent().getIntExtra("category", -1);

		// category==0 支出 category==1 收入
		if (category == 0) {
			setTitle(R.string.title_category_expend);
		} else {
			setTitle(R.string.title_category_income);
		}

		spf = PreferenceManager.getDefaultSharedPreferences(this);
		default_text = getResources().getString(R.string.default_text);

		getData();
	}

	private void init() {
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
		getMenuInflater().inflate(R.menu.activity_category, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;

			case R.id.menu_new:
				showEditor(0, -1);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		listCategory = null;
		super.onBackPressed();
	}

	// 载入上下文菜单
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenu.ContextMenuInfo menuInfo) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.activity_category_list_context, menu);
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
			adapter.notifyDataSetChanged();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 获取并显示数据
	 */
	private void getData() {
		listCategory = new CategoryProvider(this).getAllByType(category);

		if (category == 0)
			default_id = spf.getLong(Global.EXPEND_CATEGORY_DEFAULT, -1);
		else
			default_id = spf.getLong(Global.INCOME_CATEGORY_DEFAULT, -1);

		adapter = new CategoryListAdapter(this);
		mListView.setAdapter(adapter);
	}

	/**
	 * 删除分类
	 *
	 * @param index 选定的索引
	 */
	private void delete(final int index) {
		final Category c = listCategory.get(index);

		int count = new TransactionProvider(this)
				.getCount(Transaction.CATEGORY_ID + "=" + c.id);

		if (count > 0) {
			new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info)
					.setTitle(R.string.dialog_title_hint)
					.setMessage(R.string.category_hint)
					.setPositiveButton(R.string.ok, null).show();
		} else {

			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle(c.name)
					.setMessage(R.string.delete_affirm)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (new CategoryProvider(CategoryActivity.this).delete(c.id) > 0) {
								listCategory.remove(index);
								Toast.makeText(CategoryActivity.this, R.string.delete_success,
										Toast.LENGTH_SHORT).show();
								adapter.notifyDataSetChanged();
							} else {
								Toast.makeText(CategoryActivity.this, R.string.delete_failure,
										Toast.LENGTH_SHORT).show();
							}
						}
					})
					.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();
		}
	}

	/**
	 * 显示编辑器
	 *
	 * @param op    0是添加 1修改
	 * @param index 修改时的项目索引,添加时不用
	 */
	private void showEditor(int op, int index) {
		Intent i = new Intent(this, CategoryEditorActivity.class);
		i.putExtra("category", category);
		i.putExtra("op", op);
		if (op == 1)
			i.putExtra("index", index);
		startActivityForResult(i, 0);
	}

	/**
	 * 设为默认分类
	 *
	 * @param index 索引
	 */
	private void setDefault(int index) {
		Editor editor = spf.edit();
		if (category == 0)
			editor.putLong(Global.EXPEND_CATEGORY_DEFAULT, listCategory.get(index).id);
		else
			editor.putLong(Global.INCOME_CATEGORY_DEFAULT, listCategory.get(index).id);
		editor.commit();

		default_id = listCategory.get(index).id;
		adapter.notifyDataSetChanged();
	}

	private class CategoryListAdapter extends BaseAdapter {
		private Context _context;
		private LayoutInflater _inflater;

		public CategoryListAdapter(Context c) {
			_context = c;
			_inflater = LayoutInflater.from(_context);
		}

		public int getCount() {
			return listCategory.size();
		}

		public Object getItem(int arg0) {
			return listCategory.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup arg2) {

			if (null == convertView)
				convertView = _inflater.inflate(R.layout.category_item, null);

			TextView tv = (TextView) convertView.findViewById(R.id.tv_name);
			if (listCategory.get(position).id == default_id)
				tv.setText(listCategory.get(position).name + default_text);
			else
				tv.setText(listCategory.get(position).name);

			return convertView;
		}

	}
}

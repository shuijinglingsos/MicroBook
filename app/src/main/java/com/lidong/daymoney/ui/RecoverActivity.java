package com.lidong.daymoney.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.lidong.daymoney.DBHelper;
import com.lidong.daymoney.FileOperation;
import com.lidong.daymoney.Global;
import com.lidong.daymoney.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

/**
 * 数据恢复
 */
public class RecoverActivity extends AppCompatActivity {

	private ListView mListView;
	private ArrayAdapter<String> adapter;

	private List<String> fileList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recover);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		setTitle(R.string.db_recover);
		init();

		File dir = new File(Global.backupDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String[] files = dir.list(new BackupFileFilter());

		Arrays.sort(files, new Comparator<String>() {
			public int compare(String lhs, String rhs) {
				return (rhs.compareTo(lhs));
			}
		});

		fileList = Arrays.asList(files);
//		fileList = new ArrayList<>();
//		for (String s : files) {
//			fileList.add(s);
//		}

		adapter = new ArrayAdapter<>(this,
				android.R.layout.simple_list_item_1, fileList);
		mListView.setAdapter(adapter);
	}

	private void init() {
		mListView = (ListView) findViewById(R.id.listView);
		mListView.setEmptyView(findViewById(R.id.tv_empty));
		registerForContextMenu(mListView);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				recover(arg2);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 载入上下文菜单
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenu.ContextMenuInfo menuInfo) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.activity_recover_list_context, menu);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		menu.setHeaderTitle(fileList.get(info.position));
	}

	// 上下文菜单点击事件
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int index = info.position;

		switch (item.getItemId()) {
			case R.id.context_recover: // 恢复数据
				recover(index);
				break;

			case R.id.context_delete: // 删除备份
				delete(index);
				break;
		}

		return true;
	}

	/**
	 * 恢复数据
	 *
	 * @param index 索引
	 */
	public void recover(final int index) {
		final String fileName = fileList.get(index);

		String text = getResources().getString(R.string.db_recover_message) + fileName;

		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(R.string.dialog_title_hint)
				.setMessage(text)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {

								try {
									File f = new File(Global.backupDir + fileName);
									File dbFile = getDatabasePath(DBHelper.DB_NAME);
									FileOperation.copyDatabase(f, dbFile);

									Intent data = new Intent();
									data.putExtra("read", true);
									setResult(Activity.RESULT_OK, data);
									finish();

									showMessage(R.string.db_recover_success);
								} catch (IOException e) {
									e.printStackTrace();
									showMessage(R.string.db_recover_failure);
								}
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {

							}
						}).show();
	}

	/**
	 * 删除备份
	 *
	 * @param index 索引
	 */
	public void delete(int index) {
		final String fileName = fileList.get(index);

		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(fileName)
				.setMessage(R.string.delete_affirm)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {

								File f = new File(Global.backupDir + fileName);
								if (f.delete()) {
									fileList.remove(fileName);
									adapter.notifyDataSetChanged();
									showMessage(R.string.delete_success);
								} else
									showMessage(R.string.delete_failure);

							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {

							}
						}).show();
	}

	/**
	 * 显示信息
	 *
	 * @param resId 资源ID
	 */
	private void showMessage(int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}

	private class BackupFileFilter implements FilenameFilter {
		public boolean accept(File dir, String filename) {
			return filename.endsWith(".bak");
		}
	}
}

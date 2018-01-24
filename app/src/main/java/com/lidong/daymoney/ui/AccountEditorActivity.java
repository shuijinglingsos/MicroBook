package com.lidong.daymoney.ui;

import com.lidong.daymoney.Account;
import com.lidong.daymoney.AccountProvider;
import com.lidong.daymoney.Global;
import com.lidong.daymoney.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class AccountEditorActivity extends AppCompatActivity {

	private int op;
	private EditText txtName, txtBalance;
	private Account account;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_editor);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		op = getIntent().getIntExtra("op", -1);

		if (op == 0) {
			setTitle(R.string.title_account_add);
		} else {
			setTitle(R.string.title_account_modify);
		}

		txtName = (EditText) findViewById(R.id.txt_account_name);
		txtBalance = (EditText) findViewById(R.id.txt_account_balance);

		if (op == 1) {
			long id = getIntent().getLongExtra("id", -1);
			account = new AccountProvider(this).get(id);
			txtName.setText(account.name);
			txtBalance.setText(Global.df1.format(account.balance));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_account_editor, menu);
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
	 * 保存
	 */
	private void save() {

		String name = txtName.getText().toString().trim();
		String balance = txtBalance.getText().toString().trim();

		if (name.equals("") || balance.equals("")) {
			Toast.makeText(this, R.string.message1, Toast.LENGTH_SHORT).show();
			return;
		}

		if (op == 0) {
			// 添加

			account = new Account();
			// c.id=0;
			account.name = name;
			account.balance = Double.parseDouble(balance);
			account.status = 0;

			account.id = new AccountProvider(this).insert(account);
			if (account.id == -1) {
				Toast.makeText(this, R.string.save_failure, Toast.LENGTH_SHORT).show();
				return;
			}

		} else {
			// 修改

			Account newAccount = new Account();
			newAccount.name = name;
			newAccount.balance = Double.parseDouble(balance);

			if (new AccountProvider(this).update(account, newAccount) == 0) {
				Toast.makeText(this, R.string.save_failure, Toast.LENGTH_SHORT).show();
				return;
			}
		}

		Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
		setResult(Activity.RESULT_OK);
		finish();
	}
}

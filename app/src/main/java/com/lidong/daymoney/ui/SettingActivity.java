package com.lidong.daymoney.ui;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import com.lidong.daymoney.DBHelper;
import com.lidong.daymoney.FileOperation;
import com.lidong.daymoney.Global;
import com.lidong.daymoney.R;
import com.umeng.fb.UMFeedbackService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * 设置界面
 * @author Lidong
 *
 */
public class SettingActivity extends AppCompatActivity {

	private SettingFragment mSettingFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.title_setting);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		mSettingFragment = new SettingFragment();
		getFragmentManager().beginTransaction().replace(android.R.id.content, mSettingFragment).commit();
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

	@Override
	public void onBackPressed() {
		close();
	}

	/**
	 * 关闭窗口
	 */
	private void close() {
		Intent data = new Intent();
		data.putExtra("read", mSettingFragment.modify_flag);
		setResult(Activity.RESULT_OK, data);
		finish();
	}

	/**
	 * 设置fragment
	 */
	public static class SettingFragment extends PreferenceFragment implements OnPreferenceClickListener {

		//修改标志
		private boolean modify_flag = false;
		private String backup_key, recover_key, password_key, feedback_key, loginPw_key;

		@Override
		public void onCreate(@Nullable Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.setting);

			backup_key = getResources().getString(R.string.db_backup);
			recover_key = getResources().getString(R.string.db_recover);
			loginPw_key = getResources().getString(R.string.setting_login_password);
			password_key = getResources().getString(R.string.setting_password);
			feedback_key = getResources().getString(R.string.setting_SendFeedback);

			findPreference(backup_key).setOnPreferenceClickListener(this);
			findPreference(recover_key).setOnPreferenceClickListener(this);
			findPreference(loginPw_key).setOnPreferenceClickListener(this);
			findPreference(password_key).setOnPreferenceClickListener(this);
			findPreference(feedback_key).setOnPreferenceClickListener(this);
		}

		@Override
		public boolean onPreferenceClick(Preference preference) {
			//备份数据
			if (preference.getKey().equals(backup_key)) {
				backup();
			}

			//恢复数据
			if (preference.getKey().equals(recover_key)) {
				recover();
			}

			//登录密码
			if (preference.getKey().equals(loginPw_key)) {
				CheckBoxPreference p = (CheckBoxPreference) preference;
				Intent intent = new Intent(getActivity(), PasswordEditActivity.class);
				if (p.isChecked()) {
					intent.putExtra(PasswordEditActivity.arg_op, PasswordEditActivity.op_add);
					startActivityForResult(intent, PasswordEditActivity.op_add);
				} else {
					intent.putExtra(PasswordEditActivity.arg_op, PasswordEditActivity.op_clear);
					startActivityForResult(intent, PasswordEditActivity.op_clear);
				}

				Global.showDialog(getActivity());

				//findPreference(password_key).setEnabled(p.isChecked());
			}

			//密码设置
			if (preference.getKey().equals(password_key)) {
				Intent intent = new Intent(getActivity(), PasswordEditActivity.class);
				intent.putExtra(PasswordEditActivity.arg_op, PasswordEditActivity.op_modify);
				startActivityForResult(intent, PasswordEditActivity.op_modify);
				Global.showDialog(getActivity());
			}

			//反馈
			if (preference.getKey().equals(feedback_key)) {
				//增加返回按钮
				UMFeedbackService.setGoBackButtonVisible();
				//打开友盟反馈界面
				UMFeedbackService.openUmengFeedbackSDK(getActivity());
			}

			return false;
		}

		/**
		 * 备份
		 */
		private void backup() {
			if (!Global.sdCardIsExsit()) {
				showMessage(R.string.no_sdcard);
				return;
			}

			File dbFile = getActivity().getDatabasePath(DBHelper.DB_NAME);

			Calendar date = Calendar.getInstance();
			String s = Global.dateTimeFormatter1.format(date.getTime());
			File toFile = new File(Global.backupDir + s + ".bak");

			try {
				FileOperation.copyDatabase(dbFile, toFile);
				showMessage(R.string.db_backup_success);
			} catch (IOException e) {
				e.printStackTrace();
				showMessage(R.string.db_backup_failure);
			}
		}

		/**
		 * 恢复
		 */
		private void recover() {
			if (!Global.sdCardIsExsit()) {
				showMessage(R.string.no_sdcard);
				return;
			}
			Intent data = new Intent(getActivity(), RecoverActivity.class);
			startActivityForResult(data, 0);
		}


		/**
		 * 显示信息
		 *
		 * @param resId 资源ID
		 */
		private void showMessage(int resId) {
			Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (resultCode == Activity.RESULT_OK) {
				switch (requestCode) {
					case PasswordEditActivity.op_add:
					case PasswordEditActivity.op_clear:
						break;

					case 0:
						if (data.getBooleanExtra("read", false)) {
							modify_flag = true;
						}
						break;
				}
			} else {
				switch (requestCode) {
					case PasswordEditActivity.op_add:
					case PasswordEditActivity.op_clear:
						CheckBoxPreference p = (CheckBoxPreference) findPreference(loginPw_key);
						p.setChecked(!p.isChecked());
						break;
				}
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
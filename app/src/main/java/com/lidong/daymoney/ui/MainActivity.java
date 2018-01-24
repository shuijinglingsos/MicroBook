package com.lidong.daymoney.ui;

import java.util.Calendar;
import java.util.Date;

import com.lidong.daymoney.Global;
import com.lidong.daymoney.R;
import com.lidong.daymoney.TransactionProvider;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	private double[] todaySum, weekSum, monthSum;
	private ListView listView;
	private MainListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				showLogin();
			}
		}, 300);
	}

	/**
	 * 初始化控件
	 */
	private void initControl() {
		// 友盟错误报告
		MobclickAgent.onError(this);
		// 友盟 检查反馈是否有恢复
		UMFeedbackService.enableNewReplyNotification(this,
				NotificationType.NotificationBar);

		listView = (ListView) findViewById(R.id.listView_main);
		listView.setOnItemClickListener(listItemClick);

		getData();

		adapter = new MainListAdapter(this);
		listView.setAdapter(adapter);

		listAnim();
	}

	/**
	 * 显示登录（输入密码）
	 */
	private void showLogin() {
		SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
		String key = getResources().getString(R.string.setting_login_password);
		if (spf.getBoolean(key, false)) {
			Intent intent = new Intent(MainActivity.this, PasswordEditActivity.class);
			intent.putExtra(PasswordEditActivity.arg_op, PasswordEditActivity.op_login);
			startActivityForResult(intent, PasswordEditActivity.op_login);
			Global.showDialog(MainActivity.this);
		} else {
			initControl();
		}
	}

	/**
	 * 退出
	 */
	private void exit() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.icon)
				.setTitle("提示")
				.setMessage("你确定要退出吗？")
				.setPositiveButton("退出", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				})
				.setNeutralButton("最小化", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						moveTaskToBack(true);
					}
				})
				.setNegativeButton(R.string.cancel, null)
				.show();
	}

	@Override
	public void onBackPressed() {
		exit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		menu.findItem(R.id.menu_statistics).setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_transaction:
				openTransactionActivity();
				break;

			case R.id.menu_statistics:
				openStatisticsActivity();
				break;

			case R.id.menu_income_category:
				openCategoryActivity(1);
				break;

			case R.id.menu_expend_category:
				openCategoryActivity(0);
				break;

			case R.id.menu_account:
				openAccountActivity();
				break;

			case R.id.menu_setting:
				openSettingActivity();
				break;

			case R.id.menu_about:
				openAbout();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 打开明细界面
	 */
	private void openTransactionActivity() {
		Intent i = new Intent(MainActivity.this, TransactionActivity.class);
		i.putExtra("show_type", TransactionActivity.SHOW_YEAR);
		i.putExtra("year", Calendar.getInstance().get(Calendar.YEAR));
		startActivityForResult(i, 0);
	}

	/**
	 * 打开统计界面
	 */
	private void openStatisticsActivity(){
		Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
		startActivity(intent);
	}

	/**
	 * 打开收入/支出界面
	 *
	 * @param category 1收入  0支出
	 */
	private void openCategoryActivity(int category) {
		Intent i = new Intent(this, CategoryActivity.class);
		i.putExtra("category", category);
		startActivity(i);
	}

	/**
	 * 打开账户界面
	 */
	private void openAccountActivity() {
		Intent i = new Intent(this, AccountActivity.class);
		startActivity(i);
	}

	/**
	 * 打开设置界面
	 */
	private void openSettingActivity() {
		Intent i = new Intent(this, SettingActivity.class);
		startActivityForResult(i, 0);
	}

	/**
	 * 显示关于
	 */
	private void openAbout() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			// 当前应用的版本名称
			String versionName = info.versionName;
			//获取开发者QQ
			String qq = getResources().getString(R.string.developer_qq);
			//获取开发者Email
			String email = getResources().getString(R.string.developer_email);

			String format = "版本：%1$s \nQQ：%2$s \nE-mail：%3$s";
			String str = String.format(format, versionName, qq, email);

			new AlertDialog.Builder(this).setIcon(R.drawable.icon)
					.setTitle(R.string.app_name)
					.setMessage(str)
					.setPositiveButton(R.string.ok, null).show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {

			switch (requestCode) {

				case PasswordEditActivity.op_login:
					initControl();
					break;

				case 0:
					if (data.getBooleanExtra("read", false)) {
						getData();
						adapter.notifyDataSetChanged();
					}
					break;
			}
		} else {
			switch (requestCode) {
				case PasswordEditActivity.op_login:
					finish();
					break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 点击列表项事件
	 */
	public OnItemClickListener listItemClick = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {

			Intent i = new Intent(MainActivity.this, TransactionActivity.class);
			Date[] dates;
			switch (arg2) {
				case 1:    //显示今天列表

					dates = getDate(arg2);
					i.putExtra("start", Global.dateTimeFormatter.format(dates[0]));
					i.putExtra("end", Global.dateTimeFormatter.format(dates[1]));
					i.putExtra("show_type", TransactionActivity.SHOW_DAY);
					startActivityForResult(i, 0);

					break;

				case 2: //显示本周列表

					dates = getDate(arg2);
					i.putExtra("start", Global.dateTimeFormatter.format(dates[0]));
					i.putExtra("end", Global.dateTimeFormatter.format(dates[1]));
					i.putExtra("show_type", TransactionActivity.SHOW_WEEK);
					startActivityForResult(i, 0);
					break;

				case 3: //显示本月列表

					Calendar c = Calendar.getInstance();
					i.putExtra("show_type", TransactionActivity.SHOW_MONTH);
					i.putExtra("year", c.get(Calendar.YEAR));
					i.putExtra("month", c.get(Calendar.MONTH) + 1);
					startActivityForResult(i, 0);

					break;
			}


		}
	};

	/**
	 * 转账
	 */
	public OnClickListener btnTransferClick = new OnClickListener() {
		public void onClick(View v) {
			Intent i = new Intent(MainActivity.this,
					TransferEditor.class);
			i.putExtra("op", 0);
			startActivityForResult(i, 0);
		}
	};

	/**
	 * 记一笔收入
	 */
	public OnClickListener btnIncomeClick = new OnClickListener() {
		public void onClick(View v) {
			Intent i = new Intent(MainActivity.this, TransactionEditor.class);
			i.putExtra("type", 1);
			i.putExtra("op", 0);
			startActivityForResult(i, 0);
		}
	};

	/**
	 * 记一笔支出
	 */
	public OnClickListener btnExpendClick = new OnClickListener() {
		public void onClick(View v) {
			Intent i = new Intent(MainActivity.this, TransactionEditor.class);
			i.putExtra("type", 0);
			i.putExtra("op", 0);
			startActivityForResult(i, 0);
		}
	};

	/**
	 * 获取数据
	 */
	private void getData() {
		TransactionProvider db = new TransactionProvider(this);

		Date[] dates = getDate(1);  //今天
		todaySum = db.getSum(dates[0], dates[1]);

		dates = getDate(2);  //本周
		weekSum = db.getSum(dates[0], dates[1]);

		dates = getDate(3);   //本月
		monthSum = db.getSum(dates[0], dates[1]);
	}


	public void showSum(int index, View view) {
		String dateString = "";
		Date[] dates = getDate(index);

		double[] sum;

		if (index == 1)
			sum = todaySum;
		else if (index == 2)
			sum = weekSum;
		else
			sum = monthSum;

		if (index == 1) {    //今天
			dateString = Global.dateFormatter3.format(dates[0]);
		} else {    //本周和本月
			Calendar c = Calendar.getInstance();
			c.setTime(dates[1]);
			c.add(Calendar.DAY_OF_MONTH, -1);
			dateString = Global.dateFormatter1.format(dates[0]) + " - " + Global.dateFormatter1.format(c.getTime());
		}

		((TextView) view.findViewById(R.id.tv_expend)).setText("- " + Global.df.format(sum[0]));
		((TextView) view.findViewById(R.id.tv_income)).setText("+ " + Global.df.format(sum[1]));
		((TextView) view.findViewById(R.id.tv_date)).setText(dateString);

	}

	/**
	 * 获取开始和结束的日期
	 *
	 * @param index 1 今天   2 本周  3本月
	 * @return 1 开始时间  2结束时间
	 */
	public Date[] getDate(int index) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		Date start, end;


		switch (index) {
			case 1:
				//今天
				start = c.getTime();
				c.add(Calendar.DAY_OF_MONTH, 1);
				end = c.getTime();
				break;

			case 2:
				//本周
				int week = c.get(Calendar.DAY_OF_WEEK);
				if (week == Calendar.SUNDAY)
					week = 8;
				c.add(Calendar.DAY_OF_MONTH, -(week - 2));
				start = c.getTime();
				c.add(Calendar.DAY_OF_MONTH, 7);
				end = c.getTime();
				break;

			case 3:
				//本月
				c.set(Calendar.DAY_OF_MONTH, 1);
				start = c.getTime();
				c.add(Calendar.MONTH, 1);
				end = c.getTime();
				break;

			default:
				return null;
		}

		return new Date[]{start, end};
	}

	/**
	 * @author Lidong
	 *         菜单列表适配器
	 */
	public class MainListAdapter extends BaseAdapter {
		private Context _context;
		private LayoutInflater _inflater;

		public MainListAdapter(Context c) {
			_context = c;
			_inflater = LayoutInflater.from(_context);
		}

		public int getCount() {
			return 4;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			if (0 == position) {
				convertView = _inflater.inflate(R.layout.btns, null);
				Button btn = (Button) convertView.findViewById(R.id.btn_income);
				btn.setOnClickListener(btnIncomeClick);
				btn = (Button) convertView.findViewById(R.id.btn_expend);
				btn.setOnClickListener(btnExpendClick);
				btn = (Button) convertView.findViewById(R.id.btn_transfer);
				btn.setOnClickListener(btnTransferClick);
			} else {

				// if (null == convertView) {
				convertView = _inflater.inflate(R.layout.listview_item_main,
						null);
				// }

				ImageViewText iv = (ImageViewText) convertView
						.findViewById(R.id.imageViewText);
				TextView tv = (TextView) convertView
						.findViewById(R.id.tv_name);

				if (position == 1) {
					iv.getImageView().setImageResource(R.drawable.main_today);
					iv.getTextView().setText("" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
					tv.setText(R.string.current_day);

					showSum(1, convertView);
				}

				if (position == 2) {
					iv.getImageView().setImageResource(R.drawable.main_week);
					iv.getTextView().setText("");
					tv.setText(R.string.current_week);

					showSum(2, convertView);
				}

				if (position == 3) {
					iv.getImageView().setImageResource(R.drawable.main_month);
					iv.getTextView().setText("");
					tv.setText(R.string.current_month);

					showSum(3, convertView);
				}
			}

			return convertView;
		}
	}

	/**
	 * 动画
	 */
	private void listAnim() {
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(50);
		set.addAnimation(animation);

		animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 5.0f, Animation.RELATIVE_TO_SELF, 0.0f
		);
		animation.setDuration(300);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
		listView.setLayoutAnimation(controller);
	}

	//----------------- 友盟统计--------------------

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	//----------------- 友盟统计 end--------------------
}
package com.lidong.daymoney;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.os.Environment;

public class Global {

	/**
	 * 数据备份路径
	 */
	public static String backupDir="/mnt/sdcard/.DayMoney/Backup/";

	public static String INCOME_CATEGORY_DEFAULT="income_category_default";
	public static String EXPEND_CATEGORY_DEFAULT="expend_category_default";
	public static String ACCOUNT_DEFAULT="account_default";
	public static String PASSWORD="password";

	/**
	 * 日期格式 yyyy-MM-dd HH:mm:ss
	 */
	public static SimpleDateFormat dateTimeFormatter1 = new SimpleDateFormat(
			"yyyy-MM-dd HH.mm.ss");

	/**
	 * 日期格式 yyyy-MM-dd HH:mm
	 */
	public static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	/**
	 * 日期格式yyyy年MM月dd日
	 */
	public static SimpleDateFormat dateFormatter=new SimpleDateFormat(
			"yyyy年MM月dd日");

	/**
	 * 日期格式MM月dd日
	 */
	public static SimpleDateFormat dateFormatter1=new SimpleDateFormat(
			"MM月dd日");

	/**
	 * 日期格式 dd日 HH:mm
	 */
	public static SimpleDateFormat dateFormatter2=new SimpleDateFormat(
			"dd日 HH:mm");

	/**
	 * 日期格式yyyy年MM月dd日 E
	 */
	public static SimpleDateFormat dateFormatter3=new SimpleDateFormat(
			"yyyy年MM月dd日 E");

	/**
	 * 小数保留两位显示 ##,###,###,##0.00
	 */
	public static DecimalFormat df=new DecimalFormat("#,###,###,###,##0.00");

	/**
	 * 小数保留两位显示 ##########0.00
	 */
	public static DecimalFormat df1=new DecimalFormat("##########0.00");

	/**
	 * 判断sd卡是否存在
	 * @return
	 */
	public static boolean sdCardIsExsit() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 弹出对话框  从下往上
	 * @param activity
	 */
	public static void showDialog(Activity activity)
	{
		activity.overridePendingTransition(R.anim.dialog_buttom_in, R.anim.push_empty_out);
	}

	/**
	 * 隐藏对话框 从上往下
	 * @param activity
	 */
	public static void hideDialog(Activity activity)
	{
		activity.overridePendingTransition(R.anim.push_empty_out,R.anim.dialog_buttom_out);
	}

	/**
	 * 判断字符串是否为空
	 * @param s
	 * @return
	 */
	public static boolean stringIsNullOrEmpty(String s)
	{
		if(s==null)
			return true;

		if(s.equals(""))
			return true;

		return false;
	}
}

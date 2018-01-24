package com.lidong.daymoney;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 密码帮助
 * @author Lidong
 *
 */
public class PasswordHelper {

	private SharedPreferences spf;

	public PasswordHelper(Context c)
	{
		spf= PreferenceManager.getDefaultSharedPreferences(c);
	}

	/**
	 * 获取密码
	 * @return 获取密码，为空时没有密码
	 */
	public String getPassword()
	{
		return spf.getString(Global.PASSWORD, "");
	}

	/**
	 * 设置密码
	 * @param pw 新密码
	 */
	public void setPassword(String pw)
	{
		Editor editor= spf.edit();
		editor.putString(Global.PASSWORD, pw);
		editor.commit();
	}

	/**
	 * 清空密码
	 */
	public void clearPassword()
	{
		Editor editor= spf.edit();
		editor.remove(Global.PASSWORD);
		editor.commit();
	}
}

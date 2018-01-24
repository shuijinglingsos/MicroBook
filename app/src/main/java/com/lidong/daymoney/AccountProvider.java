package com.lidong.daymoney;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 账户数据提供者
 *
 * @author Lidong
 *
 */
public class AccountProvider {

	private DBHelper dbHelper;
	private Context _c;

	public AccountProvider(Context c) {
		_c=c;
		dbHelper = new DBHelper(c);
	}

	/**
	 * 获取所有账户余额总和（净资产）
	 * @return
	 */
	public double getSum()
	{
		String sql="Select sum(balance) From [account] ";

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor result = db.rawQuery(sql, null);

		double temp=0;
		if (result.moveToFirst()) {
			temp=result.getDouble(0);
		}
		db.close();

		return temp;
	}

	/**
	 * 获取所有账户
	 */
	public ArrayList<Account> getAll() {
		ArrayList<Account> list = new ArrayList<Account>();

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor result = db.query(DBHelper.TABLE_ACCOUNT, null, null, null,
				null, null, null);

		Account temp;

		if (result.moveToFirst()) {
			do {
				temp = new Account();
				temp.id = result.getLong(0);
				temp.name = result.getString(1);
				temp.balance = result.getDouble(2);
				temp.status = result.getInt(3);

				list.add(temp);
			} while (result.moveToNext());
		}

		db.close();

		return list;
	}

	/**
	 * 根据ID获取账户信息
	 * @param id
	 * @return
	 */
	public Account get(long id)
	{
		String sql="Select * From [account] where _id="+id;

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor result = db.rawQuery(sql, null);

		Account temp=null;
		if (result.moveToFirst()) {
			temp = new Account();
			temp.id = result.getLong(0);
			temp.name = result.getString(1);
			temp.balance = result.getDouble(2);
			temp.status = result.getInt(3);
		}
		db.close();

		return temp;
	}

	/**
	 * 添加账户
	 * @param a 账户
	 * @return 账户ID
	 */
	public long insert(Account a)
	{
		ContentValues values=new ContentValues();
		values.put(Account.NAME, a.name);
		values.put(Account.BALANCE, a.balance);
		values.put(Account.STATUS, a.status);

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		long id=db.insert(DBHelper.TABLE_ACCOUNT, null, values);
		db.close();

		return id;
	}

	/**
	 * 修改账户
	 * @param a 账户
	 * @return 影响的行数
	 */
	public int update(Account oldAccount,Account newAccount)
	{
		ContentValues values = new ContentValues();
		values.put(Account.NAME, newAccount.name);
		values.put(Account.BALANCE, newAccount.balance);

		String where = "_id=" + oldAccount.id;

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int result = db.update(DBHelper.TABLE_ACCOUNT, values, where, null);
		db.close();

		if(oldAccount.balance!=newAccount.balance)
		{
			Transaction action=new Transaction();
			action.date=Calendar.getInstance().getTime();
			action.t_type=3;  //修改余额
			action.account_id_1=oldAccount.id;
			action.money=newAccount.balance-oldAccount.balance;
			new TransactionProvider(_c).insert(action);
		}

		return result;
	}

	/**
	 * 删除指定账户
	 * @param id 账户ID
	 * @return
	 */
	public int delete(long id)
	{
		String where = "_id="+id;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int result = db.delete(DBHelper.TABLE_ACCOUNT, where, null);
		db.close();

		return result;
	}


	/**
	 * 改变账户余额
	 * @param id 账户ID
	 * @param operator 运算符 （+/-）
	 * @param money 金额
	 */
	public void changeBalance(double id, String operator, double money)
	{
		String sql;
		if(operator.equals("-"))
			sql="update Account set balance=(balance-?) where _id=?";
		else if(operator.equals("+"))
			sql="update Account set balance=(balance+?) where _id=?";
		else
			return;

		Object[] args=new Object[]{money,id};

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(sql, args);
		db.close();
	}
}

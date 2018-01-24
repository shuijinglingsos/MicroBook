package com.lidong.daymoney;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TransactionProvider {

	private DBHelper dbHelper;
	private Context _context;

	public TransactionProvider(Context c) {
		dbHelper = new DBHelper(c);
		_context=c;
	}


	/**
	 * 获取指定的消费明细
	 * @param id id
	 * @return
	 * @throws ParseException
	 */
	public Transaction get(long id) throws ParseException
	{
		String sql="Select * From [transactions] where _id="+id;

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor result = db.rawQuery(sql, null);

		Transaction temp=null;

		if (result.moveToFirst()) {
			temp = new Transaction();
			temp.id = result.getLong(0);
			temp.date = Global.dateTimeFormatter.parse(result.getString(1));
			temp.t_type = result.getInt(2);
			temp.category_id = result.getLong(3);
			temp.account_id_1 = result.getLong(4);
			temp.account_id_2 = result.getLong(5);
			temp.money = result.getDouble(6);
			temp.remark = result.getString(7);
			temp.status = result.getInt(8);
		}

		db.close();

		return temp;
	}

	/**
	 * 添加交易
	 *
	 * @param t
	 * @return id
	 */
	public long insert(Transaction t) {
		ContentValues values = new ContentValues();

		if(t.t_type==0 || t.t_type==1)
		{
			values.put(Transaction.T_TYPE, t.t_type);
			values.put(Transaction.MONEY, t.money);
			values.put(Transaction.CATEGORY_ID, t.category_id);
			values.put(Transaction.ACCOUNT_ID_1, t.account_id_1);
			values.put(Transaction.DATE, Global.dateTimeFormatter.format(t.date));
			values.put(Transaction.REMARK, t.remark);
			values.put(Transaction.STATUS, t.status);
		}

		if(t.t_type==2)
		{
			values.put(Transaction.T_TYPE, t.t_type);
			values.put(Transaction.MONEY, t.money);
			values.put(Transaction.ACCOUNT_ID_1, t.account_id_1);
			values.put(Transaction.ACCOUNT_ID_2, t.account_id_2);
			values.put(Transaction.DATE, Global.dateTimeFormatter.format(t.date));
			values.put(Transaction.REMARK, t.remark);
			values.put(Transaction.STATUS, t.status);
		}

		if(t.t_type==3)
		{
			values.put(Transaction.T_TYPE, t.t_type);
			values.put(Transaction.MONEY, t.money);
			values.put(Transaction.ACCOUNT_ID_1, t.account_id_1);
			values.put(Transaction.DATE, Global.dateTimeFormatter.format(t.date));
			values.put(Transaction.STATUS, t.status);
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id = db.insert(DBHelper.TABEL_TRANSACTION, null, values);
		db.close();

		AccountProvider ap=new AccountProvider(_context);
		if(t.t_type==0)
		{
			ap.changeBalance(t.account_id_1, "-", t.money);
		}
		if(t.t_type==1)
		{
			ap.changeBalance(t.account_id_1, "+", t.money);
		}
		if(t.t_type==2)
		{
			ap.changeBalance(t.account_id_1, "-", t.money);
			ap.changeBalance(t.account_id_2, "+", t.money);
		}
		return id;
	}

	/**
	 * 删除交易
	 *
	 * @param t
	 * @return
	 */
	public int delete(Transaction t) {

		String where = "_id=" + t.id;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int result = db.delete(DBHelper.TABEL_TRANSACTION, where, null);
		db.close();

		AccountProvider ap=new AccountProvider(_context);

		if(t.t_type==0)
		{
			ap.changeBalance(t.account_id_1, "+", t.money);
		}
		if(t.t_type==1)
		{
			ap.changeBalance(t.account_id_1, "-", t.money);
		}
		if(t.t_type==2)
		{
			ap.changeBalance(t.account_id_1, "+", t.money);
			ap.changeBalance(t.account_id_2, "-", t.money);
		}
		if(t.t_type==3)
		{
			ap.changeBalance(t.account_id_1, "-", t.money);
		}

		return result;
	}

	/**
	 * 更新交易明细
	 * @param t 明细
	 * @return 影响的行
	 * @throws ParseException
	 */
	public int update(Transaction t) throws ParseException
	{
		//获取之前的交易明细
		Transaction source=get(t.id);

		ContentValues values = new ContentValues();
		if(t.t_type==0 || t.t_type==1)
		{
			values.put(Transaction.MONEY, t.money);
			values.put(Transaction.CATEGORY_ID, t.category_id);
			values.put(Transaction.ACCOUNT_ID_1, t.account_id_1);
			values.put(Transaction.DATE, Global.dateTimeFormatter.format(t.date));
			values.put(Transaction.REMARK, t.remark);
			values.put(Transaction.STATUS, t.status);
		}
		if(t.t_type==2)
		{
			values.put(Transaction.MONEY, t.money);
			values.put(Transaction.ACCOUNT_ID_1, t.account_id_1);
			values.put(Transaction.ACCOUNT_ID_2, t.account_id_2);
			values.put(Transaction.DATE, Global.dateTimeFormatter.format(t.date));
			values.put(Transaction.REMARK, t.remark);
			values.put(Transaction.STATUS, t.status);
		}

		String where = "_id=" + t.id;

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int result = db.update(DBHelper.TABEL_TRANSACTION, values, where, null);
		db.close();

		AccountProvider ap=new AccountProvider(_context);
		if(t.t_type==0) //支出
		{
			//增加之前的账户金额
			ap.changeBalance(source.account_id_1, "+", source.money);
			//扣除现在的账户金额
			ap.changeBalance(t.account_id_1, "-", t.money);
		}
		if(t.t_type==1) //收入
		{
			//扣除之前的账户金额
			ap.changeBalance(source.account_id_1, "-", source.money);
			//增加现在的账户金额
			ap.changeBalance(t.account_id_1, "+", t.money);
		}
		if(t.t_type==2)
		{
			//增加之前转出账号金额
			ap.changeBalance(source.account_id_1, "+", source.money);
			//扣除之前转入账号金额
			ap.changeBalance(source.account_id_2, "-", source.money);

			//扣除现在转出账号金额
			ap.changeBalance(t.account_id_1, "-", t.money);
			//增加现在转入账号金额
			ap.changeBalance(t.account_id_2, "+", t.money);
		}


		return result;
	}


	/**
	 * 获取指定时间段的收入和支出总和
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 0 支出   1收入
	 */
	public double[] getSum(Date start,Date end)
	{
		String sql = "select sum(case when t_type=0 then money else 0 end) ,"
				+ "sum(case when t_type=1 then money else 0 end) "
				+ "from transactions t1 "
				+ "where (t1.t_type=0 or t1.t_type=1) and "
				+ "(date >='"+Global.dateTimeFormatter.format(start)+"' and date<'"+Global.dateTimeFormatter.format(end)+"')";


		SQLiteDatabase db = dbHelper.getReadableDatabase();

		try {
			Cursor result = db.rawQuery(sql, null);

			if (result.moveToFirst()) {
				return new double[] { result.getDouble(0), result.getDouble(1) };
			} else
				return new double[] { 0f, 0f };
		} finally {
			db.close();
		}
	}

	/**
	 * 获取交易列表
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 交易列表
	 * @throws ParseException
	 */
	public ArrayList<Transaction> get(Date start, Date end, String where)
			throws ParseException {
		ArrayList<Transaction> list = new ArrayList<Transaction>();


		String sql = "select t1.*,t2.[name] as category_name "
				+ "From [transactions] t1 left join [category] t2 on t1.category_id=t2._id "
				+ "where "  // t1.[category_id]=t2.[_id] and "
				+ "(date >='" + Global.dateTimeFormatter.format(start)
				+ "' and date<'" + Global.dateTimeFormatter.format(end) + "') ";

		if(where!=null && !where.equals(""))
		{
			sql+=" and "+where+" ";
		}

		sql+= "order by t1.[date] desc ,t1.[_id] desc";

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor result = db.rawQuery(sql, null);

		Transaction temp;

		if (result.moveToFirst()) {
			do {
				temp = new Transaction();
				temp.id = result.getLong(0);
				temp.date = Global.dateTimeFormatter.parse(result.getString(1));
				temp.t_type = result.getInt(2);
				temp.category_id = result.getLong(3);
				temp.account_id_1 = result.getLong(4);
				temp.account_id_2 = result.getLong(5);
				temp.money = result.getDouble(6);
				temp.remark = result.getString(7);
				temp.status = result.getInt(8);
				temp.ex_categroy_name=result.getString(9);

				list.add(temp);

			} while (result.moveToNext());
		}

		db.close();

		return list;
	}

	/**
	 * 返回符合指定条件的数量
	 * @param where 条件
	 * @return 数量
	 */
	public int getCount(String where) {
		String sql = "select count(*) from transactions where " + where;

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			Cursor result = db.rawQuery(sql, null);
			if(result.moveToFirst())
				return result.getInt(0);
			else
				return 0;
		} finally {
			db.close();
		}
	}

	private ArrayList<TransactionSum> getSumHelper(String sql,String[] args)
	{
		ArrayList<TransactionSum> list = new ArrayList<TransactionSum>();

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor result = db.rawQuery(sql, args);

		TransactionSum temp;

		if (result.moveToFirst()) {
			do {
				temp = new TransactionSum();
				temp.date = result.getString(0);
				temp.expend = result.getDouble(1);
				temp.income = result.getDouble(2);
				temp.transfer_count = result.getInt(3);

				list.add(temp);
			} while (result.moveToNext());
		}

		db.close();

		return list;
	}

	/**
	 * 获取交易明细之和（以年为单位）
	 * @return
	 */
	public ArrayList<TransactionSum> getSumByAll() {
		String sql = "Select strftime('%Y',[date]), "
				+ "sum(case when t1.t_type=0 then money else 0 end), "
				+ "sum(case when t1.t_type=1 then money else 0 end), "
				+ "sum(case when t1.t_type=2 then 1 else 0 end) "
				+ "From [transactions] t1 "
				+ "group by 1 "
				+ "order by 1 desc";

		return getSumHelper(sql,null);
	}

	/**
	 * 获取交易明细之和（以月为单位）
	 * @param year
	 * @return
	 */
	public ArrayList<TransactionSum> getSumByYear(int year)
	{
		String sql = "Select strftime('%m',[date]), "
				+ "sum(case when t1.t_type=0 then money else 0 end), "
				+ "sum(case when t1.t_type=1 then money else 0 end), "
				+ "sum(case when t1.t_type=2 then 1 else 0 end) "
				+ "From [transactions] t1 "
				+ "where strftime('%Y',[date])='"+year+"' "
				+ "group by 1 "
				+ "order by 1 desc";

		//String[] args = new String[] { year };

		return getSumHelper(sql, null);
	}

	/**
	 * 获取交易明细之和（以天为单位）
	 * @param start
	 * @param end
	 * @return
	 */
	public ArrayList<TransactionSum> getSumByMonth(Date start, Date end)
	{
		String sql = "Select strftime('%Y-%m-%d',[date]), "
				+ "sum(case when t_type=0 then money else 0 end), "
				+ "sum(case when t_type=1 then money else 0 end), "
				+ "sum(case when t1.t_type=2 then 1 else 0 end) "
				+ "from transactions t1 "
				+ "where (t1.t_type=0 or t1.t_type=1) and "
				+ "(date >='"+Global.dateTimeFormatter.format(start)+"' and date<'"+Global.dateTimeFormatter.format(end)+"') "
				+ "group by 1 "
				+ "order by 1 desc";

		return getSumHelper(sql, null);
	}
}

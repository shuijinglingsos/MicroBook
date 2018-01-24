package com.lidong.daymoney;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	// 数据库文件名
	public static final String DB_NAME = "daymoney.db";
	// 数据库版本
	public static final int DB_VERSION = 1;
	// 交易明细表
	public static final String TABEL_TRANSACTION = "transactions";
	// 账户表
	public static final String TABLE_ACCOUNT = "account";
	// 分类表
	public static final String TABLE_CATEGORY = "category";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		//创建账户表
		String c_account = "create table " + TABLE_ACCOUNT + " ("
				+ Account.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Account.NAME + " text,"
				+ Account.BALANCE + " double,"
				+ Account.STATUS + " integer"
				+ ");";

		//创建收入/支出分类表
		String c_category = "create table " + TABLE_CATEGORY + " ("
				+ Category.ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ Category.NAME + " text,"
				+ Category.IO_TYPE + " integer,"
				+ Category.STATUS + " integer"
				+ ");";

		//创建交易明细表
		String c_transacation = "create table " + TABEL_TRANSACTION + " ("
				+ Transaction.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Transaction.DATE + " datetime,"
				+ Transaction.T_TYPE + " integer,"
				+ Transaction.CATEGORY_ID + " integer,"
				+ Transaction.ACCOUNT_ID_1 + " integer,"
				+ Transaction.ACCOUNT_ID_2 + " integer,"
				+ Transaction.MONEY + " double,"
				+ Transaction.REMARK + " text,"
				+ Transaction.STATUS + " integer"
				+ ");";

		db.execSQL(c_account);
		db.execSQL(c_category);
		db.execSQL(c_transacation);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}

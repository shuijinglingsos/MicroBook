package com.lidong.daymoney;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Lidong 收入或支出分类 数据提供者
 */
public class CategoryProvider {

	private DBHelper dbHelper;

	public CategoryProvider(Context c) {
		dbHelper = new DBHelper(c);
	}

	/**
	 * 获取指定类型的所有分类
	 *
	 * @param type
	 *            0支出 1收入
	 * @return 分类列表
	 */
	public ArrayList<Category> getAllByType(int type) {
		ArrayList<Category> list = new ArrayList<Category>();

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from category where io_type=" + type;

		Cursor result = db.rawQuery(sql, null);

		Category temp;

		if (result.moveToFirst()) {
			do {
				temp = new Category();
				temp.id = result.getLong(0);
				temp.name = result.getString(1);
				temp.io_type = result.getInt(2);
				temp.status = result.getInt(3);

				list.add(temp);
			} while (result.moveToNext());
		}

		db.close();

		return list;
	}

	/**
	 * 插入分类
	 *
	 * @param c
	 *            分类
	 * @return 分类ID
	 */
	public long insert(Category c) {
		ContentValues values = new ContentValues();
		values.put(Category.NAME, c.name);
		values.put(Category.IO_TYPE, c.io_type);
		values.put(Category.STATUS, c.status);

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id = db.insert(DBHelper.TABLE_CATEGORY, null, values);
		db.close();

		return id;
	}

	/**
	 * 更新分类
	 *
	 * @param c
	 *            分类
	 * @return 影响的行数
	 */
	public int update(Category c) {
		ContentValues values = new ContentValues();
		values.put(Category.NAME, c.name);

		String where = "_id=" + c.id;

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int result = db.update(DBHelper.TABLE_CATEGORY, values, where, null);
		db.close();

		return result;
	}

	/**
	 * 删除指定分类
	 * @param id 分类ID
	 * @return
	 */
	public int delete(long id)
	{
		String where = "_id="+id;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int result = db.delete(DBHelper.TABLE_CATEGORY, where, null);
		db.close();

		return result;
	}
}

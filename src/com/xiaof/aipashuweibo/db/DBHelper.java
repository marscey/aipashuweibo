package com.xiaof.aipashuweibo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	/**
	 * 构造器
	 * @param context 	上下文
	 * @param name	  	数据库的名称
	 * @param factory	游标工厂
	 * @param version	数据库的版本
	 */
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	/**
	 * 创建DBHepler实例
	 * @param context 上下文
	 */
	public DBHelper(Context context) {
		this(context, DBInfo.DB.DB_NAME, null, DBInfo.DB.VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DBInfo.Table.CREATE_USER_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//可以先备份，在删除
		db.execSQL(DBInfo.Table.DROP_USER_TABLE);
		onCreate(db);

	}

}

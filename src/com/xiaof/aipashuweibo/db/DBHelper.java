package com.xiaof.aipashuweibo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	/**
	 * ������
	 * @param context 	������
	 * @param name	  	���ݿ������
	 * @param factory	�α깤��
	 * @param version	���ݿ�İ汾
	 */
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	/**
	 * ����DBHeplerʵ��
	 * @param context ������
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
		//�����ȱ��ݣ���ɾ��
		db.execSQL(DBInfo.Table.DROP_USER_TABLE);
		onCreate(db);

	}

}

package com.xiaof.aipashuweibo.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.xiaof.aipashuweibo.db.DBHelper;
import com.xiaof.aipashuweibo.db.DBInfo;
import com.xiaof.aipashuweibo.pojo.User;

public class UserDao {
	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	ContentValues values = null;
	/**
	 * User���ֶ�����
	 */
	String []columns = { DBInfo.Table._ID, DBInfo.Table.USER_NAME, 
			DBInfo.Table.USER_ID, DBInfo.Table.TOKEN,
			DBInfo.Table.TOKEN_SECRET, DBInfo.Table.DESCRIPTION, DBInfo.Table.USER_HEAD  };
	
	public UserDao(Context context){
		dbHelper = new DBHelper(context);
	}
	
	/**
	 * �����û�
	 * @param user
	 * @return
	 */
	public long inserUser(User user){
		// ���SQLiteDatebase�������ݿ����
		db = dbHelper.getWritableDatabase();
		// �����󶨶���
		values = new ContentValues();
		values.put(DBInfo.Table.USER_ID, user.getId());
		values.put(DBInfo.Table.USER_NAME, user.getUserName());
		values.put(DBInfo.Table.DESCRIPTION, user.getDescription());
		values.put(DBInfo.Table.TOKEN, user.getToken());
		values.put(DBInfo.Table.TOKEN_SECRET, user.getTokenSecret());
		
		//��ͼƬ���͵����ݽ��д洢��ʱ����Ҫ����ת�����ܴ洢��BLOB������
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		//Ϊ��ʵ�����ݴ洢����Ҫ���������ͽ���ת��
		BitmapDrawable newHead = (BitmapDrawable) user.getUserHead();
		//�����ݽ���ѹ����PNG�������ݣ��洢���� 100%
		newHead.getBitmap().compress(CompressFormat.PNG, 100, os);
		values.put(DBInfo.Table.USER_HEAD, os.toByteArray());
		
		//���в�������������к�
		long rowId = db.insert(DBInfo.Table.USER_TABLE, DBInfo.Table.USER_NAME, values);
		//�ر����ݿ����ӣ��ͷ���Դ
		db.close();
		return rowId;
	}
	
	/**
	 * �����û�
	 * @param user
	 * @return
	 */
	public int updateUser(User user){
		return 1;
	}
	
	/**
	 * �����û�ID��ɾ���û�
	 * @param userId
	 * @return
	 */
	public int deleteUser(String userId){
		return 1;
	}
	
	/**
	 * ���� �û�ID ��ȡ�û���Ϣ
	 * @param userId
	 * @return
	 */
	public User findUserByUserId(String userId){
		return new User();
	}
	

	
	/**
	 * ��ȡ���е��û�
	 * @return
	 */
	public List<User> findAllUsers(){
		// ���SQLiteDatebase�������ݿ����
		db = dbHelper.getWritableDatabase();
		List<User> userList = null;
		User user = null;
		Cursor cursor = db.query(DBInfo.Table.USER_TABLE, columns, null, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			userList = new ArrayList<User>(cursor.getCount());
			while (cursor.moveToNext()) {
				user = new  User();
				user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DBInfo.Table._ID)));
				user.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.Table.USER_ID)));
				user.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.Table.USER_NAME)));
				user.setToken(cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.Table.TOKEN)));
				user.setTokenSecret(cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.Table.TOKEN_SECRET)));
				user.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.Table.DESCRIPTION)));
				byte[] byteHead = cursor.getBlob(cursor.getColumnIndexOrThrow(DBInfo.Table.USER_HEAD));
				if (byteHead!=null) {
					ByteArrayInputStream is = new ByteArrayInputStream(byteHead);
					Drawable userHead= Drawable.createFromStream(is, "image");
					user.setUserHead(userHead);
				}
				userList.add(user);
			}
		}
		cursor.close();
		return userList;
	}
}

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
	 * User表字段数组
	 */
	String []columns = { DBInfo.Table._ID, DBInfo.Table.USER_NAME, 
			DBInfo.Table.USER_ID, DBInfo.Table.TOKEN,
			DBInfo.Table.TOKEN_SECRET, DBInfo.Table.DESCRIPTION, DBInfo.Table.USER_HEAD  };
	
	public UserDao(Context context){
		dbHelper = new DBHelper(context);
	}
	
	/**
	 * 新增用户
	 * @param user
	 * @return
	 */
	public long inserUser(User user){
		// 获得SQLiteDatebase进行数据库操作
		db = dbHelper.getWritableDatabase();
		// 参数绑定对象
		values = new ContentValues();
		values.put(DBInfo.Table.USER_ID, user.getId());
		values.put(DBInfo.Table.USER_NAME, user.getUserName());
		values.put(DBInfo.Table.DESCRIPTION, user.getDescription());
		values.put(DBInfo.Table.TOKEN, user.getToken());
		values.put(DBInfo.Table.TOKEN_SECRET, user.getTokenSecret());
		
		//将图片类型的数据进行存储的时候，需要进行转换才能存储到BLOB类型中
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		//为了实现数据存储，需要将数据类型进行转换
		BitmapDrawable newHead = (BitmapDrawable) user.getUserHead();
		//将数据进行压缩成PNG编码数据，存储质量 100%
		newHead.getBitmap().compress(CompressFormat.PNG, 100, os);
		values.put(DBInfo.Table.USER_HEAD, os.toByteArray());
		
		//进行插入操作，返回行号
		long rowId = db.insert(DBInfo.Table.USER_TABLE, DBInfo.Table.USER_NAME, values);
		//关闭数据库链接，释放资源
		db.close();
		return rowId;
	}
	
	/**
	 * 更新用户
	 * @param user
	 * @return
	 */
	public int updateUser(User user){
		return 1;
	}
	
	/**
	 * 根据用户ID，删除用户
	 * @param userId
	 * @return
	 */
	public int deleteUser(String userId){
		return 1;
	}
	
	/**
	 * 根据 用户ID 获取用户信息
	 * @param userId
	 * @return
	 */
	public User findUserByUserId(String userId){
		return new User();
	}
	

	
	/**
	 * 获取所有的用户
	 * @return
	 */
	public List<User> findAllUsers(){
		// 获得SQLiteDatebase进行数据库操作
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

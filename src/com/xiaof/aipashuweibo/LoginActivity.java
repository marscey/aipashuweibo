package com.xiaof.aipashuweibo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaof.aipashuweibo.dao.UserDao;
import com.xiaof.aipashuweibo.pojo.User;

/**
 * 用户授权操作
 * 
 * @author chaoyunYip
 * 
 *         2016-8-19
 */
public class LoginActivity extends Activity implements OnClickListener{
	
	ImageView userHead;
	TextView userSlogans;
	Spinner spinnerUser;
	Button login;
	Button cancel;
	
	List<User> userData;
	
	private static final String TAG = "LoginActivity";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		userHead = (ImageView) findViewById(R.id.user_head);
		userSlogans = (TextView) findViewById(R.id.user_slogans);
		spinnerUser = (Spinner) findViewById(R.id.auto_user);
		login = (Button) findViewById(R.id.login);
		cancel = (Button) findViewById(R.id.cancel);
		login.setOnClickListener(this);
		cancel.setOnClickListener(this);
		findViewById(R.id.add_user).setOnClickListener(this);
		init();
	}
	
	public void init(){
		UserDao dao = new UserDao(this);
		userData = dao.findAllUsers();
		
		if (userData==null || userData.size()<=0) {
    		Intent intent = new Intent(this, OAuthActivity2_0.class);
    		startActivity(intent);
    		finish();
		}else{
			List<HashMap<String, Object>> userList = new ArrayList<HashMap<String, Object>>();
			for (User u : userData) {
				HashMap<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("name", u.getUserName());
				userList.add(userMap);
			}
//			
			
			
			spinnerUser.setAdapter(new SimpleAdapter(this, userList, 
					R.layout.login_user_item, new String[]{"name"}, new int[]{R.id.user_name}));
			
			spinnerUser.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					User u = userData.get(position);
					userHead.setImageDrawable(u.getUserHead());
					userSlogans.setText(u.getDescription());
					
					UserSession.currentUser = u;
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					Toast.makeText(LoginActivity.this, "登录", Toast.LENGTH_SHORT).show();
				}
			});
//			
		}
		
	}
	
	/**
	 * 保存当前登录用户
	 * @author chaoyunYip
	 *
	 * 2016-8-26
	 */
	public static class UserSession{
		public static User currentUser;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			//Toast.makeText(LoginActivity.this, "登录", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(this, HomeActivity.class));
			break;
		case R.id.cancel:
			Toast.makeText(LoginActivity.this, "取消", Toast.LENGTH_SHORT).show();
			break;
		case R.id.add_user:
    		startActivity(new Intent(this, OAuthActivity2_0.class));
			break;
		}
	}

}

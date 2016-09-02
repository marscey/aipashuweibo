package com.xiaof.aipashuweibo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.xiaof.aipashuweibo.pojo.User;
import com.xiaof.aipashuweibo.util.OAuth;

/**
 * 用户授权操作
 * 
 * @author chaoyunYip
 * 
 *         2016-8-15
 */
public class OAuthActivity extends Activity {
	private static final String TAG = "OAuthActivity";
	private String callBackUrl = "xiaofit://OAuthActivity";
	private OAuth oAuth = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oauth);
//		解决这个问题
//		Linkedin : oauth.signpost.exception.OAuthCommunicationException: 
//		Communication with the service provider failed: null
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		View view = View.inflate(this, R.layout.oauth_dialog, null);

		Dialog dialog = new Dialog(this, R.style.oauth_style);
		dialog.setContentView(view);
		dialog.show();
		Button oauthButton = (Button) view.findViewById(R.id.oauth_start);
		oauthButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Toast.makeText(OAuthActivity.this, "认证",
				// Toast.LENGTH_SHORT).show();
				oAuth = OAuth.getInstance();
				// 请求AccessToken
				// callBackUrl 授权完成返回的页面
				oAuth.RequestAccessToken(OAuthActivity.this, callBackUrl);
			}
		});
	}

	/**
	 * 返回的时候会执行
	 */
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		User user = oAuth.GetAccessToken(intent);
		
		// UserDao
		Log.i(TAG, "-----" + user.toString());
		getUserInfo(user);
	}
	
	public User getUserInfo(User user){
		String url = "https://api.weibo.com/2/users/show.json";
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("access_token", user.getTokenSecret()));
		params.add(new BasicNameValuePair("uid", user.getUserId()));
		HttpResponse response = oAuth.SignRequest(user.getToken(), user.getTokenSecret(), url, params);
		String result = "";
		if (true) {
			try {
				InputStream is = response.getEntity().getContent();
				Reader reader = new BufferedReader(new InputStreamReader(is), 4000);
				StringBuilder builder = new StringBuilder((int) response.getEntity().getContentLength());
				
				char[] buf = new char[1024];
				int length = 0;
				while((length = reader.read(buf)) != -1){
					builder.append(buf, 0, length);
				}
				reader.close();
				
				result = builder.toString();
				Log.i(TAG, result);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return null;
	}
}

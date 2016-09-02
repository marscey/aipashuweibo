package com.xiaof.aipashuweibo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.xiaof.aipashuweibo.dao.UserDao;
import com.xiaof.aipashuweibo.pojo.User;
import com.xiaof.aipashuweibo.util.Constants;
import com.xiaof.aipashuweibo.util.HttpRequestUtil;
import com.xiaof.aipashuweibo.util.Tools;

/**
 * 用户授权操作
 * 
 * @author chaoyunYip
 * 
 *         2016-8-15
 */
public class OAuthActivity2_0 extends Activity {
	
	private static final String TAG = "OAuthActivity2_0";
	
    private AuthInfo mAuthInfo;
    
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    
    private Dialog dialog = null;
    
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

		dialog = new Dialog(this, R.style.oauth_style);
		dialog.setContentView(view);
		dialog.show();
		Button oauthButton = (Button) view.findViewById(R.id.oauth_start);
		oauthButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Toast.makeText(OAuthActivity.this, "认证", Toast.LENGTH_SHORT).show();
				
				// 请求AccessToken
				// 创建微博实例
				mAuthInfo = new AuthInfo(OAuthActivity2_0.this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
				mSsoHandler = new SsoHandler(OAuthActivity2_0.this, mAuthInfo);
				// SSO 授权, ALL IN ONE 如果手机安装了微博客户端则使用客户端授权,没有则进行网页授权
				mSsoHandler.authorize(new AuthListener());
				
			}
		});
	}

	/**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
    	
		@Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            String code = values.getString("code");
            if (mAccessToken.isSessionValid()) {
            	//
            	SaveUser(mAccessToken);
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                Toast.makeText(OAuthActivity2_0.this, "授权失败："+code, Toast.LENGTH_LONG).show();
                Log.i(TAG, "授权失败："+code);
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(OAuthActivity2_0.this, "授权取消", Toast.LENGTH_LONG).show();
        }
        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(OAuthActivity2_0.this, "授权异常: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
	
    /**
     * 保存 Token 对象到 User，并存入数据库中。
     * 
     * @param token   Token 对象
     */
    public void SaveUser(Oauth2AccessToken token) {
    	
    	User user = Tools.getUserInfo(OAuthActivity2_0.this, token.getUid(), token.getToken());
        if (user!=null) {
			UserDao dao = new UserDao(this);
			dao.inserUser(user);
			//关闭 弹层
			dialog.dismiss();
			
			//跳转到登录
			startActivity(new Intent(this, LoginActivity.class));
			
			//关掉当前Activity
			finish();
		}
    }
}

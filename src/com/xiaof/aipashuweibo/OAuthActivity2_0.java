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
 * �û���Ȩ����
 * 
 * @author chaoyunYip
 * 
 *         2016-8-15
 */
public class OAuthActivity2_0 extends Activity {
	
	private static final String TAG = "OAuthActivity2_0";
	
    private AuthInfo mAuthInfo;
    
    /** ��װ�� "access_token"��"expires_in"��"refresh_token"�����ṩ�����ǵĹ�����  */
    private Oauth2AccessToken mAccessToken;

    /** ע�⣺SsoHandler ���� SDK ֧�� SSO ʱ��Ч */
    private SsoHandler mSsoHandler;
    
    private Dialog dialog = null;
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oauth);
//		����������
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
				// Toast.makeText(OAuthActivity.this, "��֤", Toast.LENGTH_SHORT).show();
				
				// ����AccessToken
				// ����΢��ʵ��
				mAuthInfo = new AuthInfo(OAuthActivity2_0.this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
				mSsoHandler = new SsoHandler(OAuthActivity2_0.this, mAuthInfo);
				// SSO ��Ȩ, ALL IN ONE ����ֻ���װ��΢���ͻ�����ʹ�ÿͻ�����Ȩ,û���������ҳ��Ȩ
				mSsoHandler.authorize(new AuthListener());
				
			}
		});
	}

	/**
     * ΢����֤��Ȩ�ص��ࡣ
     * 1. SSO ��Ȩʱ����Ҫ�� {@link #onActivityResult} �е��� {@link SsoHandler#authorizeCallBack} ��
     *    �ûص��Żᱻִ�С�
     * 2. �� SSO ��Ȩʱ������Ȩ�����󣬸ûص��ͻᱻִ�С�
     * ����Ȩ�ɹ����뱣��� access_token��expires_in��uid ����Ϣ�� SharedPreferences �С�
     */
    class AuthListener implements WeiboAuthListener {
    	
		@Override
        public void onComplete(Bundle values) {
            // �� Bundle �н��� Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            String code = values.getString("code");
            if (mAccessToken.isSessionValid()) {
            	//
            	SaveUser(mAccessToken);
            } else {
                // ���¼�������������յ� Code��
                // 1. ����δ��ƽ̨��ע���Ӧ�ó���İ�����ǩ��ʱ��
                // 2. ����ע���Ӧ�ó��������ǩ������ȷʱ��
                // 3. ������ƽ̨��ע��İ�����ǩ��������ǰ���Ե�Ӧ�õİ�����ǩ����ƥ��ʱ��
                Toast.makeText(OAuthActivity2_0.this, "��Ȩʧ�ܣ�"+code, Toast.LENGTH_LONG).show();
                Log.i(TAG, "��Ȩʧ�ܣ�"+code);
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(OAuthActivity2_0.this, "��Ȩȡ��", Toast.LENGTH_LONG).show();
        }
        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(OAuthActivity2_0.this, "��Ȩ�쳣: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
	
    /**
     * ���� Token ���� User�����������ݿ��С�
     * 
     * @param token   Token ����
     */
    public void SaveUser(Oauth2AccessToken token) {
    	
    	User user = Tools.getUserInfo(OAuthActivity2_0.this, token.getUid(), token.getToken());
        if (user!=null) {
			UserDao dao = new UserDao(this);
			dao.inserUser(user);
			//�ر� ����
			dialog.dismiss();
			
			//��ת����¼
			startActivity(new Intent(this, LoginActivity.class));
			
			//�ص���ǰActivity
			finish();
		}
    }
}

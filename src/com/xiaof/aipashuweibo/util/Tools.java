package com.xiaof.aipashuweibo.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.xiaof.aipashuweibo.LoadActivity;
import com.xiaof.aipashuweibo.R;
import com.xiaof.aipashuweibo.LoginActivity.UserSession;
import com.xiaof.aipashuweibo.pojo.User;
import com.xiaof.aipashuweibo.pojo.WeiboInfo;

/**
 * ������
 * @author chaoyunYip
 *
 * 2016-8-19
 */
public class Tools {
	
	private static final String TAG = "Tools";

	private static Tools instance = null;
	
	private Tools(){}
	
	public static Tools getTnstance(){
		if (instance==null) {
			instance = new Tools();
		}
		return instance;
	}
	
	/**
	 * ��������Ƿ���ã�
	 * @param context
	 */
	public static void checkNetwork(final LoadActivity context){
		if (!isNetworkAvailable(context)) {
			TextView msg = new TextView(context);
			msg.setText("��ǰû�п��õ����磬���������磡");
			
			new AlertDialog.Builder(context)
					.setIcon(R.drawable.not_network)
					.setTitle("����״̬��ʾ")
					.setView(msg)
					.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//��ת�����ý���
							context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
							context.finish();
						}
					}).create().show();
		}
	}
	
	/**
     * �ж�����״̬
     * @param context ������ 
     * @return true�������磻false��û����
     */
    public static boolean isNetworkAvailable(Context context) {
		//�������״̬������
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		}else{
			NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
			if (info != null && info.length>0) {
				for (NetworkInfo networkInfo : info) {
					
					if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
    
    /**
     * ͨ��url ��ö�Ӧ��Drawable��Դ
     * @param url
     * @return
     */
    public static Drawable getDrawableFromUrl(String url){
    	try {
			URLConnection urls = new URL(url).openConnection();
			return Drawable.createFromStream(urls.getInputStream(), "image");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    
    public static List<WeiboInfo> loadHomeData(Context context){
    	User currUser = UserSession.currentUser;
    	if (currUser != null) {
    		
    		WeiboParameters params = new WeiboParameters(Constants.APP_KEY);
            params.put(Constants.KEY_ACCESS_TOKEN, currUser.getToken());
//            params.put("uid", currUser.getUserId());
            
            //��ȡ��ǰ��¼�û���������ע����Ȩ���û�������΢��
            String url = "https://api.weibo.com/2/statuses/friends_timeline.json";
            String result = HttpRequestUtil.requestSync(context, url, params, Constants.HTTPMETHOD_GET);
            return parserUserInfoFromJsonData(result);
		}
    	return null;
    }
    
    //�첽��ȡ
    public static List<WeiboInfo> loadHomeData2(Context context){
    	final List<WeiboInfo> weiboInfoList = new ArrayList<WeiboInfo>();
    	User currUser = UserSession.currentUser;
    	if (currUser != null) {
    		
    		WeiboParameters params = new WeiboParameters(Constants.APP_KEY);
            params.put(Constants.KEY_ACCESS_TOKEN, currUser.getToken());
            
            //��ȡ��ǰ��¼�û���������ע����Ȩ���û�������΢��
            String url = "https://api.weibo.com/2/statuses/friends_timeline.json";
            HttpRequestUtil.requestAsync(context, url, params, Constants.HTTPMETHOD_GET, new RequestListener() {
				
				@Override
				public void onWeiboException(WeiboException arg0) {
					Log.i(TAG, "��ȡ��ǰ��¼�û���������ע����Ȩ���û�������΢���쳣��"+arg0.getMessage());
				}
				
				@Override
				public void onComplete(String arg0) {
//					Log.i(TAG, "��ǰ��¼�û���������ע����Ȩ���û�������΢����"+arg0);
//					weiboInfoList = parserUserInfoFromJsonData(arg0);
				}
			});
		}
    	return weiboInfoList;
    }
    
    public static List<WeiboInfo> parserUserInfoFromJsonData(String jsonStr){
    	List<WeiboInfo> weiboInfoList = null;
    	if (!jsonStr.isEmpty()) {
			try {
				JSONObject jsonObject = new JSONObject(jsonStr);
				
				//�Ƿ����key��statuses�������ݣ�����Ϊ�쳣
				if (jsonObject.has("statuses")) {
					weiboInfoList = new ArrayList<WeiboInfo>();
					JSONArray jsonArray = jsonObject.getJSONArray("statuses");
					//ѭ������΢��
					for (int i = 0; i < jsonArray.length(); i++) {
						WeiboInfo weiboInfo = new WeiboInfo();
						JSONObject json = jsonArray.getJSONObject(i);
						//��ȡ΢����ID
						String id = json.getString("id");
						//��ȡ΢����ʱ��
						String time = json.getString("created_at");
						Date startDate = new Date(time);
						//��õ�ǰʱ��
						Date nowDate = Calendar.getInstance().getTime();
						//�ȽϷ���΢��ʱ��͵�ǰʱ��֮�����ʱ��
						time = DateUtils.twoDateDistance(startDate, nowDate);
						
						//��ȡ΢��������
						String text = json.getString("text");
						//�ж�΢�����ڴ�ͼƬ��Ϣ
						boolean hasImg = false;
						if (json.has("thumbnail_pic")) {
							hasImg = true;
							//��ȡ����ͼURL����
							String thumbnail_pic = json.getString("thumbnail_pic");
							weiboInfo.setImageUrl(thumbnail_pic);
						}
						//��ȡ�û���Ϣ
						JSONObject u = json.getJSONObject("user");
						String userId = u.getString("id");
						String userName = u.getString("screen_name");
						String userIcon = u.getString("profile_image_url");
						
						weiboInfo.setId(id);
						weiboInfo.setImage(hasImg);
						weiboInfo.setText(text);
						weiboInfo.setTime(time);
						weiboInfo.setUserHead(userIcon);
						weiboInfo.setUserId(userId);
						weiboInfo.setUserName(userName);
						weiboInfoList.add(weiboInfo);
					}
				}
			} catch (JSONException e) {
				Log.i(TAG, "��ǰ��¼�û���������ע����Ȩ���û�������΢����JSONת���쳣��"+e.getMessage());
				e.printStackTrace();
			}
		}
		return weiboInfoList;
    }
    
    
    /**
     * ��ȡ�õ�����
     * @param context ������
     * @param url			
     * @param userId
     * @param accessToken
     * @return
     */
	public static User getUserInfo(Context context, String userId, String accessToken){
		User user = null;
    	
    	WeiboParameters params = new WeiboParameters(Constants.APP_KEY);
        params.put("uid", userId);
        params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
        
        //ͬ����ȡ΢���û���Ϣ
        String result = HttpRequestUtil.requestSync(context, Constants.sAPIList.get(Constants.READ_USER), params, Constants.HTTPMETHOD_GET);
    	
        if (!TextUtils.isEmpty(result)) {
        	try {
        		user = new User();
            	user.setUserId(userId);
            	user.setToken(accessToken);
//            	user.setTokenSecret(token.getRefreshToken());
				JSONObject json = new JSONObject(result);
				user.setId(json.getLong("id"));
				user.setUserName(json.getString("name"));
				user.setDescription(json.getString("description"));
				String headUrl = json.getString("profile_image_url");
				user.setUserHead(getDrawableFromUrl(headUrl));
			} catch (JSONException e) {
				Log.i(TAG, "��ȡ΢���û���JSON����ת���쳣��"+e.getMessage());
				e.printStackTrace();
			}
		}
        return user;
	}
}

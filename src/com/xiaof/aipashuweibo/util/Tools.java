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
 * 工具类
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
	 * 检测网络是否可用，
	 * @param context
	 */
	public static void checkNetwork(final LoadActivity context){
		if (!isNetworkAvailable(context)) {
			TextView msg = new TextView(context);
			msg.setText("当前没有可用的网络，请设置网络！");
			
			new AlertDialog.Builder(context)
					.setIcon(R.drawable.not_network)
					.setTitle("网络状态提示")
					.setView(msg)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//跳转到设置界面
							context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
							context.finish();
						}
					}).create().show();
		}
	}
	
	/**
     * 判断网络状态
     * @param context 上下文 
     * @return true：有网络；false：没网络
     */
    public static boolean isNetworkAvailable(Context context) {
		//获得网络状态管理器
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
     * 通过url 获得对应的Drawable资源
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
            
            //获取当前登录用户及其所关注（授权）用户的最新微博
            String url = "https://api.weibo.com/2/statuses/friends_timeline.json";
            String result = HttpRequestUtil.requestSync(context, url, params, Constants.HTTPMETHOD_GET);
            return parserUserInfoFromJsonData(result);
		}
    	return null;
    }
    
    //异步获取
    public static List<WeiboInfo> loadHomeData2(Context context){
    	final List<WeiboInfo> weiboInfoList = new ArrayList<WeiboInfo>();
    	User currUser = UserSession.currentUser;
    	if (currUser != null) {
    		
    		WeiboParameters params = new WeiboParameters(Constants.APP_KEY);
            params.put(Constants.KEY_ACCESS_TOKEN, currUser.getToken());
            
            //获取当前登录用户及其所关注（授权）用户的最新微博
            String url = "https://api.weibo.com/2/statuses/friends_timeline.json";
            HttpRequestUtil.requestAsync(context, url, params, Constants.HTTPMETHOD_GET, new RequestListener() {
				
				@Override
				public void onWeiboException(WeiboException arg0) {
					Log.i(TAG, "获取当前登录用户及其所关注（授权）用户的最新微博异常："+arg0.getMessage());
				}
				
				@Override
				public void onComplete(String arg0) {
//					Log.i(TAG, "当前登录用户及其所关注（授权）用户的最新微博："+arg0);
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
				
				//是否包含key“statuses”的数据，否则为异常
				if (jsonObject.has("statuses")) {
					weiboInfoList = new ArrayList<WeiboInfo>();
					JSONArray jsonArray = jsonObject.getJSONArray("statuses");
					//循环遍历微博
					for (int i = 0; i < jsonArray.length(); i++) {
						WeiboInfo weiboInfo = new WeiboInfo();
						JSONObject json = jsonArray.getJSONObject(i);
						//获取微博的ID
						String id = json.getString("id");
						//获取微博的时间
						String time = json.getString("created_at");
						Date startDate = new Date(time);
						//获得当前时间
						Date nowDate = Calendar.getInstance().getTime();
						//比较发表微博时间和当前时间之间距离时常
						time = DateUtils.twoDateDistance(startDate, nowDate);
						
						//获取微博的内容
						String text = json.getString("text");
						//判断微博存在带图片信息
						boolean hasImg = false;
						if (json.has("thumbnail_pic")) {
							hasImg = true;
							//获取缩略图URL链接
							String thumbnail_pic = json.getString("thumbnail_pic");
							weiboInfo.setImageUrl(thumbnail_pic);
						}
						//获取用户信息
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
				Log.i(TAG, "当前登录用户及其所关注（授权）用户的最新微博，JSON转换异常："+e.getMessage());
				e.printStackTrace();
			}
		}
		return weiboInfoList;
    }
    
    
    /**
     * 获取用的数据
     * @param context 上下文
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
        
        //同步获取微博用户信息
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
				Log.i(TAG, "获取微博用户，JSON数据转换异常："+e.getMessage());
				e.printStackTrace();
			}
		}
        return user;
	}
}

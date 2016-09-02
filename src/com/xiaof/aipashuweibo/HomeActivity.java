package com.xiaof.aipashuweibo;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.xiaof.aipashuweibo.LoginActivity.UserSession;
import com.xiaof.aipashuweibo.adapter.HomeAdapters;
import com.xiaof.aipashuweibo.pojo.User;
import com.xiaof.aipashuweibo.pojo.WeiboInfo;
import com.xiaof.aipashuweibo.util.Constants;
import com.xiaof.aipashuweibo.util.HttpRequestUtil;
import com.xiaof.aipashuweibo.util.Tools;

/**
 * 主页面
 * 
 * @author chaoyunYip
 * 
 *         2016-8-19
 */
public class HomeActivity extends Activity {
	
	ImageView refresh;
	ImageView writer;
	TextView weibo_name;
	ListView home_lv;
	
	private static final String TAG = "HomeActivity";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		refresh = (ImageView) findViewById(R.id.refresh);
		writer = (ImageView) findViewById(R.id.writer);
		weibo_name = (TextView) findViewById(R.id.we_name);
		home_lv = (ListView) findViewById(R.id.home_lv);

		MyClick click = new MyClick();
		refresh.setOnClickListener(click);
		writer.setOnClickListener(click);
//		getNetworkBuilder();
		init();
	}
	
	/**
	 * 安卓2.3以后访问网络增加内容
	 */
	public void getNetworkBuilder(){
//		if (DEVELOPER_MODE) {
	         StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	                 .detectDiskReads()
	                 .detectDiskWrites()
	                 .penaltyDialog()
	                 .detectNetwork()   // or .detectAll() for all detectable problems
	                 .penaltyLog()
	                 .build());
	         StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	                 .detectLeakedSqlLiteObjects()
	                 .detectLeakedClosableObjects()
	                 .penaltyLog()
	                 .penaltyDeath()
	                 .build());
//	     }
	}
	
	public void init(){
//		List<WeiboInfo> weiboInfoList = Tools.loadHomeData(this);
		
//		HomeAdapters adapter = new HomeAdapters(this, weiboInfoList);
//		home_lv.setAdapter(adapter);
		loadData();
	}
	
	public void loadData(){
    	User currUser = UserSession.currentUser;
    	if (currUser != null) {
    		
    		WeiboParameters params = new WeiboParameters(Constants.APP_KEY);
            params.put(Constants.KEY_ACCESS_TOKEN, currUser.getToken());
            
            //获取当前登录用户及其所关注（授权）用户的最新微博
            String url = "https://api.weibo.com/2/statuses/friends_timeline.json";
            //获取用户发布的微博
//            String url = "https://api.weibo.com/2/statuses/user_timeline.json";
            HttpRequestUtil.requestAsync(this, url, params, Constants.HTTPMETHOD_GET, new RequestListener() {
				
				@Override
				public void onComplete(String arg0) {
//					Log.i(TAG, "当前登录用户及其所关注（授权）用户的最新微博："+arg0);
					List<WeiboInfo> weiboInfoList = Tools.parserUserInfoFromJsonData(arg0);
					HomeAdapters adapter = new HomeAdapters(HomeActivity.this, weiboInfoList);
					home_lv.setAdapter(adapter);
					
					home_lv.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Object obj = view.getTag();
							if (obj!=null) {
								String weiboId = obj.toString();
								Intent intent = new Intent(HomeActivity.this, ContentActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("weiboId", weiboId);
								intent.putExtras(bundle);
								startActivity(intent);
							}
						}
					});
				}
            	
				@Override
				public void onWeiboException(WeiboException arg0) {
					Log.i(TAG, "获取当前登录用户及其所关注（授权）用户的最新微博异常："+arg0.getMessage());
				}
			});
		}
	}

	class MyClick implements OnClickListener{
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.refresh:
				Toast.makeText(HomeActivity.this, "登录", Toast.LENGTH_SHORT).show();
				break;
			case R.id.writer:
				startActivity(new Intent(HomeActivity.this, WriterWeiboActivity.class));
				break;
			}
		}
	}
	

}

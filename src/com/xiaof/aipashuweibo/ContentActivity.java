package com.xiaof.aipashuweibo;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.xiaof.aipashuweibo.LoginActivity.UserSession;
import com.xiaof.aipashuweibo.pojo.User;
import com.xiaof.aipashuweibo.util.AsyncImageLoader;
import com.xiaof.aipashuweibo.util.AsyncImageLoader.ImageCallback;
import com.xiaof.aipashuweibo.util.Constants;
import com.xiaof.aipashuweibo.util.DateUtils;
import com.xiaof.aipashuweibo.util.HttpRequestUtil;

/**
 * 主页面
 * 
 * @author chaoyunYip
 * 
 *         2016-8-19
 */
public class ContentActivity extends Activity {
	
	ImageView user_head;
	ImageView pic;
	TextView tv_user_name;
	TextView tv_text;
	TextView tv_time;
	
	private static final String TAG = "HomeActivity";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content);
		
		user_head = (ImageView) findViewById(R.id.user_head);
		pic = (ImageView) findViewById(R.id.pic);
		tv_user_name = (TextView) findViewById(R.id.user_name);
		tv_text = (TextView) findViewById(R.id.text);
//		tv_time = (TextView) findViewById(R.id.time);
		Intent in = this.getIntent();
		if (in!=null) {
			Bundle b = in.getExtras();
			if (b!=null) {
				if (b.containsKey("weiboId")) {
					String weiboId = b.getString("weiboId");
					init(weiboId);
				}
			}
		}
		
	}
	
	
	public void init(String weiboId){
		
		getWeiboById(weiboId);
	}
	
	
	//获取单条微博数据
	public void getWeiboById(String weiboId){
    	User currUser = UserSession.currentUser;
    	if (currUser != null) {
//    		[id=4014570672153350, null, access_token=2.00svraoBK4W3yB1d1b64a06d0u5gcM, null]
//    		08-31 05:01:50.585: I/HomeActivity(2497): 根据微博ID获取单条微博内容异常：{"error":"Permission Denied!","error_code":20112,"request":"/2/statuses/show.json"}
    		
    		WeiboParameters params = new WeiboParameters(Constants.APP_KEY);
            params.put(Constants.KEY_ACCESS_TOKEN, currUser.getToken());
            params.put("id", weiboId);
            
            //根据微博ID获取单条微博内容
            String url = "https://api.weibo.com/2/statuses/show.json";
            HttpRequestUtil.requestAsync(this, url, params, Constants.HTTPMETHOD_GET, new RequestListener() {
				
				@Override
				public void onComplete(String arg0) {
					
					//
					try {
						JSONObject jsonObject = new JSONObject(arg0);
						if (jsonObject!=null) {
							JSONObject jsonUser = jsonObject.getJSONObject("user");
							String userName = jsonUser.getString("screen_name");
							String userImage = jsonUser.getString("profile_image_url");
							String time = jsonObject.getString("created_at");
							String text = jsonObject.getString("text");
							
//							TextView tv_user_name = (TextView) ContentActivity.this.findViewById(R.id.user_name);
							tv_user_name.setText(userName);
							
//							TextView tv_text = (TextView) ContentActivity.this.findViewById(R.id.text);
							tv_text.setText(text);
							
//							ImageView user_head = (ImageView) ContentActivity.this.findViewById(R.id.user_head);
							//使用异步下载图片
							Drawable image = AsyncImageLoader.loadDrawable(
									userImage, user_head, new ImageCallback() {
								@Override
								public void imageSet(Drawable drawable, ImageView imageView) {
									imageView.setImageDrawable(drawable);
								}
							});
							if (image != null) {
								user_head.setImageDrawable(image);
							}
							
							//获得微博图片
							if (jsonObject.has("bmiddle_pic")) {
								String picurl1 = jsonObject.getString("bmiddle_pic");
								String picurl2 = jsonObject.getString("original_pic");
								
//								ImageView pic = (ImageView) findViewById(R.id.pic);
								pic.setTag(picurl2);
								pic.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										Object obj = v.getTag();
										Intent intent = new Intent(ContentActivity.this, null);
										
										Bundle bundle = new Bundle();
										bundle.putString("url", obj.toString());
										intent.putExtras(bundle);
										startActivity(intent);
									}
								});
								Drawable cacheImage = AsyncImageLoader.loadDrawable(
										picurl1, pic, new ImageCallback() {
									@Override
									public void imageSet(Drawable drawable, ImageView imageView) {
										showImg(imageView, drawable);
									}
								});
								if (cacheImage != null) {
									showImg(pic, cacheImage);
								}
							}
							
							//时间
//							Date startDate = new Date(time);
							//获得当前时间
//							Date nowDate = Calendar.getInstance().getTime();
							//比较发表微博时间和当前时间之间距离时常
//							String time2 = DateUtils.twoDateDistance(startDate, nowDate);
//							tv_time.setText(time2);
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
            	
				@Override
				public void onWeiboException(WeiboException arg0) {
					Log.i(TAG, "根据微博ID获取单条微博内容异常："+arg0.getMessage());
				}
			});
		}
	}
	
	/**
	 * 动态调整图片宽高
	 * @param view
	 * @param img
	 */
	private void showImg(ImageView view, Drawable img){
		int h = img.getIntrinsicHeight();
		int w = img.getIntrinsicWidth();
		if (w>300) {
			int hh = 300 * h / w;
			LayoutParams params = view.getLayoutParams();
			params.width = 300;
			params.height = hh;
			view.setLayoutParams(params);
		}
		view.setImageDrawable(img);
	}

	class MyClick implements OnClickListener{
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.refresh:
				Toast.makeText(ContentActivity.this, "登录", Toast.LENGTH_SHORT).show();
				break;
			case R.id.writer:
				Toast.makeText(ContentActivity.this, "取消", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
	

}

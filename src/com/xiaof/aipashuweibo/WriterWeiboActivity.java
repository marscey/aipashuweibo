package com.xiaof.aipashuweibo;

import java.io.File;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.xiaof.aipashuweibo.LoginActivity.UserSession;
import com.xiaof.aipashuweibo.pojo.User;
import com.xiaof.aipashuweibo.util.Constants;
import com.xiaof.aipashuweibo.util.HttpRequestUtil;

/**
 * 写微博
 * 
 * @author chaoyunYip
 * 
 *         2016-8-19
 */
public class WriterWeiboActivity extends Activity {
	
	ImageView refresh_weibo;
	ImageView writer_weibo;
	Button upImage_btn;
	ImageView images;
	EditText weibo_txt;
	TextView tv_text_limit;
	int weibo_max_length = 140;
	
	private static final String TAG = "WriterWeiboActivity";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.writer);
		
		refresh_weibo = (ImageView) findViewById(R.id.refresh);
		refresh_weibo.setVisibility(View.GONE);
		
		writer_weibo = (ImageView) findViewById(R.id.writer);
		upImage_btn = (Button) findViewById(R.id.upImage_btn);
		
		images = (ImageView) findViewById(R.id.images);
		weibo_txt = (EditText) findViewById(R.id.weibo_txt);
		tv_text_limit = (TextView) findViewById(R.id.tv_text_limit);
		
		MyClick onClick = new MyClick();
		writer_weibo.setOnClickListener(onClick);
		upImage_btn.setOnClickListener(onClick);
		
		init();
		
		weibo_txt.addTextChangedListener(new TextWatcher() {
			
			//输入框中的内容改变时调用
			@SuppressLint("ResourceAsColor") @Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String mText = weibo_txt.getText().toString();
				int len = mText.length();
				if (len <= weibo_max_length) {
					len = weibo_max_length - len;
					if (writer_weibo.getVisibility()==View.GONE) {
						writer_weibo.setVisibility(View.VISIBLE);
					}
					tv_text_limit.setTextColor(R.color.green);
					tv_text_limit.setText("还可以输入 "+ len +" 个字");
				}
				else{
					len = len - weibo_max_length;
					if (writer_weibo.getVisibility()==View.VISIBLE) {
						writer_weibo.setVisibility(View.GONE);
					}
					tv_text_limit.setTextColor(R.color.red);
					tv_text_limit.setText("超出 "+ len +" 个字");
				}
			}
			//内容改变前时调用
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) { }
			//内容改变后时调用
			@Override
			public void afterTextChanged(Editable s) { }
		});
	}
	
	public void init(){
		
	}

	class MyClick implements OnClickListener{
		String picPath ;
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.writer:
				User currUser = UserSession.currentUser;
		    	if (currUser != null) {
	//				String url = "https://upload.api.weibo.com/2/statuses/upload.json";
					String url = "https://api.weibo.com/2/statuses/update.json";
					
		    		WeiboParameters params = new WeiboParameters(Constants.APP_KEY);
		    		params.put(Constants.KEY_ACCESS_TOKEN, currUser.getToken());
		            params.put("status", URLEncoder.encode(weibo_txt.getText().toString()));
	//	            params.put("pic", picPath);
		            HttpRequestUtil.requestAsync(WriterWeiboActivity.this, url, params, Constants.HTTPMETHOD_POST, new RequestListener() {
						
						@Override
						public void onWeiboException(WeiboException arg0) {
							Log.i(TAG, "发送单条微博内容异常："+arg0.getMessage());
							arg0.printStackTrace();
						}
						
						@Override
						public void onComplete(String arg0) {
							try {
								JSONObject jsonObject = new JSONObject(arg0);
								if (jsonObject!=null) {
									if (!jsonObject.has("error")) {
										Log.i(TAG, "微博发表成功！");
										//跳转
										startActivity(new Intent(WriterWeiboActivity.this, HomeActivity.class));
										WriterWeiboActivity.this.finish();
									}
								}
								
							} catch (JSONException e) {
								Log.i(TAG, "发送单条微博内容异常："+e.getMessage());
								e.printStackTrace();
							}
							
						}
					});
		    	}
	            break;
			case R.id.upImage_btn:
				File file = Environment.getExternalStorageDirectory();
				picPath = file.getAbsolutePath()+"/1.png";
				Bitmap pic = BitmapFactory.decodeFile(picPath);
				images.setImageBitmap(pic);
				images.setVisibility(View.VISIBLE);
				break;	
			}
		}
	}
	

}

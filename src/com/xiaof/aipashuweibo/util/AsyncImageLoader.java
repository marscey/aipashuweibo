package com.xiaof.aipashuweibo.util;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.http.client.utils.URLEncodedUtils;

import com.sina.weibo.sdk.utils.MD5;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
/**
 * 异步下载图片数据
 * @author chaoyunYip
 *
 * 2016-8-26
 */
public class AsyncImageLoader {
	
	/**
	 * 图片数据缓存
	 * key = url
	 * value = 图片资源对象
	 */
	private static HashMap<String, SoftReference<Drawable>> imageCaceh;
	
	public AsyncImageLoader(){
		if (imageCaceh==null) {
			imageCaceh = new HashMap<String, SoftReference<Drawable>>();
		}
	}
	
	/**
	 * 异步下载图片
	 * @param url 图片的地址
	 * @param imageView 需要显示的图片
	 * @param callback 回调接口
	 * @return 图片资源
	 * @throws UnsupportedEncodingException 
	 */
	public static Drawable loadDrawable(final String url, final ImageView imageView, final ImageCallback callback){
		try {
			if (imageCaceh==null) {
				imageCaceh = new HashMap<String, SoftReference<Drawable>>();
			}
			final String urlEncoded = URLEncoder.encode(url, "UTF-8");
			if (imageCaceh.containsKey(urlEncoded)) {
				SoftReference<Drawable> softReference = imageCaceh.get(urlEncoded);
				Drawable dra = softReference.get();
				if (dra!=null) {
					return dra;
				}
			}

			final Handler handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					//图片资源设置操作
					callback.imageSet((Drawable)msg.obj, imageView);
				}
				
			};
			
			//下载操作
			new Thread(){
				public void run(){
					Drawable drawable = Tools.getDrawableFromUrl(url);
					//设置缓存，避免重复下载相同的图片资源
					imageCaceh.put(urlEncoded, new SoftReference<Drawable>(drawable));
					Message msg = handler.obtainMessage();
					msg.obj = drawable;
					handler.sendMessage(msg);
				}
			}.start();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 回调接口
	 * @author chaoyunYip
	 *
	 * 2016-8-26
	 */
	public interface ImageCallback{
		/**
		 * 图片资源设置
		 * @param drawable
		 * @param imageView
		 */
		public void imageSet(Drawable drawable, ImageView imageView);
	}
	
}

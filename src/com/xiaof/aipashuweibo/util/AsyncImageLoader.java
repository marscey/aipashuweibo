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
 * �첽����ͼƬ����
 * @author chaoyunYip
 *
 * 2016-8-26
 */
public class AsyncImageLoader {
	
	/**
	 * ͼƬ���ݻ���
	 * key = url
	 * value = ͼƬ��Դ����
	 */
	private static HashMap<String, SoftReference<Drawable>> imageCaceh;
	
	public AsyncImageLoader(){
		if (imageCaceh==null) {
			imageCaceh = new HashMap<String, SoftReference<Drawable>>();
		}
	}
	
	/**
	 * �첽����ͼƬ
	 * @param url ͼƬ�ĵ�ַ
	 * @param imageView ��Ҫ��ʾ��ͼƬ
	 * @param callback �ص��ӿ�
	 * @return ͼƬ��Դ
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
					//ͼƬ��Դ���ò���
					callback.imageSet((Drawable)msg.obj, imageView);
				}
				
			};
			
			//���ز���
			new Thread(){
				public void run(){
					Drawable drawable = Tools.getDrawableFromUrl(url);
					//���û��棬�����ظ�������ͬ��ͼƬ��Դ
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
	 * �ص��ӿ�
	 * @author chaoyunYip
	 *
	 * 2016-8-26
	 */
	public interface ImageCallback{
		/**
		 * ͼƬ��Դ����
		 * @param drawable
		 * @param imageView
		 */
		public void imageSet(Drawable drawable, ImageView imageView);
	}
	
}

package com.xiaof.aipashuweibo.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaof.aipashuweibo.R;
import com.xiaof.aipashuweibo.pojo.WeiboInfo;
import com.xiaof.aipashuweibo.util.AsyncImageLoader;
import com.xiaof.aipashuweibo.util.AsyncImageLoader.ImageCallback;
/**
 * 微博Adapter
 * @author chaoyunYip
 *
 * 2016-8-26
 */
public class HomeAdapters extends BaseAdapter {
	
	private List<WeiboInfo> weiboInfoList = null;
	private Context context = null;
	
	public HomeAdapters(Context context, List<WeiboInfo> weiboInfoList){
		this.context = context;
		this.weiboInfoList = weiboInfoList;
	}
	
	class ContenHolder{
		public ImageView content_image;
		public ImageView content_user_head;
		public TextView content_user_name;
		public TextView content_time;
		public TextView content_text;
	}
	
	@SuppressLint("ViewHolder") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		convertView = View.inflate(context, R.layout.home_item, null);
		ContenHolder holder = new ContenHolder();
		
		//关联组件，提高效率
		holder.content_image = (ImageView) convertView.findViewById(R.id.content_image2);
		holder.content_user_head = (ImageView) convertView.findViewById(R.id.content_head);
		holder.content_user_name = (TextView) convertView.findViewById(R.id.content_user);
		holder.content_time = (TextView) convertView.findViewById(R.id.content_time2);
		holder.content_text = (TextView) convertView.findViewById(R.id.content_text);
		
		WeiboInfo weibo = weiboInfoList.get(position);
		
		if (weibo!=null) {
			//设置标签，方便下次获取
			convertView.setTag(weibo.getId());
			
			holder.content_user_name.setText(weibo.getUserName());
			holder.content_time.setText(weibo.getTime());
			holder.content_text.setText(weibo.getText());
			
			//判断微博中是否包含图片
			if (weibo.isImage()) {
				
				//使用异步下载图片
				Drawable image = AsyncImageLoader.loadDrawable(
						weibo.getImageUrl(), holder.content_image, new ImageCallback() {
					@Override
					public void imageSet(Drawable drawable, ImageView imageView) {
						imageView.setImageDrawable(drawable);
					}
				});
				
				if (image != null) {
					holder.content_image.setImageDrawable(image);
				}else{
//					holder.content_image.setImageResource(R.drawable.default_image);
				}
			}
			
			//使用异步下载头像图片
			Drawable headImage = AsyncImageLoader.loadDrawable(
					weibo.getUserHead(), holder.content_user_head, new ImageCallback() {
				@Override
				public void imageSet(Drawable drawable, ImageView imageView) {
					imageView.setImageDrawable(drawable);
				}
			});
			
			if (headImage != null) {
				holder.content_user_head.setImageDrawable(headImage);
			}else{
//				holder.content_user_head.setImageResource(R.drawable.default_image);
			}
		}
		return convertView;
	}
	
	@Override
	public int getCount() {
		return this.weiboInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.weiboInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}

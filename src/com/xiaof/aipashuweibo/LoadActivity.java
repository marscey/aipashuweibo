package com.xiaof.aipashuweibo;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiaof.aipashuweibo.dao.UserDao;
import com.xiaof.aipashuweibo.pojo.User;
import com.xiaof.aipashuweibo.util.Tools;


public class LoadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        
        ImageView imageView = (ImageView) findViewById(R.id.load_image);
        //透明度渐变动画，（取值范围 0.0完全不显示 - 1.0完全显示）
        AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
        //设置动画持续时间
        animation.setDuration(5000);
        //将组件和动画关联
        imageView.setAnimation(animation);
        
        animation.setAnimationListener(new AnimationListener() {
			
        	//动画启动的时候执行
			public void onAnimationStart(Animation animation) {
			//	Toast.makeText(LoadActivity.this, "动画开始", Toast.LENGTH_SHORT).show();
				init();
			}
			
			//动画结束的时候执行
			public void onAnimationEnd(Animation animation) {
			//	Toast.makeText(LoadActivity.this, "动画结束", Toast.LENGTH_SHORT).show();
				
				//跳转登录界面
	    		Intent intent = new Intent(LoadActivity.this, LoginActivity.class);
	    		startActivity(intent);
	    		finish();
			}
			
			//动画重复执行的时候执行
			public void onAnimationRepeat(Animation animation) {}
		});
    }
    
    public void init(){
    	//网络状态判断
    	Tools.checkNetwork(LoadActivity.this);
    	
    	//检测用户是否存在
    	UserDao dao = new UserDao(this);
    	List<User> userList = dao.findAllUsers();
    	if (userList==null || userList.isEmpty()) {
    		Intent intent = new Intent(this, OAuthActivity2_0.class);
    		startActivity(intent);
    		finish();
		}
    }

}

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
        //͸���Ƚ��䶯������ȡֵ��Χ 0.0��ȫ����ʾ - 1.0��ȫ��ʾ��
        AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
        //���ö�������ʱ��
        animation.setDuration(5000);
        //������Ͷ�������
        imageView.setAnimation(animation);
        
        animation.setAnimationListener(new AnimationListener() {
			
        	//����������ʱ��ִ��
			public void onAnimationStart(Animation animation) {
			//	Toast.makeText(LoadActivity.this, "������ʼ", Toast.LENGTH_SHORT).show();
				init();
			}
			
			//����������ʱ��ִ��
			public void onAnimationEnd(Animation animation) {
			//	Toast.makeText(LoadActivity.this, "��������", Toast.LENGTH_SHORT).show();
				
				//��ת��¼����
	    		Intent intent = new Intent(LoadActivity.this, LoginActivity.class);
	    		startActivity(intent);
	    		finish();
			}
			
			//�����ظ�ִ�е�ʱ��ִ��
			public void onAnimationRepeat(Animation animation) {}
		});
    }
    
    public void init(){
    	//����״̬�ж�
    	Tools.checkNetwork(LoadActivity.this);
    	
    	//����û��Ƿ����
    	UserDao dao = new UserDao(this);
    	List<User> userList = dao.findAllUsers();
    	if (userList==null || userList.isEmpty()) {
    		Intent intent = new Intent(this, OAuthActivity2_0.class);
    		startActivity(intent);
    		finish();
		}
    }

}

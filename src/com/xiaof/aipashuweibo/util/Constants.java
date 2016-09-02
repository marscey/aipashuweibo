package com.xiaof.aipashuweibo.util;

import android.util.SparseArray;


public class Constants {

    /** Ӧ�õ� APP_KEY��������Ӧ��Ӧ��ʹ���Լ��� APP_KEY �滻�� APP_KEY */
	public static final String APP_KEY      = "1808080652";
	
	/** Ӧ�õ� APP_SECRET��������Ӧ��Ӧ��ʹ���Լ��� APP_SECRET �滻�� APP_SECRET */
	public static final String APP_SECRET	= "5d666d848f2f2f0363209bc7a927ea35";		   	// Ӧ�õ�App_SECRET
    /** 
     * Ӧ�õĻص�ҳ��������Ӧ�ÿ���ʹ���Լ��Ļص�ҳ��
     * 
     * <p>
     * ע��������Ȩ�ص�ҳ���ƶ��ͻ���Ӧ����˵���û��ǲ��ɼ��ģ����Զ���Ϊ������ʽ������Ӱ�죬
     * ����û�ж��彫�޷�ʹ�� SDK ��֤��¼��
     * ����ʹ��Ĭ�ϻص�ҳ��https://api.weibo.com/oauth2/default.html
     * </p>
     */
	public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";	// Ӧ�õĻص�ҳ
    /**
     * Scope �� OAuth2.0 ��Ȩ������ authorize �ӿڵ�һ��������ͨ�� Scope��ƽ̨�����Ÿ����΢��
     * ���Ĺ��ܸ������ߣ�ͬʱҲ��ǿ�û���˽�������������û����飬�û����� OAuth2.0 ��Ȩҳ����Ȩ��
     * ѡ����Ӧ�õĹ��ܡ�
     * 
     * ����ͨ������΢������ƽ̨-->��������-->�ҵ�Ӧ��-->�ӿڹ������ܿ�������Ŀǰ������Щ�ӿڵ�
     * ʹ��Ȩ�ޣ��߼�Ȩ����Ҫ�������롣
     * 
     * Ŀǰ Scope ֧�ִ����� Scope Ȩ�ޣ��ö��ŷָ���
     * 
     * �й���Щ OpenAPI ��ҪȨ�����룬��鿴��http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * ���� Scope ���ע�������鿴��http://open.weibo.com/wiki/Scope
     */
	public static final String SCOPE		= 							   					// Ӧ������ĸ߼�Ȩ��
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";


	/** ����΢������ӿڵĵ�ַ */
	public static final String API_SERVER       = "https://api.weibo.com/2";
    /** POST ����ʽ */
	public static final String HTTPMETHOD_POST  = "POST";
    /** GET ����ʽ */
    public static final String HTTPMETHOD_GET   = "GET";
    
    /** HTTP ���� */
    public static final String KEY_ACCESS_TOKEN = "access_token";
    
    public static final int READ_USER           = 0;
    public static final int READ_USER_BY_DOMAIN = 1;
    public static final int READ_USER_COUNT     = 2;
    public static final String API_BASE_URL = Constants.API_SERVER + "/users";
    public static final SparseArray<String> sAPIList = new SparseArray<String>();
    static {
        sAPIList.put(READ_USER,           API_BASE_URL + "/show.json");
        sAPIList.put(READ_USER_BY_DOMAIN, API_BASE_URL + "/domain_show.json");
        sAPIList.put(READ_USER_COUNT,     API_BASE_URL + "/counts.json");
    }
}
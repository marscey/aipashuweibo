package com.xiaof.aipashuweibo.util;

import android.util.SparseArray;


public class Constants {

    /** 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY */
	public static final String APP_KEY      = "1808080652";
	
	/** 应用的 APP_SECRET，第三方应用应该使用自己的 APP_SECRET 替换该 APP_SECRET */
	public static final String APP_SECRET	= "5d666d848f2f2f0363209bc7a927ea35";		   	// 应用的App_SECRET
    /** 
     * 应用的回调页，第三方应用可以使用自己的回调页。
     * 
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
	public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";	// 应用的回调页
    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * 
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     * 
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * 
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
	public static final String SCOPE		= 							   					// 应用申请的高级权限
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";


	/** 访问微博服务接口的地址 */
	public static final String API_SERVER       = "https://api.weibo.com/2";
    /** POST 请求方式 */
	public static final String HTTPMETHOD_POST  = "POST";
    /** GET 请求方式 */
    public static final String HTTPMETHOD_GET   = "GET";
    
    /** HTTP 参数 */
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
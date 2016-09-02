package com.xiaof.aipashuweibo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.params.CoreConnectionPNames;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.utils.LogUtil;

public class HttpRequestUtil {

	private static final String TAG = "HttpRequestUtil";

	/**
     * HTTP 同步请求。
     * 
     * @param context	  上下文
     * @param url        请求的地址
     * @param params     请求的参数
     * @param httpMethod 请求方法
     * 
     * @return 同步请求后，服务器返回的字符串。
     */
	public static String requestSync(Context context, String url, WeiboParameters params, String httpMethod) {
        if (null == context
                || TextUtils.isEmpty(url)
                || null == params
                || TextUtils.isEmpty(httpMethod)) {
            LogUtil.e(TAG, "参数出错!");
            return "";
        }
        return new AsyncWeiboRunner(context).request(url, params, httpMethod);
    }
    
    /**
     * HTTP 异步请求。
     * 
     * @param context	  上下文
     * @param url        请求的地址
     * @param params     请求的参数
     * @param httpMethod 请求方法
     * @param listener   请求后的回调接口
     */
    public static void requestAsync(Context context, String url, WeiboParameters params, String httpMethod, RequestListener listener) {
        if (null == context
                || TextUtils.isEmpty(url)
                || null == params
                || TextUtils.isEmpty(httpMethod)
                || null == listener) {
            LogUtil.e(TAG, "参数出错!");
            return;
        }
        new AsyncWeiboRunner(context).requestAsync(url, params, httpMethod, listener);
    }
	
	/**
	 * 执行一个HTTP GET请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param queryString
	 *            请求的查询参数,可以为null
	 * @return 返回请求响应的HTML
	 */
	public static String doGet(String url, String queryString, int connTimeOut) {
		String response = null;
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		try {
			if (!TextUtils.isEmpty(queryString)) {
				method.setQueryString(URIUtil.encodeQuery(queryString));
			}
			client.getParams().setIntParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, connTimeOut);
			// client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
			// timeOut);
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				response = method.getResponseBodyAsString();
			}
		} catch (URIException e) {
			Log.i(TAG, "执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！" + e);
		} catch (IOException e) {
			Log.i(TAG, "执行HTTP Get请求" + url + "时，发生异常！" + e);
		} finally {
			method.releaseConnection();
		}
		return response;
	}

	/**
	 * 执行一个HTTP GET请求，返回请求响应的状态码
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param connTimeOut
	 *            连接超时时间
	 * @return 返回请求状态码
	 */
	public static int doGet(String url, int connTimeOut) {
		int status = -1;
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		try {
			client.getParams().setIntParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, connTimeOut);
			client.executeMethod(method);
			status = method.getStatusCode();

		} catch (IOException e) {
			Log.i(TAG, "执行HTTP Get请求" + url + "时，发生异常！" + e);
		} finally {
			method.releaseConnection();
		}
		return status;
	}

	/**
	 * 执行一个HTTP GET请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param queryString
	 *            请求的查询参数,可以为null
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的HTML
	 */
	public static String doGet(String url, String queryString, String charset,
			boolean pretty) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		// GetMethod method = new GetMethod(url);
		try {
			if (!TextUtils.isEmpty(queryString)){
				// 对get请求参数做了http请求默认编码，好像没有任何问题，汉字编码后，就成为%式样的字符串
				method.setQueryString(URIUtil.encodeQuery(queryString));
			}
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (URIException e) {
			Log.i(TAG, "执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！" + e);
		} catch (IOException e) {
			Log.i(TAG, "执行HTTP Get请求" + url + "时，发生异常！" + e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	/**
	 * 执行一个HTTP POST请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的查询参数,可以为null
	 * @return 返回请求响应的HTML
	 */
	public static String doPost(String url, Map<String, String> params,
			int timeOut) {
		if (timeOut <= 0) {
			timeOut = 60000;
		}
		String response = null;
		HttpClient client = new HttpClient();
		// HttpMethod method = new PostMethod(url);
		PostMethod method = new PostMethod(url);
		// 设置Http Post数据
		if (params != null) {
			// HttpMethodParams p = new HttpMethodParams();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				// p.setParameter(entry.getKey(), entry.getValue());
				method.addParameter(entry.getKey(), entry.getValue());
			}
			// method.setParams(p);
		}
		try {
			// System.out.println(method.toString());
			// HTTP请求时设置超时时间是明智的，避免死等。
			client.getParams().setIntParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, timeOut);
			// 也可以这样，下面这行代码本质上也是通过上面的代码实现
			// HttpConnectionParams.setConnectionTimeout(client.getParams(),
			// 15000);
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				response = method.getResponseBodyAsString();
			}
		} catch (IOException e) {
			Log.i(TAG, "执行HTTP Post请求" + url + "时，发生异常！" + e);
		} finally {
			method.releaseConnection();
		}

		return response;
	}

	/**
	 * 执行一个HTTP POST请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的查询参数,可以为null
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的HTML
	 */
	public static String doPost(String url, Map<String, String> params,
			String charset, boolean pretty) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		// HttpMethod method = new PostMethod(url);
		PostMethod method = new PostMethod(url);
		// 设置Http Post数据
		if (params != null) {
			HttpMethodParams p = new HttpMethodParams();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				// p.setParameter(entry.getKey(), entry.getValue());
				method.addParameter(entry.getKey(), entry.getValue());
			}
			// p.setParameter("haha", "efg");
			// method.setParams(p);
		}
		try {
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (IOException e) {
			Log.i(TAG, "执行HTTP Post请求" + url + "时，发生异常！" + e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}
	

	public static void main(String[] args) {

		Map<String, String> map = new HashMap<String, String>();
		String aa = "http://127.0.0.1:8093/OutWork/OAInterface.oa?method=addUserInfo";
		String cc = "{\"employeeID\":4418,\"usename\":\"陈红\",\"passWord\":\"20140625\",\"duty\":\"\",\"groupId\":0,\"phoneNumber\":\"15899958122\",\"email\":\"\",\"img_url\":\"\"}";
		try {
			cc = URLEncoder.encode(cc, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("params", cc);
		System.out.println(doPost(aa, map, 0));
	}

}

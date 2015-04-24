package net.ememed.doctor2.network;

import java.io.UnsupportedEncodingException;

import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


import com.android.volley.AuthFailureError;

import java.util.Map;

import net.ememed.doctor2.R;
import net.ememed.doctor2.cache.Cache;
import net.ememed.doctor2.entity.OrderListEntity;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.TextUtil;


/**
 *@author chen  5月19日
 */
public class GsonRequest<T> extends Request<T> {
	private final Gson gson = new Gson();
	private final Class<T> clazz;
	private final Map<String, String> headers;
	private final Response.Listener<T> listener;
	private String url;
	private Map<String, String> params;

	/**
	 * 带参数 带头(Header)的 GET POST 请求
	 * 
	 * @param method
	 * @param url
	 * @param clazz
	 * @param headers
	 * @param params
	 * @param listener
	 * @param errorListener
	 */
	public GsonRequest(int method, String url, Class<T> clazz,
			Map<String, String> headers, Map<String, String> params,
			Response.Listener<T> listener, Response.ErrorListener errorListener) {
		super(method, url, errorListener);
		this.clazz = clazz;
		this.headers = headers;
		this.listener = listener;
		this.url = url;
		this.params = params;
		setRetryPolicy(new DefaultRetryPolicy(20*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

	/**
	 * 不带参数的请求 GET POST
	 * 
	 * @param method
	 * @param url
	 * @param clazz
	 * @param listener
	 * @param errorListener
	 */
	public GsonRequest(int method, String url, Class<T> clazz,
			Response.Listener<T> listener, Response.ErrorListener errorListener) {
		this(method, url, null, null, listener, errorListener);
	}

	/**
	 * 带参数的请求 GET POST
	 * 
	 * @param method
	 *            GET POST
	 * @param url
	 * @param clazz
	 * @param params
	 *            带参数的请求
	 * @param listener
	 * @param errorListener
	 */
	public GsonRequest(int method, String url, Class<T> clazz,
			Map<String, String> params, Response.Listener<T> listener,
			Response.ErrorListener errorListener) {
		this(method, url, clazz, null, params, listener, errorListener);

	}

	/**
	 * 不带参数默认GET 请求方法
	 * 
	 * @param url
	 *            传入url http://.... 格式错误可能报NULLPointer
	 * @param clazz
	 *            需要转化的实体类
	 * @param listener
	 *            传入成功监听listener
	 * @param errorListener
	 *            失败listener
	 */
	public GsonRequest(String url, Class<T> clazz,
			Response.Listener<T> listener, Response.ErrorListener errorListener) {
		this(Method.GET, url, clazz, null, null, listener, errorListener);
	}
	
	@Override
	public RetryPolicy getRetryPolicy() { //处理超时、网络重连等
	    RetryPolicy retryPolicy = new DefaultRetryPolicy(20*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT); 
	    return retryPolicy; 
	}
	// default for POST PUT
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return params != null ? params : super.getParams();
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers != null ? headers : super.getHeaders();
	}

	@Override
	protected void deliverResponse(T response) {
		listener.onResponse(response);
	}

	/**
	 * 处理网络返回
	 * 
	 * @param response
	 * @return
	 */
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
//			String json = new String(response.data,
//					HttpHeaderParser.parseCharset(response.headers));
			String json = new String(response.data,"utf-8");
			String str1 = ":\"\",";
			String str2 = ":null,";
			String str3 = ":\"\"}";
			String str4 = ":null}";
			String replace = json.replace(str1,str2);
			json = replace.replace(str3, str4);
			json = TextUtil.substring(json, "{");
			
			/*if (!TextUtils.isEmpty(json)) {
				Logger.dout("result:"+json + "");
			}*/
			
//			Cache.getDataCache().saveToCache(url, json);
			
			/*try{
				OrderListEntity entity = (OrderListEntity) gson.fromJson(json, clazz);
				System.out.println("entity  = "+entity);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("err  = "+e.getMessage().toString());
			}*/
			
			System.out.println("url = "+url +"json>>>>"+json);
			T obj = null;
			try {
				obj = gson.fromJson(json, clazz);
			} catch (Exception e) {
				json = json.replace("\":[]", "\":null");
				obj = gson.fromJson(json, clazz);
			}
			return Response.success(obj,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return Response.error(new ParseError(e));
		}
	}
}

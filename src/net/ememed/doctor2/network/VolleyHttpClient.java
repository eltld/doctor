/*
 * Copyright (c) 2014.
 * Jackrex
 */

package net.ememed.doctor2.network;

import android.util.Base64;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;

import net.ememed.doctor2.R;
import net.ememed.doctor2.util.NetWorkUtils;

/**
 * 封装volley
 */
public class VolleyHttpClient {


    private static final String CLIENT_ID = "" ; //replace your clientid;
    private static final String CLIENT_SECRET = "";

    private static HttpService httpService;
    private final static Gson gson = new Gson();

    public static  String getAbsoluteUrl(String relativeUrl) {
        return HttpUtil.URI + relativeUrl;
    }

    public VolleyHttpClient(HttpService httpService){
        this.httpService = httpService;
    }

    /**
     * 传入初始化好的httpService 实例
     * @param httpService
     * @return
     */
    public static VolleyHttpClient newInstance (HttpService httpService ){

        if(httpService !=null){
            return new VolleyHttpClient(httpService);
        }
        return null;
    }


	public void get(String url, Class clazz, Response.Listener listener, Response.ErrorListener errorListener) {

       GsonRequest request = new GsonRequest(Request.Method.GET, getAbsoluteUrl(url), clazz, null , null, listener, errorListener);

       HttpService.httpQueue.getCache().invalidate(getAbsoluteUrl(url), true);
        if(!NetWorkUtils.detect(httpService.getContext())) {

            if (HttpService.httpQueue.getCache().get(getAbsoluteUrl(url)) !=null){
                String cacheStr = new String(HttpService.httpQueue.getCache().get(getAbsoluteUrl(url)).data);

                if (cacheStr != null){

                    try {
                        listener.onResponse(gson.fromJson(cacheStr, clazz));
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
            return;
        }
        httpService.addToRequestQueue(request,url);
    }


    public  static void  getStatic(String url, Class clazz, Response.Listener listener, Response.ErrorListener errorListener, String[] keys, String[] values) {
        getWithHeaderStatic(url, clazz, null, listener, errorListener, keys ,values);
    }


    public static void getWithHeaderStatic(String url, Class clazz, Map<String, String> header, Response.Listener listener,  Response.ErrorListener errorListener, String[] keys, String[] values){

        Map<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < keys.length; i++) {
            params.put(keys[i], values[i]);
        }

        GsonRequest request = new GsonRequest(Request.Method.GET, getAbsoluteUrl(url), clazz, header, params, listener, errorListener);

        HttpService.httpQueue.getCache().invalidate(getAbsoluteUrl(url), true);
        if(!NetWorkUtils.detect(httpService.getContext())) {

            if (HttpService.httpQueue.getCache().get(getAbsoluteUrl(url)) !=null){
                String cacheStr = new String(HttpService.httpQueue.getCache().get(getAbsoluteUrl(url)).data);

                if (cacheStr != null){

                    try {
                        listener.onResponse(gson.fromJson(cacheStr, clazz));

                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }

                    return;
                }
            }
            return;
        }
        httpService.addToRequestQueue(request,url);
    }

    /**
     *
     * @param url
     * @param clazz
     * @param header
     * @param listener
     * @param errorListener
     * @param keys
     * @param values
     */
    public  void getWithHeader(String url, Class clazz, Map<String, String> header, Response.Listener listener,  Response.ErrorListener errorListener, String[] keys, String[] values){

        Map<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < keys.length; i++) {
            params.put(keys[i], values[i]);
        }

        GsonRequest request = new GsonRequest(Request.Method.GET, getAbsoluteUrl(url), clazz, header, params, listener, errorListener);
        HttpService.httpQueue.getCache().invalidate(getAbsoluteUrl(url), true);
        if(!NetWorkUtils.detect(httpService.getContext()))
        {

            if (HttpService.httpQueue.getCache().get(getAbsoluteUrl(url)) !=null){
                String cacheStr = new String(HttpService.httpQueue.getCache().get(getAbsoluteUrl(url)).data);

                if (cacheStr != null){

                    try {

                        listener.onResponse(gson.fromJson(cacheStr, clazz));

                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }

                    return;
                }
            }
            return;
        }
        httpService.addToRequestQueue(request,url);
    }
    /**
     *
     * @param url
     * @param clazz
     * @param listener
     * @param errorListener
     * @param keys
     * @param values
     */
    public  void getWithParams(String url, Class clazz, Response.Listener listener,  Response.ErrorListener errorListener, String[] keys, String[] values){

        Map<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < keys.length; i++) {
            params.put(keys[i], values[i]);
        }
    	params.put("channel", "android");
        GsonRequest request = new GsonRequest(Request.Method.GET, getAbsoluteUrl(url), clazz, null, params, listener, errorListener);

        HttpService.httpQueue.getCache().invalidate(getAbsoluteUrl(url), true);
        if(!NetWorkUtils.detect(httpService.getContext())) {

            if (HttpService.httpQueue.getCache().get(getAbsoluteUrl(url)) !=null){
                String cacheStr = new String(HttpService.httpQueue.getCache().get(getAbsoluteUrl(url)).data);

                if (cacheStr != null){

                    try {
                        listener.onResponse(gson.fromJson(cacheStr, clazz));
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
            return;
        }
        httpService.addToRequestQueue(request,url);
    }

    public void getTokenOauth(String url, Class clazz, Response.Listener listener, Response.ErrorListener errorListener){

        Map<String, String> header = new HashMap<String, String>();

        String accessToken = "";
        header.put("Authorization", "Bearer"+" "+ accessToken);
        GsonRequest request = new GsonRequest(Request.Method.GET, getAbsoluteUrl(url), clazz, header , null, listener, errorListener);


        HttpService.httpQueue.getCache().invalidate(getAbsoluteUrl(url), true);
        if(!NetWorkUtils.detect(httpService.getContext()))
        {

            if (HttpService.httpQueue.getCache().get(getAbsoluteUrl(url)) !=null){
                String cacheStr = new String(HttpService.httpQueue.getCache().get(getAbsoluteUrl(url)).data);

                if (cacheStr != null){

                    try {
                        listener.onResponse(gson.fromJson(cacheStr, clazz));
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }

                    return;
                }
            }
            return;
        }
        httpService.addToRequestQueue(request,url);
    }

    /**
     *
     * @param url
     * @param clazz
     * @param listener
     * @param errorListener
     */
    public  void post(String url, Class clazz, Response.Listener listener,  Response.ErrorListener errorListener) {
        postWithHeader(url, clazz, null, listener, errorListener);
    }

    /**
     * @param url
     * @param clazz
     * @param params
     * @param listener
     * @param errorListener
     */
    public  void postWithParams(String url, Class clazz, Map<String, String> params, Response.Listener listener,  Response.ErrorListener errorListener){
    	
    	if (null == params) {
    		params = new HashMap<String,String>();
		}
    	params.put("channel", "android");
        GsonRequest request = new GsonRequest(Request.Method.POST, getAbsoluteUrl(url), clazz, null, params, listener, errorListener);
        RetryPolicy retryPolicy = new DefaultRetryPolicy(15*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        httpService.addToRequestQueue(request);
    }


    public  void postWithHeader(String url, Class clazz, Map<String, String> header, Response.Listener listener, Response.ErrorListener errorListener){

        GsonRequest request = new GsonRequest(Request.Method.POST, getAbsoluteUrl(url), clazz, header, null, listener, errorListener);
        httpService.addToRequestQueue(request);
    }

    public  void postOAuth(String url, Class clazz, Response.Listener listener, Response.ErrorListener errorListener){

        Map<String, String> header = new HashMap<String, String>();
        //replace your token
        String accessToken = "";
        header.put("Authorization", "Bearer"+" "+ accessToken);
        postWithHeader(url,clazz,header,listener,errorListener);
    }

    public  void postOAuthWithParams(String url, Class clazz,Map<String, String> params, Response.Listener listener, Response.ErrorListener errorListener){
    	if (null == params) {
    		params = new HashMap<String,String>();
		}
    	params.put("channel", "android");
    	Map<String, String> header = new HashMap<String, String>();
        header.put("Authorization", "Basic"+" "+ Base64.encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes(), Base64.NO_WRAP));
        GsonRequest request = new GsonRequest(Request.Method.POST, getAbsoluteUrl(url), clazz, header, params, listener, errorListener);
        httpService.addToRequestQueue(request,url);
    }

    public  void postOAuthWithTokenParams(String url, Class clazz,Map<String, String> params, Response.Listener listener, Response.ErrorListener errorListener){
    	if (null == params) {
    		params = new HashMap<String,String>();
		}
    	params.put("channel", "android");
    	Map<String, String> header = new HashMap<String, String>();
        String accessToken = "";
        header.put("Authorization", "Bearer"+" "+ accessToken);
        GsonRequest request = new GsonRequest(Request.Method.POST, getAbsoluteUrl(url), clazz, header, params, listener, errorListener);
        httpService.addToRequestQueue(request,url);
     }
    
	 public void cancelRequest(String url){
	 	httpService.cancelRequests(url);
	 }
}

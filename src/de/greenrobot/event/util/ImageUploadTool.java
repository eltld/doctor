package de.greenrobot.event.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BitmapPutUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ImageUploadTool {
	private LruCache<String, Bitmap> mMemoryCache;
	private ExecutorService mImageThreadPool = null;
	int count = 0;
	private static Object lock = new Object();
	List<String> listURL;
	private DownloadStateListener listener;
	private BasicActivity activity;
	private ArrayList<BasicNameValuePair> params;
	private String httpUrl;
	private String extName;
//	List<String> successURL;
	
	Map<String, String> successURL;
	
	/**
	 * 
	 * @param context 上下文
	 * @param listURL 图片url数组
	 * @param params 上转参数
	 * @param httpUrl 上传路径
	 * @param listener 回调接口
	 * @param extName 文件名后缀
	 */
	public ImageUploadTool(Context context, List<String> listURL,ArrayList<BasicNameValuePair> params,String httpUrl,String extName,
			DownloadStateListener listener) {

		this.listURL = listURL;
		this.listener = listener;
		this.httpUrl = httpUrl;
		this.params = params;
		this.extName = extName;
		
		successURL = new HashMap<String, String>();
		
//		successURL = new ArrayList<String>();
		
		if(listURL==null||listURL.size()==0){
			this.listener.onFinish(new HashMap<String,String>());
			return;
		}

		for (int i = 0; i < listURL.size(); i++) {
			System.out.println("url = "+listURL.get(i));
			downloadImage(listURL.get(i));
		}
	}

	public interface DownloadStateListener {
		public void onFinish(Map<String, String> successURL);

		public void onFailed();
	}

	public ExecutorService getThreadPool() {
		if (mImageThreadPool == null) {
			synchronized (ExecutorService.class) {
				if (mImageThreadPool == null) {
					mImageThreadPool = Executors.newFixedThreadPool(8);
				}
			}
		}

		return mImageThreadPool;
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	public Bitmap downloadImage(final String url) {


		getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
//				File file = new File(url);
//				byte[] imgDatas = FileUtil.getBytes(file);
				
				byte[] imgDatas =BitmapUtil.Bitmap2Bytes(BitmapPutUtil.getimage(url)) ;
				
				set_doctor_certificate(imgDatas,url);
			}
		});

		return null;
	}

	private void set_doctor_certificate(final byte[] imageData,String imgPath) {

		try {
//			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
//			params.add(new BasicNameValuePair("userid", SharePrefUtil
//					.getString(Conast.MEMBER_ID)));
//			params.add(new BasicNameValuePair("dir", "14"));
			
			String ext = imgPath.substring(imgPath.lastIndexOf(".")+1, imgPath.length());
			System.out.println( SharePrefUtil
					.getString(Conast.Doctor_ID)+" ext = "+ext);
			params.add(new BasicNameValuePair(extName, ext));
			String json = HttpUtil.uploadFile(HttpUtil.URI
					+ httpUrl, params, imageData);
			json = TextUtil.substring(json, "{");
			
//			{"success":1,"errormsg":"","data":{"URL":"http:\/\/www.ememed.net\/uploads\/question\/20150128\/question_1422436826_OzvL6NYTuF.jpg"}}

			JSONObject object = new JSONObject(json);
			
			String success = object.getString("success");
			if(success.equals("1")){
//				successURL.add(object.getJSONObject("data").getString("URL"));
				successURL.put(imgPath, object.getJSONObject("data").getString("URL"));
				
			}
			
			System.out.println("上传成功json = " + json);
			statDownloadNum();
			HashMap<String, Object> map = new HashMap<String, Object>();
			JSONObject obj = new JSONObject(json);
			map.put("success", obj.getInt("success"));
			map.put("errormsg", obj.getString("errormsg"));
			JSONObject data_obj = obj.getJSONObject("data");
			if (null != data_obj) {
				map.put("cert_url", data_obj.getString("URL"));
			}
		} catch (IOException e) {
			downloadImage(imgPath);
			System.out.println("上传失败IOException");
			e.printStackTrace();
		} catch (Exception e) {
			downloadImage(imgPath);
			System.out.println("上传失败Exception");
			e.printStackTrace();
		}
	}


	public synchronized void cancelTask() {
		if (mImageThreadPool != null) {
			mImageThreadPool.shutdownNow();
			mImageThreadPool = null;
		}
	}

	private void statDownloadNum() {
		synchronized (lock) {
			count++;
			System.out.println("count===>" + count);
			if (count == listURL.size()) {
				System.out.println("download finished total " + count);
				listener.onFinish(successURL);
			}
		}
	}
	
}

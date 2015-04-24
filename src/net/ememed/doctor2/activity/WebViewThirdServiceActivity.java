package net.ememed.doctor2.activity;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.ServiceListEntry;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;


/**
 * 根据WebViewActivity拓展成的第三方Activity
 * @author chenhj
 *
 */
public class WebViewThirdServiceActivity extends BasicActivity implements OnRefreshListener {
	protected static final int MSG_404 = 1;
	private WebView wb;
	private String firstUrl;  //记录第一次传入时的URL
	private String currentUrl = null;
	private String accessKey;
	
	/* 启动WebViewActivity传入的参数，即服务列表 */
	private List<ServiceListEntry> serviceList;
	
	/* 用于存储及更新第三方需要的字段 */
	HashMap<String, String> map = new HashMap<String, String>();
	
	boolean isStop = false; //标志是前进键还是停止键

	private ImageButton forwardOrStopBtn;

	private LinearLayout ll_empty;
	private TextView noticeTextView;
	
	private ProgressBar progressBar;
	
	private String MYTAG = "chenhj,WebViewThirdServiceActivity";
	
	private WebSettings webSettings;

	@Override
	protected void onBeforeCreate(Bundle savedInstanceState) {
		super.onBeforeCreate(savedInstanceState);
		
		firstUrl = getIntent().getStringExtra("url");
		serviceList = (List<ServiceListEntry>) getIntent().getExtras().getSerializable("ServiceList");
		if (getIntent().hasExtra("accesskey")) 
			accessKey = getIntent().getStringExtra("accesskey");
	}

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.webview_third_service);
	}

	@Override
	protected void setupView() {
		
		ll_empty = (LinearLayout) findViewById(R.id.ll_empty);
		noticeTextView = (TextView) findViewById(R.id.tv_notice);
		forwardOrStopBtn = (ImageButton) findViewById(R.id.webview_forward);
		progressBar = (ProgressBar) findViewById(R.id.webview_progress);
		wb = (WebView) findViewById(R.id.wv_oauth);
		WebSettings set = wb.getSettings();
		set.setUserAgentString(set.getUserAgentString()+" ememeddoctor/"+MyApplication.getAppVersion());
		
		ll_empty.setClickable(true);
		ll_empty.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!wb.canGoBack()) {
					wb.loadUrl(currentUrl);
				}
				else {
					wb.reload();
				}
			}
		});
		
		wb.setFocusable(true);
		wb.requestFocus();
		webSettings = wb.getSettings();
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setJavaScriptEnabled(true);
		//自带缩放
		webSettings.setSupportZoom(true);  
		webSettings.setBuiltInZoomControls(true);
		webSettings.setLoadWithOverviewMode(true);
		
		if (!TextUtils.isEmpty(accessKey)) {
			synCookies(WebViewThirdServiceActivity.this, firstUrl, accessKey);
		}else {
			removeSessionCookie(WebViewThirdServiceActivity.this);
		}
		
		//设置webview：跳转、开始加载、加载完成、加载失败的逻辑
		wb.setWebViewClient(new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) { 
				
				try {
				
					Uri uri = Uri.parse(url);
					if(null != uri.getScheme()) {
						String scheme = uri.getScheme();
						if("http".equalsIgnoreCase(scheme)) {
							currentUrl = url;
							view.loadUrl(url);
						}
						else {
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
					showToast("不支持或不符合格式的URI");
				}
				
				return true;
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				progressBar.setVisibility(View.VISIBLE);
				forwardOrStopBtn.setBackgroundResource(R.drawable.webview_cancle);
				isStop = true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				
				if(wb.canGoForward()) {
					forwardOrStopBtn.setBackgroundResource(R.drawable.webview_forward);
				}
				else {
					forwardOrStopBtn.setBackgroundResource(R.drawable.webview_forward_unvalue);
				}
				
				isStop = false;
			}

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
//				Log.d(MYTAG, " onReceivedError ");
				view.stopLoading();
				view.clearView();
				Message msg = handler.obtainMessage();// 发送通知，加入线程
				msg.what = MSG_404;// 通知加载自定义404页面
				handler.sendMessage(msg);// 通知发送！
			}
		});
		
		
		//处理网页中的一些js消息和更新加载进度条
		wb.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}

			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, JsResult result) {

				return super.onJsConfirm(view, url, message, result);
			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, JsPromptResult result) {
				return super.onJsPrompt(view, url, message, defaultValue, result);
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				progressBar.setProgress(newProgress);
				if(newProgress == 100) {
					progressBar.setVisibility(View.GONE);
				}
			}
		});
		
		//初始化，将第三方需要的信息到map中 
		setMessageForThirdService();
		
		wb.addJavascriptInterface(new ObjectForJS() , "EmemedAPI");
		String actuallUrl = getMyFormatUrl(firstUrl);
		
		
		System.out.println("actuallUrl = "+actuallUrl);
		
		currentUrl = actuallUrl;
		
		//去掉WebView自带的缩放按钮但不影响缩放功能
		try {
			int sysVersion = Integer.parseInt(VERSION.SDK);
			if(sysVersion >= 11){
				setZoomControlGoneX(webSettings,new Object[]{false});
			}else{
				setZoomControlGone(wb);
			}
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		
		loadUrl(actuallUrl);
	}
	
	
	/**
	 * 根据需求设置对应字段以及值
	 * 即初始化  HashMap<String, String> map
	 */
	private void setMessageForThirdService() {
		String appVersion = "0.0";
		try {
			PackageInfo packInfo = getPackageManager().getPackageInfo(getPackageName(),0);
	        // getPackageName()是你当前类的包名，0代表是获取版本信息
			appVersion = packInfo.versionName;
			
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		
		//第三要求的修改UA信息，切记修改此处！
		webSettings.setUserAgentString(webSettings.getUserAgentString()+" ememeddoctor/"+appVersion);
		
		/* 输出确保信息正确 */
//		Log.d(MYTAG, "版本号   "+appVersion);
//		Log.d(MYTAG, "应用名称   "+"ememeddoctor");
//		Log.d(MYTAG, "系统号   "+android.os.Build.MODEL + "," + android.os.Build.VERSION.RELEASE);
//		Log.d(MYTAG,"UA : "+webSettings.getUserAgentString());
//		Log.d(MYTAG,"W : "+mDisplayMetrics.widthPixels);
//		Log.d(MYTAG,"H : "+mDisplayMetrics.heightPixels);
//		Log.d(MYTAG,"IMEI : "+PublicUtil.getDeviceUuid(this));
//		Log.d(MYTAG, "REALNAME : "+SharePrefUtil.getString(Conast.Doctor_Name));
		
		try {
			map.put("MEMBERID", URLEncoder.encode(SharePrefUtil.getString(Conast.Doctor_ID), "UTF-8"));
			map.put("MEMBERNAME", URLEncoder.encode(SharePrefUtil.getString("account"), "UTF-8"));
			map.put("UTYPE", URLEncoder.encode("doctor" ,"UTF-8"));
			map.put("REALNAME", URLEncoder.encode(SharePrefUtil.getString(Conast.Doctor_Name) ,"UTF-8"));			
			map.put("CLIENTNAME", URLEncoder.encode("ememeduserofficial" ,"UTF-8"));
			map.put("CLIENTVER", URLEncoder.encode(appVersion ,"UTF-8"));
			map.put("OSTYPE", URLEncoder.encode("Android" ,"UTF-8"));
			map.put("OSVER", URLEncoder.encode(android.os.Build.MODEL + ", Android " + android.os.Build.VERSION.RELEASE 
					,"UTF-8"));
			map.put("UA", URLEncoder.encode(webSettings.getUserAgentString() ,"UTF-8"));
			map.put("SCREENW", URLEncoder.encode(mDisplayMetrics.widthPixels+"" ,"UTF-8"));
			map.put("SCREENH", URLEncoder.encode(mDisplayMetrics.heightPixels+"" ,"UTF-8"));
			map.put("DEVICEID", "");
			map.put("IMEI", URLEncoder.encode(PublicUtil.getDeviceUuid(this) ,"UTF-8"));
			map.put("ACCESSKEY", URLEncoder.encode(SharePrefUtil.getString(Conast.ACCESS_TOKEN), "UTF-8"));
			map.put("REFERER", URLEncoder.encode("" ,"UTF-8"));
			map.put("EXT", "");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据URL请求字段以及本地保存的字段组装URL
	 * @param curUrl 服务器传入的URL
	 * @return 组装好的URL
	 */
	private String getMyFormatUrl(String curUrl) {
		String[] depart = curUrl.split("#");
		String currentKey = "";
		String resultlUrl = "";
		for(int i = 0; i < depart.length; i++) {
			if(i % 2 == 0) {
				resultlUrl += depart[i];
				if(i == 0) {
					currentKey = depart[i].substring(depart[0].indexOf('?')+1, depart[i].length()-1);
				}
				else {
					currentKey = depart[i].substring(1, depart[i].length()-1);
				}
			}
			else {
				resultlUrl += map.get(currentKey);
			}
		}
		return resultlUrl;
	}
	
	/**
	 * 线性查找appkey的下标
	 * @param aimAppKey 查找的AppKey
	 * @return 没找到时返回-1, serviceList为null返回-2
	 */
	private int getTargetAppKeyIndex(String aimAppKey) {
		if(null == serviceList) {
			return -2;
		}
		
		/* 遍历查找，效率有待改进 */
		int index = -1;
		for(int i = 0; i < serviceList.size(); i++) {
			if(aimAppKey.equals(serviceList.get(i).getAPPKEY())) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	
	/**
	 * 设置分享网址，关键是去掉URL中的用户关键字段
	 * @param originUrl 用户所要分享的网址
	 * @return 删除用户关键字段的网址
	 */
	private String getShareUrl(String originUrl) {
		StringBuilder shareUrl = new StringBuilder(originUrl);
		String[] keywords = new String[] {"MEMBERNAME", "REALNAME", "ACCESSKEY"};
		for(int i = 0; i < keywords.length; i++) {
			int startIndex = shareUrl.indexOf(keywords[i]);
			if(startIndex != -1) {
				int endIndex = shareUrl.indexOf("&", startIndex);
				if(endIndex == -1) {
					endIndex = shareUrl.length();
				}
				else {
					endIndex += 1;
				}
				shareUrl.replace(startIndex, endIndex, "");
			}
		}
		return shareUrl.toString();
	}

	public void doClick(View view) {
		
		if(ll_empty.getVisibility() == View.VISIBLE && view.getId() != R.id.ll_webview_share) {
			ll_empty.setVisibility(View.GONE);
			wb.setVisibility(View.VISIBLE);
		}
		
		switch(view.getId()) {
		
		case R.id.ll_webview_back :
			if(wb.canGoBack()) {
				wb.goBack();
			}
			else {
				finish();
			}
			break;
		
		case R.id.ll_webview_forward :
			if(isStop) {
				wb.stopLoading();
				if(wb.canGoForward()) {
					forwardOrStopBtn.setBackgroundResource(R.drawable.webview_forward);
				}
				else {
					forwardOrStopBtn.setBackgroundResource(R.drawable.webview_forward_unvalue);
				}
			}
			else {
				if(wb.canGoForward()) {
					wb.goForward();
				}
			}
			break;
			
		case R.id.ll_webview_share :
			Intent intent=new Intent(Intent.ACTION_SEND);   
            intent.setType("text/plain");   
            intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
            intent.putExtra(Intent.EXTRA_TEXT, "这个链接很不错，来分享一下（来自于薏米网医生版）\n"+getShareUrl(currentUrl));
            
            startActivity(Intent.createChooser(intent, getTitle()));
            break;
		
		case R.id.ll_webview_refresh :
			if(!wb.canGoBack()) {
				//wb.loadUrl(firstUrl);
				wb.loadUrl(getMyFormatUrl(firstUrl));
			}
			else {
				wb.reload();
			}
			break;
			
		case R.id.ll_webview_close :
			finish();
		}
	}

	@Override
	public void onRefreshStarted(View view) {
		// Here we just reload the webview
		wb.setVisibility(View.VISIBLE);
		wb.reload();
	}

	@Override
	protected void onResult(Message msg) {
		super.onResult(msg);
		if(msg.what == MSG_404){
			ll_empty.setVisibility(View.VISIBLE);
			wb.setVisibility(View.GONE);
			noticeTextView.setText("载入失败,点击上方图片重新加载");
		}
	}

	private void loadUrl(String url) {
		if (NetWorkUtils.detect(WebViewThirdServiceActivity.this)) {
			wb.loadUrl(url);
		} else {
			Message msg = handler.obtainMessage();// 发送通知，加入线程
			msg.what = MSG_404;// 通知加载自定义404页面
			handler.sendMessage(msg);// 通知发送！
			Toast.makeText(this, "网络状态异常", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(wb.canGoBack()) {
				wb.goBack();
			}
			else {
				finish();
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * 提供给JavaScript调用的类
	 * @author chenhj
	 *
	 */
	private class ObjectForJS {
		//函数前注解@JavascriptInterface不能去掉，去掉以后API 17或以上版本的手机使用时JS无法找到下列方法，向下兼容，去掉后很可能导致第三方服务不正常
		/**
		 * 用于给第三方无误调用的方法，负责寻找跳转服务并跳转
		 * @param sAppKey 需要在服务项寻找的目标AppKey
		 * @param sRefererURL 第三方传入的用于设置 REFERER的值
		 * @return 如果不能找到匹配的AppKey返回false，能匹配时负责跳转以及返回true，不作跳转容错是为了及时发现问题
		 */
		@JavascriptInterface
		public boolean checkRight(String sAppKey, String sRefererURL) {
			int index = getTargetAppKeyIndex(sAppKey);
			if(index < 0) {
				return false;
			}
			else {
				if(null == serviceList.get(index).getEXT()) {
					return false;
				}
				
				String currentRightUrl = serviceList.get(index).getEXT().getCHECK_RIGHT_URL();
				if(null != currentRightUrl) {
					map.put("REFERER", sRefererURL);
					final String newCurUrl = getMyFormatUrl(currentRightUrl);
					currentUrl = newCurUrl;
					handler.post(new Runnable() {
			            @Override
			            public void run() {
			                wb.loadUrl(newCurUrl);
			            }
			        });
				}
				return true;
			}
		}
	}
	
	//以下两个函数用于去掉WebView自带的缩放按钮但不影响缩放功能
	private void setZoomControlGoneX(WebSettings view, Object[] args) {
        Class classType = view.getClass();

        try {
            Class[] argsClass = new Class[args.length];

            for (int i = 0, j = args.length; i < j; i++) {
                argsClass[i] = args[i].getClass();
            }

            Method[] ms = classType.getMethods();

            for (int i = 0; i < ms.length; i++) {
                if (ms[i].getName().equals("setDisplayZoomControls")) {
                    try {
                        ms[i].invoke(view, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/** 
	 * 设置cookie值 
	 */  
	public void synCookies(Context context, String url, String value) {
	    CookieSyncManager.createInstance(context);  
	    CookieManager cookieManager = CookieManager.getInstance();  
	    cookieManager.setAcceptCookie(true);  
	    cookieManager.removeSessionCookie();
	    cookieManager.setCookie(url, value);
	    CookieSyncManager.getInstance().sync();  
	}
	
	/**
	 * 移除Session Cookie
	 */
	public void removeSessionCookie(Context context) {
		CookieSyncManager.createInstance(context);  
	    CookieManager cookieManager = CookieManager.getInstance();  
	    cookieManager.removeSessionCookie();
	    CookieSyncManager.getInstance().sync();  
	}
	
    //隐藏webview的缩放按钮 适用于3.0以前
    private void setZoomControlGone(View view) {
        Class classType;
        Field field;

        try {
            classType = WebView.class;
            field = classType.getDeclaredField("mZoomButtonsController");
            field.setAccessible(true);

            ZoomButtonsController mZoomButtonsController = new ZoomButtonsController(view);
            mZoomButtonsController.getZoomControls().setVisibility(View.GONE);

            try {
                field.set(view, mZoomButtonsController);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
		
}

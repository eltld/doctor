package net.ememed.doctor2.activity;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tencent.connect.common.Constants;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.InviteUserInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class BusinessCardActivity extends BasicActivity {

	private ScrollView ll_card_content;
	private TextView tv_doctor_name;
	private TextView tv_doctor_professional;
	private TextView tv_doctor_room;
	private ImageView iv_doctor_card;
	private TextView tv_doctor_hosp;
	private MyApplication application;
	private Tencent mTencent;
	private IWXAPI api;
	private String[] title = new String[3];
	private String qr_url = SharePrefUtil.getString("qr_url");
	private boolean isflag = true;
	private Button btn_inviteCode;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_business_card);
		ShareSDK.initSDK(this); // 初始化微信分享
		application = (MyApplication) getApplication();
		application.registerApp();
		api = WXAPIFactory.createWXAPI(this, application.WeChat_ip, false);
		mTencent = Tencent.createInstance(Constants.QQ_APPID, getApplicationContext());
		ShareSDK.initSDK(this);

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		destroyDialog();
	}

	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();

	    View view = getWindow().getDecorView();
	    WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
	    lp.gravity = Gravity.CENTER;
	    getWindowManager().updateViewLayout(view, lp);
	}
	
	@Override
	protected void setupView() {
		ll_card_content = (ScrollView) findViewById(R.id.ll_card_content);
		LayoutParams lps = (LayoutParams) ll_card_content.getLayoutParams();
		int height = MyApplication.height-Util.dip2px(this, 50);
		int width = MyApplication.width-Util.dip2px(this, 50);
		
		if (lps == null) {
			lps = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
		}
		lps.height = LayoutParams.WRAP_CONTENT;
		lps.width = width;
		lps.gravity = Gravity.CENTER;
		int Margin = Util.dip2px(this, 10);
		lps.setMargins(Margin, Margin, Margin, Margin*3);
		ll_card_content.setLayoutParams(lps);
		
		tv_doctor_name = (TextView) findViewById(R.id.tv_doctor_name);
		tv_doctor_professional = (TextView) findViewById(R.id.tv_doctor_professional);
		tv_doctor_room = (TextView) findViewById(R.id.tv_doctor_room);
		tv_doctor_hosp = (TextView) findViewById(R.id.tv_doctor_hosp);
		iv_doctor_card = (ImageView) findViewById(R.id.iv_doctor_card);
		
		tv_doctor_name.setText(SharePrefUtil.getString(Conast.Doctor_Name));
		tv_doctor_professional.setText(SharePrefUtil.getString(Conast.Doctor_Professional));
		tv_doctor_room.setText(SharePrefUtil.getString(Conast.ROOM_NAME));
		tv_doctor_hosp.setText(SharePrefUtil.getString(Conast.HOSPITAL_NAME));
		if(!TextUtils.isEmpty(qr_url)){
			imageLoader.displayImage(qr_url, iv_doctor_card);
			title[0] = SharePrefUtil.getString("title0");
			title[1] = SharePrefUtil.getString("title1");
			title[2] = SharePrefUtil.getString("title2");
			isflag= false;
		}
		btn_inviteCode = (Button) findViewById(R.id.btn_invite_code);
	}
	public void doClick(View v){
		switch (v.getId()) {
		case R.id.ll_share_qq:
			if(TextUtils.isEmpty(title[0])){
				return;
			}
			loading(null);

			ShareParams sp = new ShareParams();
			Platform qq = ShareSDK.getPlatform(QQ.NAME);
			sp.text=title[0];
			
			qq.setPlatformActionListener(new PlatformActionListener() {
				
				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					// TODO Auto-generated method stub
					destroyDialog();
				}
				
				@Override
				public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
					// TODO Auto-generated method stub
					destroyDialog();
				}
				
				@Override
				public void onCancel(Platform arg0, int arg1) {
					// TODO Auto-generated method stub
					destroyDialog();
				}
			});
			qq.share(sp);
			
			break;
		case R.id.ll_share_wechat:
			if(!api.isWXAppInstalled()){
				showToast("请先安装微信客户端");
				return;
			}
			
			if(TextUtils.isEmpty(title[1])){
				showToast("获取分享内容失败");
				return;
			}
			
			// 初始化一个WXTextObject对象
			WXTextObject textObj = new WXTextObject();
			textObj.text = title[1];

			// 用WXTextObject对象初始化一个WXMediaMessage对象
			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = textObj;
			// 发送文本类型的消息时，title字段不起作用
			// msg.title = "Will be ignored";
			msg.description = title[1];

			// 构造一个Req
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneSession;;
			api.sendReq(req);
			
			break;
		case R.id.ll_share_contact:
			if(TextUtils.isEmpty(title[2])){
				return;
			}
			Uri smsToUri = Uri.parse("smsto:");
			Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
			sendIntent.putExtra("sms_body", title[2]);
			sendIntent.setType("vnd.android-dir/mms-sms");
			startActivityForResult(sendIntent, 1002);
			
			break;
			
		case R.id.ll_share_qq_area:
			loading(null);
			ShareParams sp2 = new ShareParams();
			sp2.setTitle("薏米医生APP");
			sp2.setTitleUrl("http://app.ememed.net/"); // 标题的超链接
			sp2.setText("手机问诊、预约名医、免费挂号。薏米认证全国、全球知名医生入驻，您与名医不再遥远！快来下载这款手机APP吧，下载地址：http://app.ememed.net/");
		/*	sp2.setSite("发布分享的网站名称");
			sp2.setSiteUrl("发布分享网站的地址");*/
 
			Platform qzone = ShareSDK.getPlatform (QZone.NAME);
			qzone. setPlatformActionListener (new PlatformActionListener(){

				@Override
				public void onCancel(Platform arg0, int arg1) {
					destroyDialog();
				}

				@Override
				public void onComplete(Platform arg0, int arg1,
						HashMap<String, Object> arg2) {
					destroyDialog();
				}

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					destroyDialog();
				}
				
			}); // 设置分享事件回调
			// 执行图文分享
			qzone.share(sp2);
			break;
		case R.id.ll_share_friends:
		/*	IWXAPI api = WXAPIFactory.createWXAPI(context, APP_ID, false);
		    api.registerApp(APP_ID);*/
			if(!api.isWXAppInstalled()){
				showToast("请先安装微信客户端");
				return;
			}
			
		    WXWebpageObject webpage = new WXWebpageObject();
		    webpage.webpageUrl = "http://app.ememed.net/";
		    WXMediaMessage msg2 = new WXMediaMessage(webpage);
		    msg2.title = "薏米医生APP";
		    msg2.description = "手机问诊、预约名医、免费挂号。薏米认证全国、全球知名医生入驻，您与名医不再遥远！快来下载这款手机APP吧，下载地址：http://app.ememed.net/";
		   /* try
		    {
		      Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.send_img);
		      Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
		      bmp.recycle();
		      msg.setThumbImage(thumbBmp);
		    } 
		    catch (Exception e)
		    {
		      e.printStackTrace();
		    }*/
		    SendMessageToWX.Req req2 = new SendMessageToWX.Req();
		    req2.transaction = String.valueOf(System.currentTimeMillis());
		    req2.message = msg2;
		    req2.scene = SendMessageToWX.Req.WXSceneTimeline;
		    api.sendReq(req2);
			
			/*ShareParams sp4 = new ShareParams();
			sp4.setTitle("薏米医生APP");
			//sp4.setTitleUrl("http://app.ememed.net/"); // 标题的超链接
			sp4.setText("手机问诊、预约名医、免费挂号。薏米认证全国、全球知名医生入驻，您与名医不再遥远！快来下载这款手机APP吧，下载地址：http://app.ememed.net/");
			 //分享类型 文本(具体微信的分享类型有哪些可以查看sample里的 WechatPage类)
            sp4.setShareType(Platform.SHARE_TEXT);
			sp4.setImageUrl(null);
			sp2.setSite("发布分享的网站名称");
			sp2.setSiteUrl("发布分享网站的地址");
 
			Platform weixin = ShareSDK.getPlatform (WechatMoments.NAME);
			weixin.setPlatformActionListener (new PlatformActionListener(){

				@Override
				public void onCancel(Platform arg0, int arg1) {
					net.ememed.doctor2.util.Logger.dout("测试1"+arg0+"---"+arg1);
					destroyDialog();
				}

				@Override
				public void onComplete(Platform arg0, int arg1,
						HashMap<String, Object> arg2) {
					net.ememed.doctor2.util.Logger.dout("测试2"+arg0+"---"+arg1+"---"+arg2);
					destroyDialog();
				}

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					net.ememed.doctor2.util.Logger.dout("测试2"+arg0+"---"+arg1+"---"+arg2);
					destroyDialog();
				}
				
			}); // 设置分享事件回调
			// 执行图文分享
			weixin.share(sp4);*/
			break;
		case R.id.ll_share_sina_weibo:
			if(!checkAPP(this, "com.sina.weibo")){
				showToast("请先安装新浪客户端！");
				return;
			}
			
			loading(null);
			ShareParams sp3 = new ShareParams();
			sp3.setTitle("薏米医生APP");
			sp3.setTitleUrl("http://app.ememed.net/"); // 标题的超链接
			sp3.setText("手机问诊、预约名医、免费挂号。薏米认证全国、全球知名医生入驻，您与名医不再遥远！快来下载这款手机APP吧，下载地址：http://app.ememed.net/");
			 
			Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
			weibo.setPlatformActionListener(new PlatformActionListener(){

				@Override
				public void onCancel(Platform arg0, int arg1) {
					destroyDialog();
				}

				@Override
				public void onComplete(Platform arg0, int arg1,
						HashMap<String, Object> arg2) {
					destroyDialog();
				}

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					destroyDialog();
				}
				
			}); // 设置分享事件回调
			// 执行图文分享
			weibo.share(sp3);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void getData() {
		if (NetWorkUtils.detect(this)) {
			if(isflag)
				loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			// Log.d("chenhj", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.invite_user, InviteUserInfo.class, params, new Response.Listener() {
				@Override
				public void onResponse(Object response) {
					Message message = handler.obtainMessage();
					message.obj = response;
					message.what = IResult.INVITE_INFO;
					handler.sendMessage(message);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Message message = handler.obtainMessage();
					message.obj = error.getMessage();
					message.what = IResult.DATA_ERROR;
					handler.sendMessage(message);
				}
			});

		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}
	
	@Override
	protected void onResult(Message msg) {
		super.onResult(msg);
		destroyDialog();
		switch (msg.what) {
		case IResult.INVITE_INFO:
			InviteUserInfo info = (InviteUserInfo) msg.obj;	
			if(info.getSuccess()==1){
				btn_inviteCode.setText(info.getData().getInviteNumber());
				imageLoader.displayImage(info.getData().getQr_img(), iv_doctor_card);
				int i = 0;
				for (Map.Entry<String, String> entry : info.getData().getInvite_desc().entrySet()) {
					title[i] = entry.getValue(); // 获取分享信息
					SharePrefUtil.putString("title"+i,  entry.getValue());
					i++;
				}
				SharePrefUtil.putString("qr_url", info.getData().getQr_img());
				SharePrefUtil.commit();
			}
			
			break;

		default:
			break;
		}
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	//检查某个应用是否安装
    public static boolean checkAPP(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}

package net.ememed.doctor2.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.open.utils.HttpUtils.HttpStatusException;
import com.tencent.open.utils.HttpUtils.NetworkUnavailableException;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.InviteInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;

public class InviteActivity extends BasicActivity {

	private View convertView;// 弹出窗口的控件需要通过 convertView.findViewByid 来获得示例
	private View bg_gray;
	private PopupWindow popup;
	private LinearLayout invate_root_view;
	private TextView tv_invite_rule;
	private TextView tv_completed1;
	private TextView tv_completed2;
	private String[] title = new String[3];
	private String subtitle;

	private MyApplication application;
	private IWXAPI api;
	private ImageView iv_bg_empty;
	private Tencent mTencent;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_invate);
		TextView top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(getString(R.string.invate_hint));
		ShareSDK.initSDK(this); // 初始化微信分享
		application = (MyApplication) getApplication();
		application.registerApp();
		api = WXAPIFactory.createWXAPI(this, application.WeChat_ip, false);
		mTencent = Tencent.createInstance(Constants.QQ_APPID, getApplicationContext());
	}

	@Override
	protected void setupView() {
		bg_gray = findViewById(R.id.bg_gray);
		invate_root_view = (LinearLayout) findViewById(R.id.invate_root_view);
		tv_invite_rule = (TextView) findViewById(R.id.tv_invite_rule);
		iv_bg_empty = (ImageView) findViewById(R.id.iv_bg_empty);
		tv_completed1 = (TextView) findViewById(R.id.tv_completed1);
		tv_completed2 = (TextView) findViewById(R.id.tv_completed2);

		initSharePopupWindows();

		getInviteInfo();

	}

	public void doClick(View view) {
		int id = view.getId();

		if (id == R.id.btn_back) {
			finish();
		} else if (id == R.id.btn_invate_now) {
			bg_gray.setVisibility(View.VISIBLE);
			popup.showAtLocation(invate_root_view, Gravity.RIGHT|Gravity.BOTTOM, 0, 0);
		}
	}

	@Override
	protected void onResult(Message msg) {
		destroyDialog();
		switch (msg.what) {
		case IResult.INVITE_INFO:
			InviteInfo info = (InviteInfo) msg.obj;
			if (info != null) {
				if (info.getSuccess() == 1) {
					if (info.getData() == null) {
						showToast(IMessage.NET_ERROR);
					}
					if (info.getData() == null) {
						showToast(IMessage.TRANS_ERROR);
						return;
					}
					tv_invite_rule.setText(Html.fromHtml(info.getData().getInvite_rule()));

					imageLoader.displayImage(info.getData().getQr_img(), iv_bg_empty);

					String test1 = "";
					test1 += "本月已成功邀请 <strong>" + info.getData().getMonth_count() + "</strong> 人";
					tv_completed1.setText(Html.fromHtml(test1));
					String test2 = "";
					test2 += "历史成功邀请 <strong>" + info.getData().getHistory_count() + "</strong> 人";
					tv_completed2.setText(Html.fromHtml(test2));
					int i = 0;
					for (Map.Entry<String, String> entry : info.getData().getInvite_desc().entrySet()) {
						title[i] = entry.getValue(); // 获取分享信息
						i++;
					}
				} else {
					showToast(info.getErrormsg());
				}
			}
			break;

		case IResult.NET_ERROR:
			showToast(IMessage.NET_ERROR);
			break;

		case IResult.DATA_ERROR:
			showToast(IMessage.TRANS_ERROR);
			break;

		case IResult.Share_Cancel:
			showToast("分享取消");
			break;
		case IResult.Share_Complete:
			showToast("分享成功");
			break;
		case IResult.Share_Error:
			showToast("分享失败");
			break;

		default:
			break;

		}
	}

	private void initSharePopupWindows() {
		convertView = View.inflate(this, R.layout.layout_invate_popup, null);
		
		//QQ好友
		LinearLayout ll_webview_back = (LinearLayout) convertView.findViewById(R.id.ll_webview_back);
		ll_webview_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String temp = title[0].substring(title[0].indexOf("http"));

				Bundle bundle = new Bundle();
				// 这条分享消息被好友点击后的跳转URL。
				bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, temp);
				// 分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_
				// SUMMARY不能全为空，最少必须有一个是有值的。
				bundle.putString(QQShare.SHARE_TO_QQ_TITLE, title[0]);

				mTencent.shareToQQ(InviteActivity.this, bundle, new IUiListener() {

					@Override
					public void onError(UiError arg0) {
						handler.sendEmptyMessage(IResult.Share_Error);
					}

					@Override
					public void onComplete(Object arg0) {
						handler.sendEmptyMessage(IResult.Share_Complete);
					}

					@Override
					public void onCancel() {
						handler.sendEmptyMessage(IResult.Share_Cancel);
					}
				});

				if (popup != null && popup.isShowing())
					popup.dismiss();

			}
		});
		
		//微信好友
		LinearLayout ll_webview_forward = (LinearLayout) convertView.findViewById(R.id.ll_webview_forward);
		ll_webview_forward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!api.isWXAppInstalled()){
					showToast("请先安装微信客户端");
					return;
				}

				WXWebpageObject webpage0 = new WXWebpageObject();

				WXMediaMessage msg1 = new WXMediaMessage(webpage0);
				Bitmap thumb1 = BitmapFactory.decodeResource(getResources(), R.drawable.wechat_log);

				String temp = title[1].substring(title[1].indexOf("http"));
				webpage0.webpageUrl = temp;
				String title3 = title[1];
				if (!TextUtils.isEmpty(subtitle))
					title3 += ("\n" + subtitle);
				msg1.title = title3;
				msg1.description = subtitle;

				msg1.thumbData = Util.bmpToByteArray(thumb1, true);

				SendMessageToWX.Req req1 = new SendMessageToWX.Req();
				req1.message = msg1;
				req1.scene = SendMessageToWX.Req.WXSceneSession;// 这里设置为好友
				api.sendReq(req1);
				if (popup != null && popup.isShowing())
					popup.dismiss();

			}
		});
		
		//联系人
		LinearLayout contact = (LinearLayout) convertView.findViewById(R.id.contact);
		contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Uri smsToUri = Uri.parse("smsto:");
				Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
				sendIntent.putExtra("sms_body", title[2]);
				sendIntent.setType("vnd.android-dir/mms-sms");
				startActivityForResult(sendIntent, 1002);
				if (popup != null && popup.isShowing())
					popup.dismiss();
			}
		});
		
		//QQ空间
		LinearLayout ll_qzone = (LinearLayout) convertView.findViewById(R.id.ll_qzone);
		ll_qzone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShareParams sp2 = new ShareParams();
				String temp = title[0].substring(title[0].indexOf("http"));
				sp2.setTitle("薏米网");
				sp2.setText(title[0]);
				sp2.setTitleUrl(temp); // 标题的超链接
	 
				Platform qzone = ShareSDK.getPlatform (QZone.NAME);
				qzone. setPlatformActionListener (new PlatformActionListener(){

					@Override
					public void onCancel(Platform arg0, int arg1) {
						handler.sendEmptyMessage(IResult.Share_Error);
					}

					@Override
					public void onComplete(Platform arg0, int arg1,
							HashMap<String, Object> arg2) {
						handler.sendEmptyMessage(IResult.Share_Complete);
					}

					@Override
					public void onError(Platform arg0, int arg1, Throwable arg2) {
						handler.sendEmptyMessage(IResult.Share_Cancel);
					}
					
				}); // 设置分享事件回调
				// 执行图文分享
				qzone.share(sp2);
			}
		});
		
		//微信朋友圈
		LinearLayout ll_weixin_friend = (LinearLayout) convertView.findViewById(R.id.ll_weixin_friend);
		ll_weixin_friend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!api.isWXAppInstalled()){
					showToast("请先安装微信客户端");
					return;
				}
				
			    WXWebpageObject webpage = new WXWebpageObject();
			    
				Bitmap thumb1 = BitmapFactory.decodeResource(getResources(), R.drawable.wechat_log);

				String temp = title[1].substring(title[1].indexOf("http"));
				webpage.webpageUrl = temp;
				String title3 = title[1];
				if (!TextUtils.isEmpty(subtitle))
					title3 += ("\n" + subtitle);
			  
			    WXMediaMessage msg2 = new WXMediaMessage(webpage);
				msg2.title = title3;
				msg2.description = subtitle;
				msg2.thumbData = Util.bmpToByteArray(thumb1, true);
			   
			    SendMessageToWX.Req req2 = new SendMessageToWX.Req();
			    req2.transaction = String.valueOf(System.currentTimeMillis());
			    req2.message = msg2;
			    req2.scene = SendMessageToWX.Req.WXSceneTimeline;
			    api.sendReq(req2);
			}
		});
		
		//新浪微博
		LinearLayout ll_sina_weibo = (LinearLayout) convertView.findViewById(R.id.ll_sina_weibo);
		ll_sina_weibo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!checkAPP(InviteActivity.this, "com.sina.weibo")){
					showToast("请先安装新浪客户端！");
					return;
				}
				
				String temp = title[1].substring(title[1].indexOf("http"));
				String title3 = title[1];
				if (!TextUtils.isEmpty(subtitle))
					title3 += ("\n" + subtitle);
				
				ShareParams sp3 = new ShareParams();
				sp3.setTitle(title3);
				sp3.setTitleUrl(temp); // 标题的超链接
				sp3.setText(subtitle);
				 
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
				
			}
		});
		

		popup = new PopupWindow(convertView, LayoutParams.MATCH_PARENT, Util.dip2px(this, 320));
		popup.setOutsideTouchable(true);
		popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popup.setFocusable(true);
		popup.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				bg_gray.setVisibility(View.GONE);
			}
		});

	}

	private void getInviteInfo() {
		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			// Log.d("chenhj", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_invite_info, InviteInfo.class, params, new Response.Listener() {
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

package net.ememed.doctor2.wxapi;


/*
 * 官网地站:http://www.ShareSDK.cn
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 ShareSDK.cn. All rights reserved.
 */


import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.ShareSdkUtil;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.ShareEnd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/** 微信客户端回调activity示例 */
public class WXEntryActivity extends BasicActivity implements IWXAPIEventHandler {

	private MyApplication application;
	private IWXAPI api;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		api = WXAPIFactory.createWXAPI(this, application.WeChat_ip, false);
		api.registerApp(MyApplication.WeChat_ip);
		api.handleIntent(getIntent(), this);
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		// Logger.dout("3:"+arg0.toString());   760069708
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}
	
	@Override
	public void onResp(BaseResp resp) {
		EventBus.getDefault().post(new ShareEnd());
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			showToast("分享成功");
			Intent intent = new Intent();
			intent.putExtra("share_type", ShareSdkUtil.getShare_to_where());
			intent.putExtra("share_source", ShareSdkUtil.getShare_source());
			intent.putExtra("share_doctor_id", ShareSdkUtil.getShare_doctor_id());
			intent.setAction(MyApplication.WECHAT_SHARE_SUCCESS);
			sendBroadcast(intent);
			Logger.iout("测试", "发送微信分享成功的广播");
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			showToast("取消分享");
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			break;
		default:
			showToast("分享失败");
			break;
		}
		finish();
	}
}

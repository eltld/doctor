package net.ememed.doctor2.receiver;

import java.util.HashMap;
import java.util.Map;


import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.OrderDetailActivity;
import net.ememed.doctor2.activity.WebViewActivity;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.NotifiUitl;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class JpushReceiver extends BroadcastReceiver{
	
	private static final String TAG = "JpushReceiver";
	private static final int a1 = 1;//签约状态（同意、拒绝等）
	private static final int a2 = 2;//医生对用户咨询回复
	private static final int a3 = 3;//户发布的求医信息有人回复
	private static final int a4 = 4;//薏米咨询
	private static final int a5 = 5;//支付信息
	private static final int a6 = 6;//资讯推送
	private static final int a7 = 7;//活动推送
	private static final int a8 = 8;//订单详情
	private static final int a9 = 9;//对话界面
	private static final int a10 = 10;//平台通知


	@Override
	public void onReceive(Context context, Intent intent) {
	
			Bundle bundle = intent.getExtras();
//			Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: "
//					+ printBundle(bundle));

			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
//				Log.d(TAG, "接收Registration Id : " + regId);
				// send the Registration Id to your server...
			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//				Log.d(TAG,"接收到推送下来的自定义消息: "+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
				String TITLE = bundle.getString("cn.jpush.android.TITLE");
				String EXTRA = bundle.getString("cn.jpush.android.EXTRA");
				
				
				JSONObject obj = null;
				try {
					obj = new JSONObject(EXTRA);
					Map<String, String> map = new HashMap<String, String>();
					int type = obj.getInt("TYPE");
					switch (type) {
					case 8:
						map.clear();
						map.put("ORDERTYPE", obj.getString("ORDERTYPE"));
						map.put("ORDERID", obj.getString("ORDERID"));
						NotifiUitl.getInstance().setNotiType(R.drawable.ic_launcher, "薏米网", TITLE, OrderDetailActivity.class, map,Integer.parseInt(obj.getString("TYPE")), true);
						break;
					case 6:
						map.clear();
						map.put("url", HttpUtil.URI+"/normal/html5/newsDetail/"+obj.getString("ID"));
						map.put("title", TITLE);
						NotifiUitl.getInstance().setNotiType(R.drawable.ic_launcher, "薏米网", TITLE, WebViewActivity.class, map,Integer.parseInt(obj.getString("TYPE")), true);

						break;
					default:
						break;
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//				Log.i("life", "接收到推送下来的通知" + printBundle(intent.getExtras()));
				
			}else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//				Log.d(TAG, "用户点击打开了通知");
			}
	}
	
	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

}

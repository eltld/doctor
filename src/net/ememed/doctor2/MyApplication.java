package net.ememed.doctor2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ememed.doctor2.activity.ContactInfoActivity;
import net.ememed.doctor2.cache.Cache;
import net.ememed.doctor2.db.UserDao;
import net.ememed.doctor2.entity.ChatUser;
import net.ememed.doctor2.entity.MessageBackupFieldEntry;
import net.ememed.doctor2.entity.MobileParam;
import net.ememed.doctor2.exception.CrashHandler;
import net.ememed.doctor2.network.HttpService;
import net.ememed.doctor2.network.VolleyHttpClient;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MyApplication extends Application {
	public static MyApplication applicationContext;

	public static VolleyHttpClient volleyHttpClient;
	public static HttpService httpService;
	private static Context context;
	public static String WeChat_ip = "wx78cddabc986e012f";
	public static int height;
	public static int width;
	public static String WECHAT_SHARE_SUCCESS = "wechat_share_success";

	private static Toast toast = null;
	private static Vibrator vibrator;
	public int canvasWidth; // 屏幕宽度
	public int canvasHeight;
	public int density; // 屏幕密度dpi

//	public static String FILEPATH;

	// public static DoctorHXSDKHelper hxSDKHelper = new DoctorHXSDKHelper();
	@Override
	public void onCreate() {
		super.onCreate();

		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		if (processAppName == null || processAppName.equals("")) {
			// workaround for baidu location sdk 3.3
			// 百度定位sdk3.3，定位服务运行在一个单独的进程，每次定位服务启动的时候，都会调用application::onCreate
			// 创建新的进程。
			// 但环信的sdk只需要在主进程中初始化一次。 这个特殊处理是，如果从pid 找不到对应的processInfo
			// processName，
			// 则此application::onCreate 是被service 调用的，直接返回
			return;
		}
//		FILEPATH = getExternalFilesDir("").getAbsolutePath();

//		System.out.println(FILEPATH + "$$$" + getContentResolver().toString());
//		CrashHandler.getInstance().init(this);

		toast = Toast.makeText(MyApplication.this, "", Toast.LENGTH_LONG);
		applicationContext = this;
		Cache.newInstance(getApplicationContext());
		httpService = HttpService.newInstance(getApplicationContext());
		volleyHttpClient = VolleyHttpClient.newInstance(httpService);
		context = this.getApplicationContext();
		JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush
		JPushInterface.setAlias(this, JPushInterface.getUdid(this), new TagAliasCallback() {

			@Override
			public void gotResult(int returnCode, String alias, Set<String> arg2) {
				// System.out.println("push backCode:"+returnCode);
				// System.out.println("push backAlias:"+alias);

				String logs;
				switch (returnCode) {
				case 0:
					// logs = "Set tag and alias success";
					// 建议这里往 SharePreference
					// 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
					break;
				case 6002:
					// logs =
					// "Failed to set alias and tags due to timeout. Try again after 60s.";
					// 延迟 60 秒来调用 Handler 设置别名
					// mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS,
					// alias), 1000 * 60);
					break;
				default:
					// logs = "Failed with errorCode = " + code;
				}

			}

		});
		BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(MyApplication.this);
		builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;// 设置为自动消失
		JPushInterface.setPushNotificationBuilder(1, builder);
		initImageLoader(this);
		// hxSDKHelper.onInit(applicationContext);

		// 初始化环信SDK,一定要先调用init()
		// 初始化环信SDK,一定要先调用init()
		// Log.d("EMChat Demo", "initialize EMChat SDK");
		EMChat.getInstance().init(applicationContext);
		// debugmode设为true后，就能看到sdk打印的log了
		EMChat.getInstance().setDebugMode(true);
		// 获取到EMChatOptions对象
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		// 默认添加好友时，是不需要验证的，改成需要验证
		options.setAcceptInvitationAlways(false);
		// 设置收到消息是否有新消息通知，默认为true
		options.setNotificationEnable(SharePrefUtil
				.getMsgNotifyBoolean(Conast.EM_SETTING_SAVE_MSG_NOTIFY));
		// 设置收到消息是否有声音提示，默认为true
		options.setNoticeBySound(true);
		// 设置收到消息是否震动 默认为true
		options.setNoticedByVibrate(false);
		// 设置语音消息播放是否设置为扬声器播放 默认为true
		options.setUseSpeaker(true);

		// 设置notification消息点击时，跳转的intent为自定义的intent
		options.setOnNotificationClickListener(new OnNotificationClickListener() {

			@Override
			public Intent onNotificationClick(EMMessage message) {

				String extStr;
				MessageBackupFieldEntry entry = null;
				try {
					extStr = message.getStringAttribute("ext");
					Gson gson = new Gson();
					entry = gson.fromJson(extStr, MessageBackupFieldEntry.class);
				} catch (EaseMobException e) {
					e.printStackTrace();
				}
				String user_avatar = "";
				String title = "";
				if (null != entry) {
					if (!TextUtils.isEmpty(entry.getISSYSTEMMSG())
							&& "1".equals(entry.getISSYSTEMMSG())) {
						return null;
					} else {
						user_avatar = entry.getUser_avatar();
						title = entry.getUser_name();
						Intent intent = new Intent(context, ContactInfoActivity.class);
						intent.putExtra("tochat_userId", message.getFrom());
						intent.putExtra("user_avatar", user_avatar);
						intent.putExtra("title", title);
						return intent;
					}
				} else {
					return null;
				}
			}
		});
		// 取消注释，app在后台，有新消息来时，状态栏的消息提示换成自己写的

		options.setNotifyText(new OnMessageNotifyListener() {

			@Override
			public int onSetSmallIcon(EMMessage arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			public String onSetNotificationTitle(EMMessage arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String onNewMessageNotify(EMMessage message) {
				// TODO Auto-generated method stub
				String extStr;
				MessageBackupFieldEntry entry = null;
				try {
					extStr = message.getStringAttribute("ext");
					Gson gson = new Gson();
					entry = gson.fromJson(extStr, MessageBackupFieldEntry.class);
				} catch (EaseMobException e) {
					e.printStackTrace();
				}
				if (null != entry) {
					if (!TextUtils.isEmpty(entry.getISSYSTEMMSG())
							&& "1".equals(entry.getISSYSTEMMSG())) {
						return null;
					} else {
						// 可以根据message的类型提示不同文字，demo简单的覆盖了原来的提示
						return "您有新的消息";
					}
				} else {
					// 可以根据message的类型提示不同文字，demo简单的覆盖了原来的提示
					return "您有新的消息";
				}
			}

			public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
				// return fromUsersNum + "个用户，发来了" + messageNum + "条消息";
				return "您有新的消息";
			}
		});

		// 获取屏幕尺寸
		WindowManager wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
		canvasWidth = metric.widthPixels;
		canvasHeight = metric.heightPixels;
		density = metric.densityDpi;

		// options.setNotifyText(new OnMessageNotifyListener() {
		//
		// @Override
		// public String onNewMessageNotify(EMMessage message) {
		// String extStr;
		// MessageBackupFieldEntry entry = null;
		// try {
		// extStr = message.getStringAttribute("ext");
		// Gson gson = new Gson();
		// entry = gson.fromJson(extStr,MessageBackupFieldEntry.class);
		// } catch (EaseMobException e) {
		// e.printStackTrace();
		// }
		// if (null != entry) {
		// if (!TextUtils.isEmpty(entry.getISSYSTEMMSG()) &&
		// "1".equals(entry.getISSYSTEMMSG())) {
		// return null;
		// } else {
		// //可以根据message的类型提示不同文字，demo简单的覆盖了原来的提示
		// return "您有新的消息";
		// }
		// } else {
		// //可以根据message的类型提示不同文字，demo简单的覆盖了原来的提示
		// return "您有新的消息";
		// }
		// }
		//
		// @Override
		// public String onLatestMessageNotify(EMMessage message, int
		// fromUsersNum, int messageNum) {
		// // return fromUsersNum + "个用户，发来了" + messageNum + "条消息";
		// return "您有新的消息";
		// }
		//
		// @Override
		// public String onSetNotificationTitle(EMMessage arg0) {
		// // TODO Auto-generated method stub
		// return null;
		// }
		// });

		// 设置一个connectionlistener监听账户重复登录
		// EMChatManager.getInstance().addConnectionListener(
		// new MyConnectionListener());
	}

	public void registerApp() {
		final IWXAPI api = WXAPIFactory.createWXAPI(this, WeChat_ip, false);
		api.registerApp(WeChat_ip);
	}

	public static Context getContext() {
		return context;
	}

	public static MyApplication getInstance() {
		return applicationContext;
	}

	public static void initImageLoader(Context context) {
//		int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
//
//		MemoryCacheAware<String, Bitmap> memoryCache;
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//			memoryCache = new LruMemoryCache(memoryCacheSize);
//		} else {
//			memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
//		}
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		//配全局内存缓存，磁盘缓存。 
		DisplayImageOptions options = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true)
		 	.cacheInMemory(true)
		 	.cacheOnDisk(true)
		 	.build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // Not
				.diskCacheSize(50 * 1024 * 1024) // 50 Mb
//				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
					.next());
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName,
							PackageManager.GET_META_DATA));
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
				// Log.d("Process", "Error>> :"+ e.toString());
			}
		}
		return processName;
	}

	private Map<String, ChatUser> contactList;

	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, ChatUser> getContactList() {
		if (SharePrefUtil.getString(Conast.Doctor_ID) != null && contactList == null) {
			UserDao dao = new UserDao(getContext());
			// 获取本地好友user list到内存,方便以后获取好友list
			contactList = dao.getContactList();
		}
		return contactList;
	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, ChatUser> contactList) {
		this.contactList = contactList;
	}

	private static final String TAG = MyApplication.class.getSimpleName();

	// class MyConnectionListener implements ConnectionListener {
	//
	// @Override
	// public void onReConnecting() {
	// }
	//
	// @Override
	// public void onReConnected() {
	// }
	//
	// @Override
	// public void onDisConnected(String errorString) {
	// if (errorString != null && errorString.contains("conflict")) {
	// Logger.dout(TAG
	// + " EMChatManager MyConnectionListener  onDisConnected 帐号在其他设备登录");
	// } else {
	// Logger.dout(TAG
	// + " EMChatManager MyConnectionListener  onDisConnected 帐号连接失败");
	// }
	// }
	//
	// @Override
	// public void onConnecting(String progress) {
	// Logger.dout(TAG
	// + " EMChatManager MyConnectionListener  onReConnected 重新连接上");
	// }
	//
	// @Override
	// public void onConnected() {
	// Logger.dout(TAG
	// + " EMChatManager MyConnectionListener  onReConnecting 重新连接中...");
	// }
	// }

	public final Handler handlerToast = new Handler() {
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {

				case 2:
					if (msg.obj instanceof String)
						toast.setText(String.valueOf(msg.obj));
					else
						toast.setText(Integer.valueOf("" + msg.obj));
					toast.show();
					vibrate();
					break;
				default:
					if (msg.obj instanceof String)
						toast.setText(String.valueOf(msg.obj));
					else
						toast.setText(Integer.valueOf("" + msg.obj));
					toast.show();
					break;
				}
			} catch (NumberFormatException e) {
				Log.e("MyApp", e.getMessage(), e);
			}
		};
	};

	/**
	 * 震动 50 毫秒
	 */
	public static void vibrate() {
		vibrate(50);
	}

	/**
	 * 震动
	 * 
	 * @param milliseconds
	 */
	public static void vibrate(long milliseconds) {
		if (vibrator == null)
			vibrator = (Vibrator) getInstance().getSystemService(Service.VIBRATOR_SERVICE);
		vibrator.vibrate(milliseconds);
	}

	/**
	 * toast信息提示(long)
	 * 
	 * @param msg
	 */
	public static void toastMakeTextLong(String msg) {
		applicationContext.handlerToast.sendMessage(applicationContext.handlerToast.obtainMessage(
				2, msg));
	}

	/**
	 * 获取设备信息
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String getMobileParam(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		MobileParam param = new MobileParam();
		Build bd = new Build();
		param.setMobile_model(bd.BRAND); // 机器型号（xiaomi）
		param.setMobile_version(bd.MODEL); // 机器版本（MI 3）
		param.setMobile_alias(bd.HOST); // 别名
		param.setMobile_sysversion(Build.VERSION.RELEASE);

		int type = telephonyManager.getNetworkType();
		String networkType;
		if (type == TelephonyManager.NETWORK_TYPE_GPRS) {
			networkType = "GPRS";
		} else if (type == TelephonyManager.NETWORK_TYPE_EDGE) {
			networkType = "EDGE";
		} else if (type == TelephonyManager.NETWORK_TYPE_UMTS) {
			networkType = "UMTS";
		} else if (type == TelephonyManager.NETWORK_TYPE_CDMA) {
			networkType = "CDMA";
		} else if (type == TelephonyManager.NETWORK_TYPE_EVDO_0) {
			networkType = "EVDO_0";
		} else if (type == TelephonyManager.NETWORK_TYPE_EVDO_A) {
			networkType = "EVDO_A";
		} else if (type == TelephonyManager.NETWORK_TYPE_1xRTT) {
			networkType = "1xRTT";
		} else if (type == TelephonyManager.NETWORK_TYPE_HSDPA) {
			networkType = "HSDPA";
		} else if (type == TelephonyManager.NETWORK_TYPE_HSUPA) {
			networkType = "HSUPA";
		} else if (type == TelephonyManager.NETWORK_TYPE_HSPA) {
			networkType = "HSPA";
		} else {
			networkType = "未知网络";
		}

		param.setMobile_network(networkType); // 网络制式 GPRS
		param.setMobile_operator(telephonyManager.getNetworkOperatorName()); // CHINA BOBILE

		Gson gson = new Gson();
		String strJson = gson.toJson(param);
		return strJson;
	}

	public static String getAppVersion() {
		String appVersion = "0.0";
		try {
			PackageInfo packInfo = getInstance().getPackageManager().getPackageInfo(
					getInstance().getPackageName(), 0);
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			appVersion = packInfo.versionName;

		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			return appVersion;
		}
	}

	/**
	 * 判断审核通过时间是否在三天以内
	 */
	public static boolean judgeAuditTimeIsInThreeDays(String auditTime) {
		// 没有时间数据处理为三天内
		if (TextUtils.isEmpty(auditTime)) {
			return true;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d;
		long auditTimeLong;
		try {
			d = sdf.parse(auditTime);
			auditTimeLong = d.getTime();
		} catch (Exception e) {
			return false;
		}

		Long cur_time = System.currentTimeMillis();

		if (cur_time - auditTimeLong < 3 * 24 * 60 * 60 * 1000) {
			return true;
		} else {
			return false;
		}
	}

}

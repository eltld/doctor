package net.ememed.doctor2.util;

import java.lang.ref.WeakReference;
import net.ememed.doctor2.R;
import android.content.Context;

/**
 * 全局类
 * @author chen
 * 
 */
public class AppContext {

	private static AppContext instance = new AppContext();

	public static AppContext getInstance() {
		if (instance == null) {
			instance = new AppContext();
		}
		return instance;
	}

	/**
	 * 获取 系统上下文
	 */
	private WeakReference<Context> context;

	/**
	 * 获取 系统上下文
	 * 
	 * @return
	 */
	public static Context getContext() {
		if (getInstance().context == null) {
			return null;
		}
		return getInstance().context.get();
	}

	/**
	 * 设置 系统上下文
	 * 
	 * @return
	 */
	public static void setContext(Context contextx) {
		getInstance().context = null;
		getInstance().context = new WeakReference<Context>(contextx);
	}

	public static int language;
	/**
	 * 1 CN
	 * 0 en
	 * @param language
	 */
	public static void setLanguage(int language) {
		AppContext.language = language;
	}
	
	
	public static final String Preferences_userinfo = "Preferences_userinfo";
	
	public boolean isActive = false;
	
	private int login_uid;
	private String login_token;
	private String deviceId;
	private int currentItem;

	public int getLogin_uid() {
		return login_uid;
	}

	public void setLogin_uid(int login_uid) {
		this.login_uid = login_uid;
	}

	public String getLogin_token() {
		return login_token;
	}

	public void setLogin_token(String login_token) {
		this.login_token = login_token;
	}
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(int currentItem) {
		this.currentItem = currentItem;
	}

	}

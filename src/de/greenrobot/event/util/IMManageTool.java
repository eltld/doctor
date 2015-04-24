package de.greenrobot.event.util;

import net.ememed.doctor2.interfac.EMLoginCallBack;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;

import android.content.Context;

public class IMManageTool {

	public static IMManageTool tool;
	Context context;

	private EMLoginCallBack callBack;

	public IMManageTool(Context context) {
		this.context = context;
	}

	public static IMManageTool getInstance(Context context) {
		if (tool == null) {
			tool = new IMManageTool(context);
		}
		return tool;
	}

	public void setEMLoginCallBack(EMLoginCallBack callBack) {
		this.callBack = callBack;
	}

	/**
	 * 登入环信
	 * 
	 * @param username
	 * @param pw
	 */
	public void loginIM(String doctor_ID, String pw) {
		EMChatManager.getInstance().login(doctor_ID, pw, new EMCallBack() {
			public void onSuccess() {
				System.out.println("环信登入成功..........");
				if(callBack!=null)
				callBack.onSuccess();
			}

			public void onProgress(int arg0, String arg1) {
				if(callBack!=null)
				callBack.onProgress(arg0, arg1);
			}

			public void onError(int arg0, String arg1) {
				System.out.println("环信登入失败.........." + arg1);
				if(callBack!=null)
				callBack.onError(arg0, arg1);
			}
		});
	}

	/**
	 * 退出环信登入
	 */
	public void logoutIM() {
		EMChatManager.getInstance().logout();
	}

}

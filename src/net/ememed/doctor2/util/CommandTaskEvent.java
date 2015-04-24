package net.ememed.doctor2.util;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import android.app.Service;
import android.content.DialogInterface;

/**
 * 异步线程 用于后台加载数据
 * @author chen
 *
 */
public final class CommandTaskEvent extends CommandTask<Object, Object, Object> {

	BasicUIEvent uiEvent;
	BasicActivity context;
	String tips;
	public CommandTaskEvent(BasicUIEvent basicUIEvent, BasicActivity context, String tips) {
		uiEvent = basicUIEvent;
		this.context = context;
		this.tips = tips;
	}	
	
	@Override
	public Object doInBackground(Object... params) {
		uiEvent.execute(Integer.parseInt(params[0].toString()),params[1]);
		return null;
	}

	Service service;
	public CommandTaskEvent(BasicUIEvent basicUIEvent,Service service) {
		uiEvent = basicUIEvent;
		this.service=service; 
	}
	
	public void onPreExecute() {
		super.onPreExecute();
		if (tips != null && context != null) {
			context.loading(tips);
			context.dialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						public void onCancel(DialogInterface dialog) {
							// 取消事件监听， 同时取消现场访问
							if (!isCancelled()) {
								cancel(true);
							}
						}
					});
		}
	}
	
	@Override
	public void onPostExecute(Object result) {
		super.onPostExecute(result);
		if (tips != null) {
			if (context != null && !context.isFinishing()) {
				context.destroyDialog();
			}
		}
	}
}

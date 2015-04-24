package net.ememed.doctor2.util;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import android.app.Service;
import android.content.Intent;

/**
 * 
 * @author chen
 *
 */
public class UICore {

	private static UICore ins = new UICore();
//	private PushService service;
	
	private UICore() {
		
	}

	public static UICore getInstance() {
		if (ins == null) {
			ins = new UICore();
		}
		return ins;
	}
	/***
	 * 
	 * @param basicUIEvent
	 * @param context
	 * @param CommandID
	 * @param tips  为null  则不显示进度框
	 * @param obj
	 */
	public static void eventTask(BasicUIEvent basicUIEvent,
			BasicActivity context, int CommandID, String tips, Object obj) {
		try {
			CommandTaskEvent commandTask = new CommandTaskEvent(basicUIEvent, context, tips);
			commandTask.execute((new Object[] { CommandID + "", obj }));
		} catch (Exception e) {
			e.printStackTrace();
			Logger.dout("cancel:  success!!");
			if (context != null)
				context.destroyDialog();
		}
	}

	public static void eventTask(BasicUIEvent basicUIEvent, Service service, int CommandID, String tips, Object obj) {
		try {
			CommandTaskEvent commandTask = new CommandTaskEvent(basicUIEvent, service);
			commandTask.execute((new Object[] { CommandID + "", obj }));
		} catch (Exception e) {
			e.printStackTrace();
			Logger.dout("cancel:  success!!");
		}
	}
	
//	public static void setService(PushService service) {
//		UICore.getInstance().service = service;
//	}
//	
//	public static PushService getService() {
//		return UICore.getInstance().service;
//	}
//	
//	public void closeService() {
//		if (service != null) {
//			service.stopService(new Intent(service, PushService.class));
//			UICore.setService(null);
//			service = null;
//		}
//	}
}

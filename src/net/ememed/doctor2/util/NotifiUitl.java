package net.ememed.doctor2.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;


public class NotifiUitl {
	
	private static NotifiUitl Notifi= new NotifiUitl();
	
	
	private NotifiUitl() {
		super();
	}
	MyApplication context = MyApplication.getInstance();
	public static NotifiUitl getInstance() {
		return Notifi;
	}
	int type =1;
	private static long l =0;
	public synchronized void setNotiType(int iconId, String contentTitle, String contentText, Class activity, Map<String,String> map,int type,boolean OpenMusic) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		Notification notif = new Notification();
		
		long temp=0;
		boolean flag=false;
		synchronized (this) {
		if (OpenMusic){
				if(l==0){
					l=System.currentTimeMillis();
					flag = true;
				}else{
					temp = System.currentTimeMillis();
					flag = (temp - l) > 1000;
//					Log.i("LicaishiText", "temp:"+temp+"");
					if(flag)
						l=0;
					
				}
//				Log.i("LicaishiText", l+"");
//				Log.i("LicaishiText", "temp1:"+temp+"");
				
				if(flag){
					notif.defaults = Notification.DEFAULT_SOUND;
				}
			}
		}

		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		notif.icon = iconId;
		notif.tickerText = contentText;
		long[] ss = {1000,500,1000};  
		notif.vibrate = ss;
		Intent nIntent = new Intent(context, activity);
		nIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);	
		
		Set<String> keySet = map.keySet();
		for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			nIntent.putExtra(key, map.get(key));
		}
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, R.drawable.ic_launcher, nIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notif.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(this.type, notif);
		type++;
	}
}

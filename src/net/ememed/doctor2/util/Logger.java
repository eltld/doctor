package net.ememed.doctor2.util;

import android.util.Log;
import net.ememed.doctor2.R;

public class Logger {
	
	private static String appName = "ememed";
	
	public static boolean debugFlag = false;
	
	public static void dout(String str) {
		if (debugFlag) {
			Log.d(appName, "Line:"+getLineNumber()+" : str>>>>>>>>>" + str);
		}
	}

	public static void dout(Class context, String str2) {
		if (debugFlag) {
			Log.d(context.getSimpleName(), "Line:"+getLineNumber()+" str:>>>>>>>>>>>>>" + str2);
		}
	}
	
	public static void iout(String str1, String str2) {
		if (debugFlag) {
			Log.i(str1, "Line:"+getLineNumber()+" str:>>>>>>>>>>>>>" + str2);
		}
	}
	
	private static int getLineNumber() {  
        return Thread.currentThread().getStackTrace()[5].getLineNumber();  
    }  
	
}

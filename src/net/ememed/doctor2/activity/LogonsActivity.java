package net.ememed.doctor2.activity;

import net.ememed.doctor2.R;
import net.ememed.doctor2.util.SharePrefUtil;

import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.RegisterSuccessEvent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/**全科，专科*/
public class LogonsActivity extends BasicActivity {
	private TextView top_title;
	private static final int REQUEST_REGISTER = 1;
	// private int freeConsult;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.u_register);
		EventBus.getDefault().registerSticky(this, RegisterSuccessEvent.class);
	}

	@Override
	protected void setupView() {
		super.setupView();
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("注册账号");
		//隐藏软键盘
		try {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					InputMethodManager localInputMethodManager = (InputMethodManager) getSystemService("input_method");
					IBinder localIBinder = getWindow().peekDecorView().getWindowToken();
					localInputMethodManager.hideSoftInputFromWindow(localIBinder, 0);	
				}
			}, 200L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void doClick(View view) {
		if (view.getId() == R.id.btn_zhuanke_register) {
			SharePrefUtil.putInt("freeConsult", 0);
			SharePrefUtil.commit();
			Intent intent = new Intent(this, RegisterByMobileActivity.class);
			startActivityForResult(intent, REQUEST_REGISTER);
		} else if (view.getId() == R.id.btn_quanke_register) {
			SharePrefUtil.putInt("freeConsult", 1);
			SharePrefUtil.commit();
			Intent intent = new Intent(this, RegisterByMobileActivity.class);
			startActivityForResult(intent, REQUEST_REGISTER);
		} else if (view.getId() == R.id.btn_back) {
			finish();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

   public void onEvent(RegisterSuccessEvent testEvent){
       finish();
   }
	@Override
	protected void onStop() {
		super.onStop();
		EventBus.getDefault().removeStickyEvent(RegisterSuccessEvent.class);
	}
}

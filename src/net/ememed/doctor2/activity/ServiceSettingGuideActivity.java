package net.ememed.doctor2.activity;

import net.ememed.doctor2.R;
import net.ememed.doctor2.util.SharePrefUtil;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import com.umeng.analytics.MobclickAgent;

public class ServiceSettingGuideActivity extends BasicActivity {
	private CheckBox service_setting_checkbox;
	private Button bt_ok;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		Window window = getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		wl.alpha = 0.88f;//设置透明度,0.0为完全透明，1.0为完全不透明
		window.setAttributes(wl);
		
		setContentView(R.layout.service_setting_guide);
	}

	@Override
	protected void setupView() {
		super.setupView();
		service_setting_checkbox = (CheckBox) findViewById(R.id.service_setting_checkbox);
		bt_ok = (Button) findViewById(R.id.bt_ok);
//		saveSetting();
	}

	private void saveSetting() {
		boolean saveSetting = service_setting_checkbox.isChecked();

		if (saveSetting) {
			SharePrefUtil.putBoolean("service_setting_checkbox", true);
		} else {
			SharePrefUtil.putBoolean("service_setting_checkbox", false);
		}
		SharePrefUtil.commit();
	}

	public void doClick(View view) {
		if (view.getId() == R.id.bt_ok) {
			saveSetting();
//			Intent intent = new Intent(this,ServicePackageSettingActivity.class);
//			startActivity(intent);
			finish();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
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
}

package net.ememed.doctor2.activity;

import com.umeng.analytics.MobclickAgent;

import net.ememed.doctor2.R;
import android.os.Bundle;

public class MoneyApplyActivity extends BasicActivity {

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.about_us);
	}

	@Override
	protected void setupView() {
		super.setupView();
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

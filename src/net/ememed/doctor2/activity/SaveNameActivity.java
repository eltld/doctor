package net.ememed.doctor2.activity;

import net.ememed.doctor2.R;
import net.ememed.doctor2.util.SharePrefUtil;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SaveNameActivity extends BasicActivity {
	private EditText et_public_str;
	private Button btn_addhealth;
	private TextView top_title;
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_save_name);
	}

	@Override
	protected void setupView() {
		btn_addhealth = (Button) findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setBackgroundDrawable(null);
		btn_addhealth.setText(getString(R.string.bt_save));
		super.setupView();
		et_public_str = (EditText) findViewById(R.id.et_public_str);
		top_title = (TextView)findViewById(R.id.top_title);
		top_title.setText(getString(R.string.set_name));
	}

	public void doClick(View view) {

		int id = view.getId();
		if (id == R.id.btn_back) {
			finish();
		} else if (id == R.id.btn_addhealth) {
			String nameStr = et_public_str.getText().toString();
			Intent intent = new Intent(SaveNameActivity.this,
					PersonInfoActivity.class);
			intent.putExtra("nameStr", nameStr);
			intent.putExtra("setting_type",
					SaveNameActivity.class.getSimpleName());
			setResult(RESULT_OK, intent);
			finish();
		} else {
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
}

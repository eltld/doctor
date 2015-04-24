package net.ememed.doctor2.activity;

import net.ememed.doctor2.R;
import net.ememed.doctor2.R.layout;
import net.ememed.doctor2.entity.OrderInformation;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 正在审核界面
 * @author Administrator
 *
 */
public class VerifingActivity extends BasicActivity{

	/*private TextView top_title; 
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_veryfing);
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("资料审核");
	}
	
	public void doClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_add_information:
			intent = new Intent(VerifingActivity.this, RegisterSuccessActivity.class);
			intent.putExtra("verify_type", 2);
			startActivity(intent);
			break;
		case R.id.btn_close:
			finish();
			break;
		case R.id.btn_back:
			finish();
			break;
		default:
			break;
		}
	}*/
}

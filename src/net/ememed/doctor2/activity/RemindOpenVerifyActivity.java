package net.ememed.doctor2.activity;

import net.ememed.doctor2.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RemindOpenVerifyActivity extends BasicActivity{
	
	private TextView top_title;
	private ImageView btn_back;
		
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateContent(savedInstanceState);
		
		setContentView(R.layout.remind_open_verify);
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("验证医生身份");
		
		btn_back = (ImageView) findViewById(R.id.btn_back);
	}
	
	public void doClick(View view){
		Intent intent = null;
		if(view.getId() == R.id.button1){
			intent = new Intent(RemindOpenVerifyActivity.this, RegisterSuccessActivity.class);
			//intent.putExtra("need_load_info", false);
			startActivity(intent);
			finish();
		} else if(view.getId() == R.id.button2){
			finish();
		} else if(view.getId() == R.id.btn_back){
			finish();
		}
	}
}

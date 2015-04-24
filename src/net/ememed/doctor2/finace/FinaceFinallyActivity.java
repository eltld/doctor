package net.ememed.doctor2.finace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;

public class FinaceFinallyActivity extends BasicActivity {
	private TextView top_title;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_finace_finally);
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("完成");
	}
	
	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			finish();
		} else if (view.getId() == R.id.btn_money){
			
			Intent intent = new Intent(FinaceFinallyActivity.this, FinaceDetails2Activity.class); 
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("from", FinaceFinallyActivity.class.getSimpleName());
			startActivity(intent);
			//Utils.startActivity(FinaceFinallyActivity.this, FinaceDetails2Activity.class);
			finish();
		}
	}
}

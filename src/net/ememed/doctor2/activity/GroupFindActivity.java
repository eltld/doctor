package net.ememed.doctor2.activity;

import net.ememed.doctor2.R;
import net.ememed.doctor2.widget.RefreshListView;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GroupFindActivity extends BasicActivity {

	private RefreshListView lv_contact;
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_group_find);
	}
	@Override
	protected void setupView() {
		TextView top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("查找群");
		lv_contact = (RefreshListView) findViewById(R.id.lv_contact_class);
		
	}
	public void doClick(View v){
		switch (v.getId()) {
		case R.id.btn_back:
			onBackClick();
			break;
		case R.id.ibtn_find_group:
			
			break;

		default:
			break;
		}
	}
}

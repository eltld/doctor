package net.ememed.doctor2.activity;

import java.io.Serializable;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.GroupSucceeEvent;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.CreateGroupEntry;
import net.ememed.doctor2.entity.GroupListEntry;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GroupDetailsActivity extends BasicActivity {

	private Button btn_addhealth;
	private String title;
	private GroupListEntry item;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_group_succee);
		item = (GroupListEntry) getIntent().getSerializableExtra("item");
		
	}
	
	@Override
	protected void setupView() {
		TextView top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(title);
//		btn_addhealth = (Button) findViewById(R.id.btn_addhealth);
//		btn_addhealth.setVisibility(View.VISIBLE);
//		btn_addhealth.setText("下一步");
		
		
	}
	
	public void doClick(View v){
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;

		default:
			break;
		}
	}

}

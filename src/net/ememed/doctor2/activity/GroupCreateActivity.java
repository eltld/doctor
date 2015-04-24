package net.ememed.doctor2.activity;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.GroupSucceeEvent;
import net.ememed.doctor2.R;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupCreateActivity extends BasicActivity {

	
	private Button btn_addhealth;
	private EditText et_group_name;
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		EventBus.getDefault().register(this,GroupSucceeEvent.class);
		
	}
	@Override
	protected void setupView() {
		TextView top_title = (TextView) findViewById(R.id.top_title);
//		ImageView btn_back = (ImageView) findViewById(R.id.btn_back);
		top_title.setText("填写群名称");
		btn_addhealth = (Button) findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setText("下一步");
		
		et_group_name = (EditText) findViewById(R.id.et_group_name);
		
	}
	/**
	 * 创建群组后调用
	 * @param event
	 */
	public void onEvent(GroupSucceeEvent event) {
		finish();
	}
	public void doClick(View v){
		switch (v.getId()) {
		case R.id.btn_addhealth:
			 GroupCreate();
			break;
		case R.id.btn_back:
			onBackClick();
			break;

		default:
			break;
		}
	}
	/**
	 * 判断群组名，
	 * 进去创建群组下一步
	 */
	private void GroupCreate() {
		// TODO Auto-generated method stub
		String GroupName = et_group_name.getText().toString().trim();
		if(TextUtils.isEmpty(GroupName)){
			Toast.makeText(getApplicationContext(), "请输入群名称", 0).show();
			return;
		}
		Bundle bundle = new Bundle();
		bundle.putString("group_name", GroupName);
		Intent i = new Intent(this,GroupLogActivity.class);
		i.putExtras(bundle);
		startActivity(i);
	}
}

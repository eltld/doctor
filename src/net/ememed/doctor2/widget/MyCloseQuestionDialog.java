package net.ememed.doctor2.widget;

import net.ememed.doctor2.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyCloseQuestionDialog extends Dialog implements android.view.View.OnClickListener{

	
	private EditText question_edit;
	private TextView question_pool_result1;
	private TextView question_pool_result2;
	private TextView question_pool_result3;
	private TextView question_pool_result4;
	private LinearLayout choice_layout;
	private LinearLayout template_layout;
	private Button Cancel_bnt;
	private Button ok_bnt;
	
	
	public MyCloseQuestionDialog(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
	}
	public MyCloseQuestionDialog(Context context,int theme) {
		// TODO Auto-generated constructor stub
		super(context,theme);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_pool_result);
		question_edit = (EditText) findViewById(R.id.question_edit);
		question_pool_result1 = (TextView) findViewById(R.id.question_pool_result1);
		question_pool_result2 = (TextView) findViewById(R.id.question_pool_result2);
		question_pool_result3 = (TextView) findViewById(R.id.question_pool_result3);
		question_pool_result4 = (TextView) findViewById(R.id.question_pool_result4);
		choice_layout = (LinearLayout) findViewById(R.id.choice_layout);
		template_layout = (LinearLayout) findViewById(R.id.template_layout);
		Cancel_bnt = (Button) findViewById(R.id.Cancel_bnt);
		ok_bnt = (Button) findViewById(R.id.ok_bnt);
		Cancel_bnt.setOnClickListener(this);
		
		question_pool_result1.setOnClickListener(this);
		question_pool_result2.setOnClickListener(this);
		question_pool_result3.setOnClickListener(this);
		question_pool_result4.setOnClickListener(this);
//		template_layout.setOnClickListener(this);
		choice_layout.setOnClickListener(this);
	}
	
	
	public void setButtonOnClickListener(android.view.View.OnClickListener listener){
		ok_bnt.setOnClickListener(listener);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.question_pool_result1:
			question_edit.setText("初步诊断无大碍");
			template_layout.setVisibility(View.GONE);
			question_edit.setSelection(question_edit.getText().toString().trim().length());
			break;
		case R.id.question_pool_result2:
			question_edit.setText("建议药物治疗");
			template_layout.setVisibility(View.GONE);
			question_edit.setSelection(question_edit.getText().toString().trim().length());
			break;
		case R.id.question_pool_result3:
			question_edit.setText("建议立即就医");
			template_layout.setVisibility(View.GONE);
			question_edit.setSelection(question_edit.getText().toString().trim().length());
			break;
		case R.id.question_pool_result4:
			question_edit.setText("建议手术治疗");
			template_layout.setVisibility(View.GONE);
			question_edit.setSelection(question_edit.getText().toString().trim().length());
			break;
		case R.id.Cancel_bnt:
			dismiss();
			break;
		case R.id.choice_layout:
			if(template_layout.getVisibility()==View.GONE){
				template_layout.setVisibility(View.VISIBLE);
			}else{
				template_layout.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}
	}
	public String getText(){
		return question_edit.getText().toString().trim();
	}
}

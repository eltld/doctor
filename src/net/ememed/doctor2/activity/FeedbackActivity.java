package net.ememed.doctor2.activity;

import java.util.HashMap;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.FeedbackResultInfo;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FeedbackActivity extends BasicActivity{
	
	private TextView title;
	private EditText feedbackEditText;
	private String feedbackContent;
	private Button btn_addhealth;
	private boolean isSend = false;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.layout_feedback);
	}

	@Override
	protected void setupView() {
		super.setupView();
		btn_addhealth = (Button) findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setBackgroundDrawable(null);
		btn_addhealth.setText(getString(R.string.button_send));
		title = (TextView) findViewById(R.id.top_title);
		title.setText("意见反馈");
		feedbackEditText = (EditText) findViewById(R.id.feedback_message);
	}
	
	@Override
	protected void onResult(Message msg) {
		super.onResult(msg);
		switch(msg.what) {
		
		case IResult.NET_ERROR :
			this.showToast(IMessage.NET_ERROR);
			operationsAfterReceive();
			break;
		}
	}
	
	
	public void doClick(View v) {
		int id = v.getId();
		if(id == R.id.btn_back) {
			finish();
		} else if (id == R.id.btn_addhealth){
			if(!isSend) {
				checkAndSendFeedback();
			}
			else {
				showToast("发送中请稍后");
			}
		}
	}
	
	private void checkAndSendFeedback() {
		feedbackContent = feedbackEditText.getText().toString().trim();
		if(feedbackContent.length() < 30 || feedbackContent.length() > 200){
			showToast("反馈信息字数不够或者上限！");
			return;
		}
		if(null == feedbackContent || feedbackContent.equals("")) {
			this.showToast("反馈信息为空，请确认");
		} else {			
			sendFeedbackToServer();
		}
	}
	
	private void sendFeedbackToServer() {
		if (NetWorkUtils.detect(this)) {
			operationsBeforeSend();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("content", feedbackContent);
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("utype", "doctor");
			params.put("channel", "android");
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_feedback,
					FeedbackResultInfo.class ,params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							operationsAfterReceive();
							if (null != response) {
								FeedbackResultInfo info = (FeedbackResultInfo) response;
								if(info.getSuccess() == 1) {
									FeedbackActivity.this.showToast("感谢您的反馈，工作人员将及时跟进");
									FeedbackActivity.this.finish();
								} else {
									FeedbackActivity.this.showToast(info.getErrormsg());
								}
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Message message = handler.obtainMessage();
							message.obj = error.getMessage();
							message.what = IResult.NET_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
//			handler.sendEmptyMessage(IResult.NET_ERROR);
			this.showToast(IMessage.NET_ERROR);
		}
	}
	
	private void operationsBeforeSend() {
		isSend = true;
		btn_addhealth.setTextColor(Color.GRAY);
		loading(null);
	}
	
	private void operationsAfterReceive() {
		isSend = false;
		btn_addhealth.setTextColor(Color.WHITE);
		destroyDialog();
	}
}
